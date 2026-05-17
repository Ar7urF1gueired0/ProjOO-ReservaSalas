import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável por orquestrar a lógica de negócios central das reservas.
 * Ela aplica as políticas de prioridade (Strategy), constrói os objetos com 
 * materiais extras (Decorator) e notifica os interessados (Observer).
 */
public class GerenciadorDeReservas {

    private final RepositorioSingleton repositorio;
    private PoliticaDeReserva politica;
    private final List<ReservaObserver> observadores = new ArrayList<>();

    public GerenciadorDeReservas(PoliticaDeReserva politicaInicial) {
        this.repositorio = RepositorioSingleton.getInstance();
        this.politica = politicaInicial;
    }

    public void setPolitica(PoliticaDeReserva politica) {
        this.politica = politica;
    }

    public void adicionarObservador(ReservaObserver observador) {
        this.observadores.add(observador);
    }

    public void removerObservador(ReservaObserver observador) {
        this.observadores.remove(observador);
    }

    private void notificarObservadoresAlteracao(IReserva original, IReserva modificada) {
        for (ReservaObserver obs : observadores) {
            obs.onReservaAlterada(original, modificada);
        }
    }

    private void notificarObservadoresCriacao(IReserva original) {
        for (ReservaObserver obs : observadores) {
            obs.onReservaCriada(original);
        }
    }

    private void notificarObservadoresCancelamento(IReserva reserva, String motivo) {
        for (ReservaObserver obs : observadores) {
            obs.onReservaCancelada(reserva, motivo);
        }
    }

    /**
     * Cria uma nova reserva, aplica os decoradores solicitados e valida as políticas de conflito.
     * 
     * @param sala A sala selecionada.
     * @param usuarios Lista de usuários (o índice 0 deve ser o organizador).
     * @param data Data da reserva.
     * @param horaInicio Horário de início.
     * @param horaFim Horário de término.
     * @param materiaisExtra Lista de strings com os nomes dos kits extras (ex: "FISICA", "COMPUTADOR").
     * @return IReserva A interface da reserva final, já decorada e validada.
     * @throws ConflitoReservaException Se a política atual barrar a criação por sobreposição de horário.
     */
    public IReserva criarReserva(Sala sala, List<Usuario> usuarios, LocalDate data, LocalTime horaInicio, LocalTime horaFim, List<String> materiaisExtra) throws Exception {
        
        IReserva novaReserva = new Reserva(sala, usuarios, data, horaInicio, horaFim);

        if (materiaisExtra != null && !materiaisExtra.isEmpty()) {
            for (String material : materiaisExtra) {
                switch (material.toUpperCase()) {
                    case "AUDIOVISUAL":
                        novaReserva = new AudiovisualDecorator(novaReserva);
                        break;
                    case "FISICA":
                        novaReserva = new KitFisicaDecorator(novaReserva);
                        break;
                    case "QUIMICA":
                        novaReserva = new KitQuimicaDecorator(novaReserva);
                        break;
                    case "CANETAS":
                        novaReserva = new CanetasDecorator(novaReserva);
                        break;
                    case "COMPUTADOR":
                        novaReserva = new ComputadorDecorator(novaReserva);
                        break;
                }
            }
        }

        List<IReserva> reservasExistentes = RepositorioSingleton.getInstance().getReservas().stream()
                .filter(IReserva::isAtiva)
                .filter(r -> r.getSala().getId() == sala.getId())
                .filter(r -> r.getData().equals(data))
                .collect(Collectors.toList());

        this.politica.validar(novaReserva, reservasExistentes, null);

        for (IReserva existente : reservasExistentes) {
            
            if (novaReserva.getHoraInicio().isBefore(existente.getHoraFim()) && 
                novaReserva.getHoraFim().isAfter(existente.getHoraInicio())) {
                existente.setAtiva(false);
                notificarObservadoresCancelamento(existente, "Sua reserva foi cancelada. A sala precisou ser realocada para um Professor (Prioridade Administrativa).");
            }
        }
        
        RepositorioSingleton.getInstance().registrarReserva(novaReserva);

        notificarObservadoresCriacao(novaReserva);

        return novaReserva;
    }

    public IReserva alterarReserva(IReserva reservaOriginal, Sala novaSala, List<Usuario> novosUsuarios, LocalDate novaData, LocalTime novaHoraInicio, LocalTime novaHoraFim) {
        IReserva reservaModificada = new Reserva(novaSala, novosUsuarios, novaData, novaHoraInicio, novaHoraFim);

        politica.validar(reservaModificada, repositorio.getReservas(), reservaOriginal);
        repositorio.atualizarReserva(reservaOriginal, reservaModificada);

        notificarObservadoresAlteracao(reservaOriginal, reservaModificada);

        return reservaModificada;
    }

    public void cancelarReserva(int idReserva, Usuario usuarioRequisitante, boolean isAdmin) throws Exception {
        // 1. Busca a reserva no repositório
        IReserva reserva = buscarReservaPorId(idReserva);

        if (reserva == null) {
            throw new Exception("Reserva não encontrada com o ID informado.");
        }

        if (!reserva.isAtiva()) {
            throw new Exception("Esta reserva já se encontra cancelada.");
        }

        // 2. Validação de Segurança: Só o organizador ou um Admin podem cancelar
        // Assumindo que o primeiro usuário da lista é sempre o organizador
        boolean isOrganizador = reserva.getUsuarios().get(0).getId() == usuarioRequisitante.getId();
        
        if (!isOrganizador && !isAdmin) {
            throw new SecurityException("Acesso negado: Apenas o organizador ou um administrador podem cancelar esta reserva.");
        }

        // 3. Aplica o Soft Delete (inativa a reserva, mas não a apaga da memória)
        reserva.setAtiva(false);

        // 4. Dispara a notificação para todos os convidados via Observer
        String motivo = (isAdmin && !isOrganizador) ? 
            "Cancelamento Administrativo" : 
            "Cancelada pelo próprio organizador (" + usuarioRequisitante.getNome() + ").";
            
        notificarObservadoresCancelamento(reserva, motivo);
    }

    private IReserva buscarReservaPorId(int id) {
        for (IReserva r : RepositorioSingleton.getInstance().getReservas()) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

}
