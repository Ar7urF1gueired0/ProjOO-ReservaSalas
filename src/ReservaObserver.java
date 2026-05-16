import java.util.List;

// Arquivos Observer: Interface e Implementação

interface ReservaObserver {
    void onReservaAlterada(IReserva reservaOriginal, IReserva reservaModificada);
    void onReservaCriada(IReserva reserva);
    void onReservaCancelada(IReserva reservaAntiga, String motivo);
}

class ServicoNotificacaoEmail implements ReservaObserver {
    @Override
    public void onReservaAlterada(IReserva reservaOriginal, IReserva reservaModificada) {
        List<Usuario> usuariosEnvolvidos = reservaModificada.getUsuarios();
        
        System.out.println("--- Iniciando Envio de Notificações ---");
        for (Usuario u : usuariosEnvolvidos) {
            System.out.printf("Para: %s (%s)%n", u.getNome(), u.getEmail());
            System.out.printf("Mensagem: Olá %s, a reserva da sala %d para o dia %s foi alterada.%n", 
                              u.getNome(), reservaModificada.getSala().getId(), reservaModificada.getData());
            System.out.println("---------------------------------------");
        }
    }

    @Override
    public void onReservaCriada(IReserva reserva){
        List<Usuario> usuariosEnvolvidos = reserva.getUsuarios();

        for (Usuario u : usuariosEnvolvidos){
            System.out.printf("Para: %s (%s)%n", u.getNome(), u.getEmail());
            System.out.printf("Mensagem: Olá %s, você tem uma nova reserva da sala %d para o dia %s. [Organizador: %s]%n", 
                              u.getNome(), reserva.getSala().getId(), reserva.getData(), reserva.getOrganizadorNome());
            System.out.println("---------------------------------------");
        }

    }

    @Override
    public void onReservaCancelada(IReserva reserva, String motivo){
        for (Usuario u : reserva.getUsuarios()) {
            System.out.println("--- ALERTA DE CANCELAMENTO ---");
            System.out.printf("Para: %s (%s)%n", u.getNome(), u.getEmail());
            System.out.printf("Sua reserva da sala %d no dia %s foi CANCELADA.%n", reserva.getSala().getId(), reserva.getData());
            System.out.printf("Motivo: %s%n", motivo);
            System.out.println("---------------------------------------");
        }
    }

}