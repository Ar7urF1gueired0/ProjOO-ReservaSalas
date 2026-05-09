// Interface para a fábrica de salas
interface SalaFactory {
    Sala criarSala(int id);
}

// Implementações concretas da fábrica para cada tipo de sala
class LaboratorioFactory implements SalaFactory {
    @Override
    public Sala criarSala(int id) {
        return new Laboratorio(id);
    }
}

class SalaEstudoFactory implements SalaFactory {
    @Override
    public Sala criarSala(int id) {
        return new SalaEstudo(id);
    }
}

class SalaAulaFactory implements SalaFactory {
    @Override
    public Sala criarSala(int id) {
        return new SalaAula(id);
    }
}