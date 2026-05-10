import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Classe para representar o repositório de dados usando o padrão Singleton
public class RepositorioSingleton {
    private static volatile RepositorioSingleton instancia;

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Sala> salas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();

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

    // Métodos para obter listas de usuários, salas e reservas
    public List<Usuario> getUsuarios() { return Collections.unmodifiableList(usuarios); }
    public List<Sala> getSalas() { return Collections.unmodifiableList(salas); }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }
}