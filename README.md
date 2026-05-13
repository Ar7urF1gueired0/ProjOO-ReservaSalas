# Sistema de Reserva de Salas 🏫

Um sistema de gerenciamento de reservas de salas (Laboratórios, Salas de Aula e Salas de Estudo) desenvolvido em Java. Este projeto aplica conceitos avançados de Programação Orientada a Objetos (POO) e Design Patterns para garantir uma arquitetura escalável, de fácil manutenção e aderente aos princípios SOLID.

## 🚀 Funcionalidades

- **Autenticação de Usuários:** Controle de acesso diferenciado para Professores e Alunos.
- **Gerenciamento de Reservas (CRUD):** Criação, alteração, listagem e exclusão de reservas.
- **Alocação Dinâmica de Materiais:** Possibilidade de reservar equipamentos adicionais (Kits de Física/Química, Audiovisual, Computadores, etc.) dependendo do tipo da sala escolhida.
- **Políticas de Prioridade:** Regras de negócio configuráveis que definem quem tem prioridade em caso de conflito de horários (ex: Professores podem sobrescrever reservas de Alunos dependendo da política ativa).
- **Relatórios:** Geração de relatórios diários de ocupação e visualização de agenda por usuário.
- **Notificações:** Sistema de alerta automático para convidados quando uma reserva sofre alterações.

## 🧩 Arquitetura e Padrões de Projeto (Design Patterns)

O sistema foi desenhado visando o baixo acoplamento e a alta coesão, utilizando os seguintes padrões do _Gang of Four_ (GoF):

- **Facade (`SistemaFacade`):** Centraliza e simplifica a comunicação entre a interface do usuário (`Main`) e a complexa lógica de negócio do subsistema.
- **Strategy (`PoliticaDeReserva`):** Permite a troca de regras de prioridade de reserva em tempo de execução (ex: `PoliticaPrimeiroAReservar` vs `PoliticaPrioridadeProfessor`).
- **Decorator (`ReservaDecorator`):** Adiciona dinamicamente materiais extras (Física, Química, Audiovisual) às reservas base sem a necessidade de criar múltiplas subclasses, evitando a explosão de classes. A comunicação flui através da interface `IReserva`.
- **Factory Method (`SalaFactory`):** Encapsula a lógica de criação de diferentes tipos de salas (Laboratórios, Salas de Aula e Estudo).
- **Singleton (`RepositorioSingleton`):** Garante uma instância única do banco de dados em memória para consistência dos dados durante a execução.
- **Observer (`ReservaObserver`):** Implementa notificações automáticas (ex: `ServicoNotificacaoEmail`) disparadas pelo `GerenciadorDeReservas` quando há modificações.

## ⚙️ Como Executar o Projeto

O projeto roda inteiramente no terminal e não possui dependências externas complexas.

**Pré-requisitos:**

- [Java JDK](https://www.oracle.com/java/technologies/downloads/) instalado na máquina (versão 8 ou superior).
- Git (opcional, para clonar o repositório).

**Passo a passo:**

1. **Clone o repositório:**

   ```bash
   git clone <URL_DO_SEU_REPOSITORIO>
   cd <NOME_DA_PASTA_DO_PROJETO>
   ```

2. **Compile o código-fonte:**

   ```bash
   javac *.java
   ```

3. **Inicie o sistema:**

   ```bash
   java Main
   ```

## 🎮 Como Usar (Dados Mockados)

Para facilitar os testes imediatos, o sistema já é inicializado com dados _mock_ (fictícios). Você pode realizar o login com as seguintes credenciais:

- **Para testar como Aluno:**
  - _Email:_ `a1@email.com` (até `a5@email.com`)
  - _Senha:_ `123`
- **Para testar como Professor:**
  - _Email:_ `p1@email.com` (até `p3@email.com`)
  - _Senha:_ `123`

Siga as instruções dos menus interativos no terminal para criar suas reservas, adicionar convidados, incluir materiais via Decorator e testar as quebras de política de prioridade.

## 🚧 Próximos Passos (Roadmap)

- **Controle de Acesso Baseado em Papéis (RBAC):** Expansão das capacidades do perfil de `Professor` para atuar como Administrador do sistema. Isso permitirá que professores cadastrem novas salas e registrem novos usuários diretamente pelo terminal, com menus de administração exclusivos e invisíveis para o perfil de `Aluno`.

...
