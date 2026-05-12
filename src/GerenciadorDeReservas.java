import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    private void notificarObservadores(IReserva original, IReserva modificada) {
        for (ReservaObserver obs : observadores) {
            obs.onReservaAlterada(original, modificada);
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
    public IReserva criarReserva(Sala sala, List<Usuario> usuarios, LocalDate data, LocalTime horaInicio, LocalTime horaFim, List<String> materiaisExtra) {
        
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

        politica.validar(novaReserva, repositorio.getReservas(), null);
        repositorio.registrarReserva(novaReserva);

        return novaReserva;
    }

    public IReserva alterarReserva(IReserva reservaOriginal, Sala novaSala, List<Usuario> novosUsuarios, LocalDate novaData, LocalTime novaHoraInicio, LocalTime novaHoraFim) {
        IReserva reservaModificada = new Reserva(novaSala, novosUsuarios, novaData, novaHoraInicio, novaHoraFim);

        politica.validar(reservaModificada, repositorio.getReservas(), reservaOriginal);
        repositorio.atualizarReserva(reservaOriginal, reservaModificada);

        notificarObservadores(reservaOriginal, reservaModificada);

        return reservaModificada;
    }
}
