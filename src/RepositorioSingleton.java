import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Classe para representar o repositório de dados usando o padrão Singleton
public class RepositorioSingleton {
    private static volatile RepositorioSingleton instancia;

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Sala> salas = new ArrayList<>();
    private final List<IReserva> reservas = new ArrayList<>();

    private RepositorioSingleton() {}

    // Implementação do padrão Singleton com verificação de instância única
    public static RepositorioSingleton getInstance() {
        if (instancia == null) {
            synchronized (RepositorioSingleton.class) {
                if (instancia == null) {
                    instancia = new RepositorioSingleton();
                }
            }
        }
        return instancia;
    }

    // Métodos para registrar usuários, salas e reservas
    void registrarUsuario(Usuario u) { this.usuarios.add(u); }
    void registrarSala(Sala s) { this.salas.add(s); }
    void registrarReserva(IReserva r) { this.reservas.add(r); }
    void atualizarReserva(IReserva original, IReserva modificada) { this.reservas.remove(original); this.reservas.add(modificada); }
    public void removerReserva(IReserva r) { this.reservas.remove(r); }

    // Métodos para obter listas de usuários, salas e reservas
    public List<Usuario> getUsuarios() { return Collections.unmodifiableList(usuarios); }
    public List<Sala> getSalas() { return Collections.unmodifiableList(salas); }
    public List<IReserva> getReservas() { return Collections.unmodifiableList(reservas); }
}