import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReserva {
    int getId();
    Sala getSala();
    List<Usuario> getUsuarios();
    LocalDate getData();
    LocalTime getHoraInicio();
    LocalTime getHoraFim();
    Usuario getOrganizador();
    String getOrganizadorNome();
    boolean isAtiva();
    void setAtiva(boolean ativa);
    String getStatus();
    // Método novo para retornar o que foi reservado
    String getDescricaoItens(); 
}