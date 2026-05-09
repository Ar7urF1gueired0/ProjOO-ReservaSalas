# 📅 Sistema de Agendamento de Salas (POO)
Este projeto consiste em um sistema de gerenciamento e reserva de salas de estudo, desenvolvido como parte da disciplina de Projeto Orientado a Objetos. O foco principal é a aplicação prática de padrões de projeto (Design Patterns) para resolver problemas de agendamento e regras de prioridade acadêmica.

## 🗂️ Estrutura do Projeto
- /src: Contém todo o código-fonte desenvolvido em Java SE puro.
- /doc: Documentação técnica, incluindo diagramas de classe UML e especificações de design.
- README.md: Guia geral e instruções de uso.

## 🏗️ Design Patterns Aplicados
Para garantir um sistema extensível, robusto e de fácil manutenção, foram implementados quatro padrões principais:

- **1. Strategy (Resolução de Conflitos)**
Utilizado para gerenciar as políticas de agendamento. Permite que o sistema alterne entre diferentes regras:
Ordem de Chegada: O primeiro a reservar garante a sala.

Prioridade Docente: Permite que um Docente sobrescreva a reserva de um Aluno.

- **2. Observer (Notificações)**
Implementa o fluxo de comunicação do sistema. Quando o status de uma reserva (Confirmada, Cancelada ou Alterada) sofre alteração, o Organizador e todos os Convidados são notificados automaticamente através deste padrão.

- **3. Factory Method (Criação de Objetos)**
Encapsula a lógica de criação das entidades do sistema. Centraliza a instância de diferentes tipos de salas (Laboratório, Estudo, Aula) e usuários (Aluno, Docente), mantendo o código cliente desacoplado das classes concretas.

- **4. Singleton (Repositório de Dados)**
Como o sistema não utiliza banco de dados externo, o padrão Singleton garante que exista uma única instância do repositório em memória durante a execução, servindo como a fonte única de verdade para salas e usuários.

## 📋 Regras de Negócio
- Persistência: Todos os dados são mantidos em memória durante a execução (volátil).

- Hierarquia de Sobrescrita:

  - Um Aluno não sobrescreve outro Aluno.

  - Um Docente não sobrescreve outro Docente.

  - Um Docente possui autoridade para sobrescrever a reserva de um Aluno.

- Interface: A interação ocorre estritamente via Terminal (CLI).

## 🚀 Como Executar
Certifique-se de ter o JDK 17 ou superior instalado.

1. Clone o repositório.

2. Compile os arquivos na pasta raiz:

```Bash
javac src/*.java
```

3. Execute a classe principal:
```Bash
java src/Main
```
