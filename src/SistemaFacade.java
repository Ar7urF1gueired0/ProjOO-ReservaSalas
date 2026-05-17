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
    private ISistemaAdmin adminProxy;
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
        for (Usuario u : RepositorioSingleton.getInstance().getUsuarios()) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                this.usuarioLogado = u;
                this.adminProxy = new SistemaAdminProxy(this.usuarioLogado); 
                return true;
            }
        }
        return false;
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public void realizarLogout() {
        this.usuarioLogado = null;
        this.adminProxy = null;
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
            IReserva conflito = repositorio.getReservas().stream()
                    .filter(IReserva::isAtiva)        
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

    public List<IReserva> listarReservasGerenciaveis() {
        validarSessao();
        boolean isAdmin = this.usuarioLogado instanceof Professor;
        
        if (isAdmin) {
            return repositorio.getReservas().stream()
                    .filter(IReserva::isAtiva)
                    .collect(Collectors.toList());
        } else {
            return consultarMeuHistorico().stream()
                    .filter(IReserva::isAtiva)
                    .collect(Collectors.toList());
        }
    }

    public List<ReservaUsuarioDTO> listarReservasUsuarioAtivo() {
        if (this.usuarioLogado == null) return Collections.emptyList();

        return repositorio.getReservas().stream()
                .filter(r -> r.getUsuarios().contains(this.usuarioLogado))
                .map(r -> new ReservaUsuarioDTO(r, r.getOrganizador().getId() == this.usuarioLogado.getId()))
                .collect(Collectors.toList());
    }

    // --- Operações de Reserva (CRUD) ---

    public IReserva criarReserva(int idSala, List<Usuario> convidados, LocalDate data, LocalTime horaInicio, LocalTime horaFim, List<String> materiaisExtra) throws Exception {
        validarSessao();

        Sala sala = repositorio.getSalas().stream()
                .filter(s -> s.getId() == idSala)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada."));

        List<Usuario> usuariosReserva = montarListaUsuarios(convidados);

        return gerenciador.criarReserva(sala, usuariosReserva, data, horaInicio, horaFim, materiaisExtra);
    }

    public IReserva alterarReserva(int idReserva, List<Usuario> convidados, LocalDate novaData, LocalTime novoInicio, LocalTime novoFim) throws Exception{
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

        boolean isAdmin = this.usuarioLogado instanceof Professor;
        
        if (reserva.getOrganizador().getId() != this.usuarioLogado.getId() && !isAdmin) {
            throw new SecurityException("Acesso negado: Apenas o organizador ou um Admin podem modificar esta reserva.");
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

    public void cadastrarUsuarioAdmin(int tipo, String nome, String email, String senha) {
        validarSessao();
        adminProxy.cadastrarUsuario(tipo, nome, email, senha);
    }

    public void cadastrarSalaAdmin(int tipo, int idSala) {
        validarSessao();
        adminProxy.cadastrarSala(tipo, idSala);
    }

    public List<IReserva> consultarMeuHistorico() {
        validarSessao();
        int meuId = this.usuarioLogado.getId();
        
        // Busca direto no repositório usando o próprio ID
        return RepositorioSingleton.getInstance().getReservas().stream()
                .filter(reserva -> reserva.getUsuarios().stream()
                                        .anyMatch(u -> u.getId() == meuId))
                .collect(Collectors.toList());
        }

    public List<IReserva> consultarHistoricoUsuarioAdmin(int idBusca) {
        validarSessao();
        return adminProxy.consultarHistoricoUsuario(idBusca);
    }

    public void cancelarReserva(int idReserva) throws Exception {
        validarSessao();
        
        boolean isAdmin = this.usuarioLogado instanceof Professor;
        
        gerenciador.cancelarReserva(idReserva, this.usuarioLogado, isAdmin);
    }
}