public class ComputadorDecorator extends ReservaDecorator {
    public ComputadorDecorator(IReserva reserva) {
        super(reserva);
    }

    @Override
    public String getDescricaoItens() {
        return super.getDescricaoItens() + " + Kit de Física";
    }
}