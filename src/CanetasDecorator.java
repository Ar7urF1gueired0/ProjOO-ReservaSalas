public class CanetasDecorator extends ReservaDecorator {
    public CanetasDecorator(IReserva reserva) {
        super(reserva);
    }

    @Override
    public String getDescricaoItens() {
        return super.getDescricaoItens() + " + Kit de Física";
    }
}