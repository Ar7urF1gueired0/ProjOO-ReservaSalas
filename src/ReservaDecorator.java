import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public abstract class ReservaDecorator implements IReserva {
    protected IReserva reservaEmbrulhada;

    public ReservaDecorator(IReserva reserva) {
        this.reservaEmbrulhada = reserva;
    }

    @Override
    public int getId() { return reservaEmbrulhada.getId(); }

    @Override
    public Sala getSala() { return reservaEmbrulhada.getSala(); }

    @Override
    public List<Usuario> getUsuarios() { return reservaEmbrulhada.getUsuarios(); }

    @Override
    public LocalDate getData() { return reservaEmbrulhada.getData(); }

    @Override
    public LocalTime getHoraInicio() { return reservaEmbrulhada.getHoraInicio(); }

    @Override
    public LocalTime getHoraFim() { return reservaEmbrulhada.getHoraFim(); }

    @Override
    public Usuario getOrganizador() { return reservaEmbrulhada.getOrganizador(); }

    @Override
    public String getDescricaoItens() { 
        return reservaEmbrulhada.getDescricaoItens(); 
    }
}