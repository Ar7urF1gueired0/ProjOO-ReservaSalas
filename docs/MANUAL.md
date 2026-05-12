# 📖 Mini Manual de Navegação (Interface via Terminal)

Bem-vindo ao **Sistema de Reserva de Salas**. A interface deste sistema foi construída para rodar integralmente no terminal (linha de comando). Siga os passos abaixo para navegar pelos menus e testar todas as funcionalidades.

## 1. Acesso ao Sistema (Login)

Ao iniciar o programa (`java Main`), o sistema exigirá autenticação. Utilize as credenciais de teste (Mock) pré-cadastradas:

- **Perfil Aluno:**
  - **Email:** `a1@email.com` (você pode usar de `a1` até `a5`)
  - **Senha:** `123`
- **Perfil Professor:**
  - **Email:** `p1@email.com` (você pode usar de `p1` até `p3`)
  - **Senha:** `123`

## 2. Entendendo o Menu Principal

Após o login, você verá o painel de controle principal. Para navegar, digite o **número** correspondente à opção e aperte `Enter`:

- **1. Alterar Conta (Logout):** Desloga o usuário atual e volta para a tela de login. Útil para testar os conflitos de horários entre Alunos e Professores.
- **2. Alterar Regra de Prioridade:** Alterna a política do sistema.
  - _Primeiro a Reservar:_ Ninguém sobrescreve a reserva de ninguém.
  - _Prioridade Professor:_ Professores podem "tomar" a sala de um aluno caso precisem do mesmo horário.
- **3. Gerenciar Reservas:** Abre o sub-menu para **Criar**, **Alterar** ou **Deletar** reservas.
- **4. Listar Minhas Reservas:** Mostra apenas as reservas onde você é o Organizador ou foi adicionado como Convidado.
- **5. Relatório Diário:** Exibe todas as reservas do sistema agendadas para a data de hoje.
- **0. Sair:** Encerra a aplicação.

## 3. Guia Passo a Passo: Criando uma Reserva

O fluxo da Opção 3 -> Opção 1 (Criar Reserva) é o coração do sistema. Fique atento aos formatos exigidos pelo terminal:

1.  **Data e Hora:**
    - Digite a data no formato exato `dd/MM/yyyy` (Ex: `25/10/2026`). O sistema não aceita datas no passado.
    - Digite a hora no formato `HH:mm` (Ex: `14:30`).
2.  **Escolha da Sala:** O sistema listará as salas disponíveis segregadas por tipo (Laboratório, Sala de Aula, Estudo). Digite o **ID numérico** da sala desejada (Ex: `101`).
3.  **Seleção de Materiais Extras (Kits):**
    - Dependendo da sala escolhida, o sistema oferecerá equipamentos.
    - Para reservar a sala vazia, digite `0`.
    - Para escolher um ou mais kits, digite os números separados por vírgula (Ex: `1, 2`).
4.  **Adição de Convidados:**
    - O sistema listará os usuários cadastrados.
    - Digite os IDs separados por vírgula (Ex: `2, 5, 8`).
    - Se não houver convidados, basta deixar vazio e apertar `Enter`.

## 4. Dicas Importantes e Resolução de Problemas

- **"Formato Inválido":** Se você digitar uma letra onde o sistema espera um número, ou errar o padrão de data (ex: usar hífens em vez de barras), o sistema não vai quebrar! Ele exibirá uma mensagem de erro e pedirá para você digitar novamente.
- **Alterando Reservas:** Para usar a opção "Alterar Reserva", você primeiro precisa saber o ID da sua reserva. Acesse a opção **4. Listar Minhas Reservas** no menu principal para anotar o ID antes de prosseguir com a alteração.
- **Sobrescrevendo Reservas (Regra do Professor):** Se você logar como Professor, ativar a Regra de Prioridade 2, e tentar reservar uma sala no mesmo horário de um Aluno, o sistema permitirá a criação. Observe que o Aluno (original) receberá uma notificação (simulada no terminal) avisando sobre a perda da sala.
