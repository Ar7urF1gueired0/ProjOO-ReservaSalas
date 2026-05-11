public class AudiovisualDecorator extends ReservaDecorator {
    public AudiovisualDecorator(IReserva reserva) {
        super(reserva);
    }

    @Override
    public String getDescricaoItens() {
        return super.getDescricaoItens() + " + Equipamento Audiovisual";
    }
}