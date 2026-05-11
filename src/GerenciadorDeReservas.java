public class GerenciadorDeReservas {

    private PoliticaDeReserva politica;

    public GerenciadorDeReservas(PoliticaDeReserva politicaInicial) {
        this.politica = politicaInicial;
    }

    public void setPolitica(PoliticaDeReserva politica) {
        this.politica = politica;
    }
}
