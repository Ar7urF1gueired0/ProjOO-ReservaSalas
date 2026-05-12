import java.util.List;

// Classes para representar as políticas de reserva e a estratégia de resolução de conflitos

// 
class ConflitoReservaException extends RuntimeException {
    public ConflitoReservaException(String mensagem) {
        super(mensagem);
    }
}

/**
 * Padrão Strategy: Define a interface para as regras de validação de conflito de reservas.
 * Permite que diferentes algoritmos de prioridade sejam aplicados dinamicamente.
 */
public interface PoliticaDeReserva {
    /**
     * Valida se uma nova reserva pode ser criada em relação às reservas já existentes.
     * 
     * @param novaReserva A reserva que está tentando ser registrada.
     * @param reservasExistentes A lista atual de reservas do banco de dados.
     * @param reservaOriginal (Opcional) A reserva que está sendo alterada, para ignorar conflitos consigo mesma.
     * @throws ConflitoReservaException Se a reserva for inválida segundo a política.
     */
    void validar(IReserva novaReserva, List<IReserva> reservasExistentes, IReserva reservaOriginal);
}

class PoliticaPrimeiroAReservar implements PoliticaDeReserva {
    @Override
    public void validar(IReserva novaReserva, List<IReserva> reservasExistentes, IReserva reservaOriginal) {
        for (IReserva existente : reservasExistentes) {
            if (reservaOriginal != null && existente.getId() == reservaOriginal.getId()) {
                continue; // Ignora a própria reserva caso seja uma operação de alteração
            }
            if (houverSobreposicao(novaReserva, existente)) {
                throw new ConflitoReservaException("Conflito de horário com a reserva ID: " + existente.getId());
            }
        }
    }

    protected boolean houverSobreposicao(IReserva r1, IReserva r2) {
        return r1.getSala().getId() == r2.getSala().getId() &&
               r1.getData().equals(r2.getData()) &&
               r1.getHoraInicio().isBefore(r2.getHoraFim()) &&
               r1.getHoraFim().isAfter(r2.getHoraInicio());
    }
}

class PoliticaPrioridadeProfessor extends PoliticaPrimeiroAReservar {
    @Override
    public void validar(IReserva novaReserva, List<IReserva> reservasExistentes, IReserva reservaOriginal) {
        boolean isNovoProfessor = novaReserva.getOrganizador() instanceof Professor;

        for (IReserva existente : reservasExistentes) {
            if (reservaOriginal != null && existente.getId() == reservaOriginal.getId()) {
                continue;
            }
            if (houverSobreposicao(novaReserva, existente)) {
                boolean isExistenteProfessor = existente.getOrganizador() instanceof Professor;
                
                // Exceção à regra de colisão: Novo organizador é Professor e existente é Aluno
                if (!(isNovoProfessor && !isExistenteProfessor)) {
                    throw new ConflitoReservaException("Conflito de horário. Prioridade insuficiente para sobrescrever reserva ID: " + existente.getId());
                }
            }
        }
    }
}
