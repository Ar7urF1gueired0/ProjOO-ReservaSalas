import java.util.List;

// Arquivos Observer: Interface e Implementação

interface ReservaObserver {
    void onReservaAlterada(Reserva reservaOriginal, Reserva reservaModificada);
}

class ServicoNotificacaoEmail implements ReservaObserver {
    @Override
    public void onReservaAlterada(Reserva reservaOriginal, Reserva reservaModificada) {
        List<Usuario> usuariosEnvolvidos = reservaModificada.getUsuarios();
        
        System.out.println("--- Iniciando Envio de Notificações ---");
        for (Usuario u : usuariosEnvolvidos) {
            System.out.printf("Para: %s (%s)%n", u.getNome(), u.getEmail());
            System.out.printf("Mensagem: Olá %s, a reserva da sala %d para o dia %s foi alterada.%n", 
                              u.getNome(), reservaModificada.getSala().getId(), reservaModificada.getData());
            System.out.println("---------------------------------------");
        }
    }
}