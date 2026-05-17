import java.util.stream.Collectors;
import java.util.List;

public class SistemaAdminReal implements ISistemaAdmin {

    @Override
    public void cadastrarUsuario(int tipo, String nome, String email, String senha) {
        // Tipo 1: Aluno, Tipo 2: Professor
        if (tipo == 2) {
            new Professor(nome, email, senha); // O construtor já faz o auto-registro no Singleton
        } else {
            new Aluno(nome, email, senha);
        }
    }

    @Override
    public void cadastrarSala(int tipo, int idSala) {
        SalaFactory factory;
        if (tipo == 1) {
            factory = new LaboratorioFactory();
        } else if (tipo == 2) {
            factory = new SalaAulaFactory();
        } else {
            factory = new SalaEstudoFactory();
        }
        
        factory.criarSala(idSala);
    }

    @Override
    public List<IReserva> consultarHistoricoUsuario(int idBusca){
        return RepositorioSingleton.getInstance().getReservas().stream()
            .filter(reserva -> reserva.getUsuarios().stream().anyMatch(u -> u.getId() == idBusca))
            .collect(Collectors.toList());
    }
}