// Classe pai para representar uma sala genérica
class Sala {
    private int id;
    private int tamanho;

    public Sala(int id, int tamanho) {
        this.id = id;
        this.tamanho = tamanho;
        
        // Auto-registro no repositório
        RepositorioSingleton.getInstance().registrarSala(this);
    }

    // Getters
    public int getId() { return id; }
    public int getTamanho() { return tamanho; }
}

// Classes filhas para representar tipos específicos de salas
class Laboratorio extends Sala {
    public Laboratorio(int id) {
        super(id, 20);
    }
}

class SalaEstudo extends Sala {
    public SalaEstudo(int id) {
        super(id, 1);
    }
}

class SalaAula extends Sala {
    public SalaAula(int id) {
        super(id, 50);
    }
}