import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    private void notificarObservadores(Reserva original, Reserva modificada) {
        for (ReservaObserver obs : observadores) {
            obs.onReservaAlterada(original, modificada);
        }
    }

    public Reserva criarReserva(Sala sala, List<Usuario> usuarios, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        Reserva novaReserva = new Reserva(sala, usuarios, data, horaInicio, horaFim);

        politica.validar(novaReserva, repositorio.getReservas(), null);
        repositorio.registrarReserva(novaReserva);

        return novaReserva;
    }

    public Reserva alterarReserva(Reserva reservaOriginal, Sala novaSala, List<Usuario> novosUsuarios, LocalDate novaData, LocalTime novaHoraInicio, LocalTime novaHoraFim) {
        Reserva reservaModificada = new Reserva(novaSala, novosUsuarios, novaData, novaHoraInicio, novaHoraFim);

        politica.validar(reservaModificada, repositorio.getReservas(), reservaOriginal);
        repositorio.atualizarReserva(reservaOriginal, reservaModificada);

        notificarObservadores(reservaOriginal, reservaModificada);

        return reservaModificada;
    }
}
