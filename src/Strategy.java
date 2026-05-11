import java.util.List;

// Classes para representar as políticas de reserva e a estratégia de resolução de conflitos

// 
class ConflitoReservaException extends RuntimeException {
    public ConflitoReservaException(String mensagem) {
        super(mensagem);
    }
}

interface PoliticaDeReserva {
    // A reservaOriginal é repassada para ser ignorada durante a validação de uma alteração
    void validar(Reserva novaReserva, List<Reserva> reservasExistentes, Reserva reservaOriginal);
}

class PoliticaPrimeiroAReservar implements PoliticaDeReserva {
    @Override
    public void validar(Reserva novaReserva, List<Reserva> reservasExistentes, Reserva reservaOriginal) {
        for (Reserva existente : reservasExistentes) {
            if (reservaOriginal != null && existente.getId() == reservaOriginal.getId()) {
                continue; // Ignora a própria reserva caso seja uma operação de alteração
            }
            if (houverSobreposicao(novaReserva, existente)) {
                throw new ConflitoReservaException("Conflito de horário com a reserva ID: " + existente.getId());
            }
        }
    }

    protected boolean houverSobreposicao(Reserva r1, Reserva r2) {
        return r1.getSala().getId() == r2.getSala().getId() &&
               r1.getData().equals(r2.getData()) &&
               r1.getHoraInicio().isBefore(r2.getHoraFim()) &&
               r1.getHoraFim().isAfter(r2.getHoraInicio());
    }
}

class PoliticaPrioridadeProfessor extends PoliticaPrimeiroAReservar {
    @Override
    public void validar(Reserva novaReserva, List<Reserva> reservasExistentes, Reserva reservaOriginal) {
        boolean isNovoProfessor = novaReserva.getOrganizador() instanceof Professor;

        for (Reserva existente : reservasExistentes) {
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