import java.util.List;

public class SistemaAdminProxy implements ISistemaAdmin {
    private SistemaAdminReal adminReal;
    private Usuario usuarioLogado;

    public SistemaAdminProxy(Usuario usuarioLogado) {
        this.adminReal = new SistemaAdminReal();
        this.usuarioLogado = usuarioLogado;
    }

    private void verificarPermissao() {
        if (usuarioLogado == null) {
            throw new SecurityException("Acesso negado: Nenhum usuário logado.");
        }
        if (!(usuarioLogado instanceof Professor)) {
            throw new SecurityException("Acesso negado: Operação restrita a Administradores (Professores).");
        }
    }

    @Override
    public void cadastrarUsuario(int tipo, String nome, String email, String senha) {
        verificarPermissao();
        adminReal.cadastrarUsuario(tipo, nome, email, senha);
    }

    @Override
    public void cadastrarSala(int tipo, int idSala) {
        verificarPermissao();
        adminReal.cadastrarSala(tipo, idSala);
    }

    @Override
    public List<IReserva> consultarHistoricoUsuario (int idBusca){
        verificarPermissao();
        return adminReal.consultarHistoricoUsuario(idBusca);
    }
}