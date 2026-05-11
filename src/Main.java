import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SistemaFacade facade = new SistemaFacade();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        carregarDadosMock();
        executarLoopInteracao();
    }

    private static void carregarDadosMock() {
        // Mock Usuários (5 Alunos, 3 Professores)
        new Aluno("Aluno Um", "a1@email.com", "123");
        new Aluno("Aluno Dois", "a2@email.com", "123");
        new Aluno("Aluno Três", "a3@email.com", "123");
        new Aluno("Aluno Quatro", "a4@email.com", "123");
        new Aluno("Aluno Cinco", "a5@email.com", "123");
        
        new Professor("Prof Um", "p1@email.com", "123");
        new Professor("Prof Dois", "p2@email.com", "123");
        new Professor("Prof Três", "p3@email.com", "123");

        // Mock Salas via Factory (3 de cada)
        SalaFactory labFactory = new LaboratorioFactory();
        SalaFactory estudoFactory = new SalaEstudoFactory();
        SalaFactory aulaFactory = new SalaAulaFactory();

        for (int i = 1; i <= 3; i++) {
            labFactory.criarSala(100 + i);    // IDs 101, 102, 103
            estudoFactory.criarSala(200 + i); // IDs 201, 202, 203
            aulaFactory.criarSala(300 + i);   // IDs 301, 302, 303
        }
        System.out.println("Sistema inicializado. Dados mock carregados com sucesso.\n");
    }

    private static void executarLoopInteracao() {
        boolean executando = true;

        while (executando) {
            garantirAutenticacao(); // Bloqueia o acesso até o login

            // Adicionada quebra de linha inicial para espaçar as iterações do menu principal
            System.out.println("\n=============================================");
            System.out.println("Usuário Logado: " + facade.getUsuarioLogado().getNome());
            System.out.println("1. Alterar Conta (Logout)");
            System.out.println("2. Alterar Regra de Prioridade");
            System.out.println("3. Gerenciar Reservas");
            System.out.println("4. Listar Minhas Reservas");
            System.out.println("5. Relatório Diário");
            System.out.println("0. Sair");

            int opcao = lerInteiro("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1:
                        facade.realizarLogout();
                        System.out.println("\nLogout realizado.");
                        break;
                    case 2:
                        System.out.println("\nPolítica Atual Ativa: " + facade.getNomePoliticaAtiva());
                        System.out.println("1 - Primeiro a Reservar | 2 - Prioridade Professor");
                        int pol = lerInteiro("Opção: ");
                        if (pol == 1) {
                            facade.alterarRegraDePrioridade(new PoliticaPrimeiroAReservar(), "Primeiro a Reservar");
                            System.out.println("Política alterada para: Primeiro a Reservar.");
                        } else if (pol == 2) {
                            facade.alterarRegraDePrioridade(new PoliticaPrioridadeProfessor(), "Prioridade Professor");
                            System.out.println("Política alterada para: Prioridade Professor.");
                        } else {
                            System.out.println("Opção inválida.");
                        }
                        break;
                    case 3:
                        abrirMenuGerenciarReservas();
                        break;
                    case 4:
                        System.out.println("\n--- Minhas Reservas ---");
                        List<SistemaFacade.ReservaUsuarioDTO> minhas = facade.listarReservasUsuarioAtivo();
                        for (SistemaFacade.ReservaUsuarioDTO dto : minhas) {
                            String papel = dto.isOrganizador ? "Organizador" : "Convidado";
                            System.out.printf("ID Reserva: %d | Sala: %d | Data: %s | Horário: %s - %s | Papel: %s%n",
                                    dto.reserva.getId(), dto.reserva.getSala().getId(), dto.reserva.getData(),
                                    dto.reserva.getHoraInicio(), dto.reserva.getHoraFim(), papel);
                        }
                        break;
                    case 5:
                        System.out.println("\n--- Relatório Diário (" + SistemaFacade.DIA_ATUAL.format(DATE_FORMAT) + ") ---");
                        List<IReserva> relatorio = facade.gerarRelatorioDiario();
                        if (relatorio.isEmpty()) {
                            System.out.println("Nenhuma reserva para o dia de hoje.");
                        } else {
                            for (IReserva r : relatorio) {
                                System.out.printf("ID: %d | Sala: %d | Horário: %s - %s | Organizador: %s%n",
                                        r.getId(), r.getSala().getId(), r.getHoraInicio(), r.getHoraFim(), r.getOrganizador().getNome());
                            }
                        }
                        break;
                    case 0:
                        executando = false;
                        System.out.println("\nEncerrando o sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro na operação: " + e.getMessage());
            }
        }
    }

    // --- Métodos de Controle de Sessão e Menus ---

    private static void garantirAutenticacao() {
        while (facade.getUsuarioLogado() == null) {
            System.out.println("\n=== Sistema de Reserva de Salas ===");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            
            if (!facade.fazerLogin(email, senha)) {
                System.out.println("Credenciais inválidas. Tente novamente.");
            } else {
                System.out.println("Login realizado com sucesso!");
            }
        }
    }

    private static void abrirMenuGerenciarReservas() {
        boolean noSubmenu = true;
        while (noSubmenu) {
            System.out.println("\n--- Gerenciar Reservas ---");
            System.out.println("1. Criar Reserva");
            System.out.println("2. Alterar Reserva");
            System.out.println("3. Deletar Reserva");
            System.out.println("0. Voltar");
            
            int opcao = lerInteiro("Escolha uma opção: ");
            try {
                switch (opcao) {
                    case 1: fluxoCriarReserva(); break;
                    case 2: fluxoAlterarReserva(); break;
                    case 3:
                        int idDel = lerInteiro("\nID da Reserva a deletar: ");
                        if (facade.deletarReserva(idDel)) System.out.println("Reserva deletada.");
                        break;
                    case 0: noSubmenu = false; break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro na operação: " + e.getMessage());
            }
        }
    }

    // --- Fluxos Progressivos ---

    private static void fluxoCriarReserva() {
        System.out.println();
        LocalDate data;
        while (true) {
            data = lerData("Data (dd/MM/yyyy): ");
            if (data.isBefore(SistemaFacade.DIA_ATUAL)) {
                System.out.println("Erro: A data da reserva não pode ser no passado.");
            } else {
                break;
            }
        }
        
        LocalTime inicio = lerHora("Hora Início (HH:mm): ");
        LocalTime fim = lerHora("Hora Fim (HH:mm): ");

        List<SistemaFacade.DisponibilidadeSalaDTO> disponiveis = facade.listarSalasDisponiveis(data, inicio, fim);
        if (disponiveis.isEmpty()) {
            System.out.println("\nNenhuma sala disponível para os parâmetros informados.");
            return;
        }

        Map<String, List<SistemaFacade.DisponibilidadeSalaDTO>> agrupadas = disponiveis.stream()
                .collect(Collectors.groupingBy(d -> d.sala.getClass().getSimpleName()));

        System.out.println("\n--- Salas Disponíveis (Segregadas por Tipo) ---");
        agrupadas.forEach((tipo, salas) -> {
            System.out.println(tipo + " | Capacidade: " + salas.get(0).sala.getTamanho());
            salas.forEach(dto -> {
                String obs = dto.ocupadaPorAluno ? " [ATENÇÃO: Ocupada por Aluno - Permitido Sobrescrever]" : "";
                System.out.println("  -> ID: " + dto.sala.getId() + obs);
            });
        });

        int idSala = lerInteiro("\nID da Sala escolhida: ");

        List<String> materiaisExtra = new ArrayList<>();
        String tipoSala = agrupadas.values().stream()
            .flatMap(List::stream)
            .filter(d -> d.sala.getId() == idSala)
            .map(d -> d.sala.getClass().getSimpleName())
            .findFirst().orElse("");

        if (tipoSala.equals("Laboratorio")) {
            System.out.println("\nVocê deseja reservar materiais extras?");
            System.out.println("1. Kit de equipamentos de física");
            System.out.println("2. Kit de equipamentos de química");
            List<Integer> escolhas = lerEscolhasMultiplas("Responda com números separados por vírgula: ");
            if (escolhas.contains(1)) materiaisExtra.add("FISICA");
            if (escolhas.contains(2)) materiaisExtra.add("QUIMICA");

        } else if (tipoSala.equals("SalaAula")) {
            System.out.println("\nVocê deseja reservar materiais extras?");
            System.out.println("1. Kit de canetas de quadro branco");
            System.out.println("2. Equipamentos de audiovisual");
            List<Integer> escolhas = lerEscolhasMultiplas("Responda com números separados por vírgula: ");
            if (escolhas.contains(1)) materiaisExtra.add("CANETAS");
            if (escolhas.contains(2)) materiaisExtra.add("AUDIOVISUAL");

        } else if (tipoSala.equals("SalaEstudo")) {
            System.out.println("\nVocê deseja reservar materiais extras?");
            System.out.println("1. Kit de canetas de quadro branco");
            System.out.println("2. Computador para estudo");
            List<Integer> escolhas = lerEscolhasMultiplas("Responda com números separados por vírgula: ");
            if (escolhas.contains(1)) materiaisExtra.add("CANETAS");
            if (escolhas.contains(2)) materiaisExtra.add("COMPUTADOR");
        }

        System.out.println("\n--- Usuários Registrados ---");
        facade.listarTodosUsuarios().forEach(u -> System.out.println("ID: " + u.getId() + " | Nome: " + u.getNome()));
        
        List<Usuario> convidados = lerConvidadosSeguro("IDs dos Convidados (separados por vírgula, ou vazio): ");

        IReserva nova = facade.criarReserva(idSala, convidados, data, inicio, fim, materiaisExtra);
        System.out.println("\nReserva criada com sucesso! ID: " + nova.getId());
        System.out.println("Itens: " + nova.getDescricaoItens());
    }

    private static void fluxoAlterarReserva() {
        int idReservaAlt = lerInteiro("\nID da Reserva a alterar: ");
        
        LocalDate novaData;
        while (true) {
            novaData = lerData("Nova Data (dd/MM/yyyy): ");
            if (novaData.isBefore(SistemaFacade.DIA_ATUAL)) {
                System.out.println("Erro: A data não pode ser no passado.");
            } else {
                break;
            }
        }

        LocalTime novoInicio = lerHora("Nova Hora Início (HH:mm): ");
        LocalTime novoFim = lerHora("Nova Hora Fim (HH:mm): ");
        
        System.out.println("\n--- Usuários Registrados ---");
        facade.listarTodosUsuarios().forEach(u -> System.out.println("ID: " + u.getId() + " | Nome: " + u.getNome()));
        
        List<Usuario> novosConvidados = lerConvidadosSeguro("Novos IDs de Convidados (separados por vírgula, ou vazio): ");

        facade.alterarReserva(idReservaAlt, novosConvidados, novaData, novoInicio, novoFim);
        System.out.println("\nReserva alterada com sucesso! Observadores notificados.");
    }

    // --- Helpers de Input Seguro (Retry Loops) ---

    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Digite um número inteiro.\n");
            }
        }
    }

    private static List<Integer> lerEscolhasMultiplas(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                
                if (input == null || input.trim().isEmpty()) {
                    return new ArrayList<>();
                }
                
                return Arrays.stream(input.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Digite apenas números separados por vírgula, ou deixe vazio.\n");
            }
        }
    }

    private static LocalDate lerData(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            } catch (Exception e) {
                System.out.println("Formato inválido. Siga o padrão dd/MM/yyyy.");
            }
        }
    }

    private static LocalTime lerHora(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalTime.parse(scanner.nextLine(), TIME_FORMAT);
            } catch (Exception e) {
                System.out.println("Formato inválido. Siga o padrão HH:mm.");
            }
        }
    }

    private static List<Usuario> lerConvidadosSeguro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return converterIdsEmUsuarios(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Digite apenas IDs numéricos separados por vírgula, ou deixe vazio.");
            }
        }
    }

    // Auxiliar para converter input de string "2, 3, 4" em uma Lista de Objetos Usuario
    private static List<Usuario> converterIdsEmUsuarios(String inputIds) {
        if (inputIds == null || inputIds.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = Arrays.stream(inputIds.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return RepositorioSingleton.getInstance().getUsuarios().stream()
                .filter(u -> ids.contains(u.getId()))
                .collect(Collectors.toList());
    }
}