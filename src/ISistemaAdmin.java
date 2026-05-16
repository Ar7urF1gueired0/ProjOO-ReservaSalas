import java.util.List;

public interface ISistemaAdmin {
    void cadastrarUsuario(int tipo, String nome, String email, String senha);
    void cadastrarSala(int tipo, int idSala);
    List<IReserva> consultarHistoricoUsuario(int idBusca);
}