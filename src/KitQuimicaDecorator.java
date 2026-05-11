public class KitQuimicaDecorator extends ReservaDecorator {
    public KitQuimicaDecorator(IReserva reserva) {
        super(reserva);
    }

    @Override
    public String getDescricaoItens() {
        return super.getDescricaoItens() + " + Kit de Química";
    }
}