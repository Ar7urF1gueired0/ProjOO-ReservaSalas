import java.time.LocalDate;
import java.util.List;

class Reserva {
    private static int contadorId = 1;
    
    private int id;
    private Sala sala;
    private List<Usuario> usuarios;
    private LocalDate data;

    public Reserva(Sala sala, List<Usuario> usuarios, LocalDate data) {
        if (usuarios == null || usuarios.isEmpty()) {
            throw new IllegalArgumentException("A reserva requer pelo menos um usuário (organizador).");
        }
        if (usuarios.size() > sala.getTamanho()) {
            throw new IllegalArgumentException("A quantidade de usuários excede a capacidade máxima da sala.");
        }
        
        this.id = contadorId++;
        this.sala = sala;
        this.usuarios = usuarios;
        this.data = data;
    }

    public int getId() { return id; }
    public Sala getSala() { return sala; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public LocalDate getData() { return data; }

    public Usuario getOrganizador() {
        return usuarios.get(0);
    }
}