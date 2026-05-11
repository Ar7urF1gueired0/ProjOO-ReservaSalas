import java.util.ArrayList;
import java.util.List;

public class GerenciadorDeReservas {

    private PoliticaDeReserva politica;
    private final List<ReservaObserver> observadores = new ArrayList<>();

    public GerenciadorDeReservas(PoliticaDeReserva politicaInicial) {
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
}
