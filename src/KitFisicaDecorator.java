public class KitFisicaDecorator extends ReservaDecorator {
    public KitFisicaDecorator(IReserva reserva) {
        super(reserva);
    }

    @Override
    public String getDescricaoItens() {
        return super.getDescricaoItens() + " + Kit de Física";
    }
}