import java.util.ArrayList;
import java.util.List;

public interface ChatMediator {
    void enviarMensagem(String mensagem, Usuario remetente);
    void adicionarUsuario(Usuario usuario);
}

class ChatMediatorImpl implements ChatMediator {
    private static final ChatMediatorImpl INSTANCE = new ChatMediatorImpl();
    private final List<Usuario> usuarios = new ArrayList<>();

    private ChatMediatorImpl() {}

    public static ChatMediatorImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void adicionarUsuario(Usuario usuario) {
        if (!usuarios.contains(usuario)) {
            usuarios.add(usuario);
        }
    }

    @Override
    public void enviarMensagem(String mensagem, Usuario remetente) {
        // iterar sobre uma cópia para evitar ConcurrentModification
        for (Usuario usuario : new ArrayList<>(usuarios)) {
            if (usuario != remetente) {
                usuario.receberMensagem(mensagem, remetente);
            }
        }
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
