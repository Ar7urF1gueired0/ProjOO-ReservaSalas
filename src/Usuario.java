// Classe pai para representar um usuário genérico
class Usuario {
    private static int contadorId = 1;

    private int id;
    private String nome;
    private String email;
    private String senha;

    public Usuario(String nome, String email, String senha) {
        this.id = contadorId++;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        
        // Auto-registro no repositório
        RepositorioSingleton.getInstance().registrarUsuario(this);
        // Registrar no mediator de chat (singleton) para receber/propagar mensagens
        ChatMediatorImpl.getInstance().adicionarUsuario(this);
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    // Enviar mensagem via mediator
    public void enviarMensagem(String mensagem) {
        System.out.println(this.nome + " enviou: " + mensagem);
        ChatMediatorImpl.getInstance().enviarMensagem(mensagem, this);
    }

    // Receber mensagem do mediator
    public void receberMensagem(String mensagem, Usuario remetente) {
        System.out.println(this.nome + " recebeu de " + remetente.getNome() + ": " + mensagem);
    }

}

// Classes filhas para representar tipos específicos de usuários
class Aluno extends Usuario {
    public Aluno(String nome, String email, String senha) {
        super(nome, email, senha);
     }
}

class Professor extends Usuario {
    public Professor(String nome, String email, String senha) {
        super(nome, email, senha);
     }
}