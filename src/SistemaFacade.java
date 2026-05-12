import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Padrão Facade: Ponto de entrada principal do sistema de reservas.
 * Esta classe isola a complexidade das regras de negócio (Gerenciador, Políticas, Repositório)
 * e fornece uma interface simplificada para a interação com o usuário (Main).
 */
public class SistemaFacade {

    private final RepositorioSingleton repositorio;
    private final GerenciadorDeReservas gerenciador;
    private Usuario usuarioLogado;
    private String nomePoliticaAtiva = "Primeiro a Reservar";
    public static final LocalDate DIA_ATUAL = LocalDate.now();

    public SistemaFacade() {
        this.repositorio = RepositorioSingleton.getInstance();
        
        // Setup estrito: Política padrão e registro do observer
        this.gerenciador = new GerenciadorDeReservas(new PoliticaPrimeiroAReservar());
        this.gerenciador.adicionarObservador(new ServicoNotificacaoEmail());
    }

    // --- Gestão de Sessão ---
    /**
     * Tenta realizar o login do usuário no sistema buscando suas credenciais no repositório.
     * @param email Email do usuário.
     * @param senha Senha do usuário.
     * @return true se as credenciais forem válidas, false caso contrário.
     */
    public boolean fazerLogin(String email, String senha) {
        this.usuarioLogado = repositorio.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha))
                .findFirst()
                .orElse(null);

        return this.usuarioLogado != null;
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public void realizarLogout() {
        this.usuarioLogado = null;
    }

    // --- Configuração de Domínio ---

    public String getNomePoliticaAtiva() {
        return this.nomePoliticaAtiva;
    }

    public void alterarRegraDePrioridade(PoliticaDeReserva novaPolitica, String nomePolitica) {
        if (novaPolitica == null) {
            throw new IllegalArgumentException("A política de reserva não pode ser nula.");
        }
        this.gerenciador.setPolitica(novaPolitica);
        this.nomePoliticaAtiva = nomePolitica;
    }

    // --- DTOs para Projeção de Dados ---
    
    public static class DisponibilidadeSalaDTO {
        public final Sala sala;
        public final boolean ocupadaPorAluno;

        public DisponibilidadeSalaDTO(Sala sala, boolean ocupadaPorAluno) {
            this.sala = sala;
            this.ocupadaPorAluno = ocupadaPorAluno;
        }
    }

    public static class ReservaUsuarioDTO {
        public final IReserva reserva;
        public final boolean isOrganizador;

        public ReservaUsuarioDTO(IReserva reserva, boolean isOrganizador) {
            this.reserva = reserva;
            this.isOrganizador = isOrganizador;
        }
    }

    // --- Implementação das Funcionalidades ---

    public List<DisponibilidadeSalaDTO> listarSalasDisponiveis(LocalDate data, LocalTime inicio, LocalTime fim) {
        if (this.usuarioLogado == null) return Collections.emptyList();

        List<DisponibilidadeSalaDTO> disponiveis = new ArrayList<>();
        boolean isProfessor = this.usuarioLogado instanceof Professor;

        for (Sala sala : repositorio.getSalas()) {
            // Busca se existe alguma reserva conflitante para esta sala no horário especificado
            IReserva conflito = repositorio.getReservas().stream()
                    .filter(r -> r.getSala().getId() == sala.getId() &&
                                 r.getData().equals(data) &&
                                 r.getHoraInicio().isBefore(fim) &&
                                 r.getHoraFim().isAfter(inicio))
                    .findFirst()
                    .orElse(null);

            if (conflito == null) {
                // Sala totalmente livre
                disponiveis.add(new DisponibilidadeSalaDTO(sala, false));
            } else if (isProfessor && conflito.getOrganizador() instanceof Aluno) {
                // Sala com conflito, mas liberada pela regra de prioridade do Professor
                disponiveis.add(new DisponibilidadeSalaDTO(sala, true));
            }
        }

        return disponiveis;
    }

    public List<ReservaUsuarioDTO> listarReservasUsuarioAtivo() {
        if (this.usuarioLogado == null) return Collections.emptyList();

        return repositorio.getReservas().stream()
                .filter(r -> r.getUsuarios().contains(this.usuarioLogado))
                .map(r -> new ReservaUsuarioDTO(r, r.getOrganizador().getId() == this.usuarioLogado.getId()))
                .collect(Collectors.toList());
    }

    // --- Operações de Reserva (CRUD) ---

    public IReserva criarReserva(int idSala, List<Usuario> convidados, LocalDate data, LocalTime horaInicio, LocalTime horaFim, List<String> materiaisExtra) {
        validarSessao();

        Sala sala = repositorio.getSalas().stream()
                .filter(s -> s.getId() == idSala)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada."));

        List<Usuario> usuariosReserva = montarListaUsuarios(convidados);

        return gerenciador.criarReserva(sala, usuariosReserva, data, horaInicio, horaFim, materiaisExtra);
    }

    public boolean deletarReserva(int idReserva) {
        if (this.usuarioLogado == null) return false;

        IReserva reserva = buscarReservaValidandoAutorizacao(idReserva);
        repositorio.removerReserva(reserva);
        
        return true;
    }

    public IReserva alterarReserva(int idReserva, List<Usuario> convidados, LocalDate novaData, LocalTime novoInicio, LocalTime novoFim) {
        validarSessao();

        IReserva reservaOriginal = buscarReservaValidandoAutorizacao(idReserva);

        Sala sala = reservaOriginal.getSala();

        List<Usuario> usuariosReserva = montarListaUsuarios(convidados);

        return gerenciador.alterarReserva(reservaOriginal, sala, usuariosReserva, novaData, novoInicio, novoFim);
    }

    // --- Métodos Auxiliares Internos ---

    private void validarSessao() {
        if (this.usuarioLogado == null) {
            throw new IllegalStateException("Operação requer um usuário logado.");
        }
    }

    private IReserva buscarReservaValidandoAutorizacao(int idReserva) {
        IReserva reserva = repositorio.getReservas().stream()
                .filter(r -> r.getId() == idReserva)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada."));

        if (reserva.getOrganizador().getId() != this.usuarioLogado.getId()) {
            throw new SecurityException("Acesso negado: Apenas o organizador pode modificar esta reserva.");
        }
        return reserva;
    }

    private List<Usuario> montarListaUsuarios(List<Usuario> convidados) {
        List<Usuario> listaCompleta = new ArrayList<>();
        listaCompleta.add(this.usuarioLogado); // Garante o logado no índice 0 (Organizador)
        if (convidados != null) {
            listaCompleta.addAll(convidados);
        }
        return listaCompleta;
    }

    public List<IReserva> gerarRelatorioDiario() {
        return repositorio.getReservas().stream()
                .filter(r -> r.getData().equals(DIA_ATUAL))
                .collect(Collectors.toList());
    }

    public List<Usuario> listarTodosUsuarios() {
        return repositorio.getUsuarios();
    }
}