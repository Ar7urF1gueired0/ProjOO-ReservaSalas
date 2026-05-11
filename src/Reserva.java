import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Classe para representar uma reserva de sala
class Reserva {
    private static int contadorId = 1;
    
    private int id;
    private Sala sala;
    private List<Usuario> usuarios;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Reserva(Sala sala, List<Usuario> usuarios, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        if (usuarios == null || usuarios.isEmpty()) {
            throw new IllegalArgumentException("A reserva requer pelo menos um usuário (organizador).");
        }
        if (usuarios.size() > sala.getTamanho()) {
            throw new IllegalArgumentException("A quantidade de usuários excede a capacidade máxima da sala.");
        }
        if (horaInicio == null || horaFim == null || !horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }
        
        this.id = contadorId++;
        this.sala = sala;
        this.usuarios = usuarios;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    // Getters
    public int getId() { return id; }
    public Sala getSala() { return sala; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public LocalDate getData() { return data; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }

    public Usuario getOrganizador() {
        return usuarios.get(0);
    }
}