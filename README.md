# Projeto ADA LocateCar - Locadora de Veículos

Este projeto é uma aplicação de console para gerenciar o aluguel de veículos, desenvolvida como parte do curso da ADA Tech. A aplicação permite cadastrar clientes (Pessoa Física e Jurídica) e veículos, além de gerenciar as operações de aluguel e devolução.

## Tecnologias Utilizadas
- **Java 17** (ou superior)
- **Maven/Gradle** (Opcional, para gerenciamento de dependências, mas não necessário para este projeto simples)

## Como Executar
1. Clone o repositório para a sua máquina local.
2. Abra o projeto em sua IDE de preferência (IntelliJ, Eclipse, VS Code).
3. Localize e execute o método `main` na classe `Main.java` localizada em `src/br/com/adatech/locatecar/Main.java`.

## Estrutura do Projeto
O projeto está organizado em uma arquitetura de camadas para separar as responsabilidades:
- `domain`: Contém as entidades de negócio (`Veiculo`, `Cliente`), enums e a lógica central do domínio.
- `repository`: Camada de acesso a dados. Atualmente, usa listas em memória, mas a estrutura com interfaces permite a troca por um banco de dados ou arquivos.
- `service`: Camada que orquestra as regras de negócio da aplicação.
- `Main.java`: Ponto de entrada da aplicação, responsável pela interação com o usuário (UI de console).

## Conceitos Aplicados e Aprendizados

### Facilidades
- **Herança e Encapsulamento:** A modelagem das classes `Cliente`, `PessoaFisica` e `PessoaJuridica` foi um processo natural e um exemplo claro de como a herança pode ser usada para reutilizar código e criar um modelo de dados coeso. O encapsulamento com atributos privados e métodos getters/setters foi fundamental para proteger a integridade dos objetos.
- **Princípio da Responsabilidade Única (SOLID):** Separar a lógica em Serviços (`VeiculoService`, `ClienteService`, `AluguelService`) e Repositórios (`VeiculoRepository`) tornou o código muito mais organizado e fácil de manter. Cada classe tem um propósito claro e bem definido.

### Dificuldades
- **Generics e Inversão de Dependência (SOLID):** No início, o conceito de criar uma interface genérica como `IRepository<T, ID>` foi um pouco abstrato. Entender como a camada de Serviço dependeria dessa *interface* em vez da classe concreta (`VeiculoRepository`) foi o maior desafio, mas também o maior aprendizado. Essa abordagem torna o sistema muito mais flexível e desacoplado, o que foi uma grande revelação sobre design de software.
- **Gerenciamento de Estado:** Controlar o estado dos objetos (por exemplo, a disponibilidade de um veículo) entre as diferentes camadas (Serviço e Repositório) exigiu atenção para garantir que os dados estivessem sempre consistentes, especialmente ao alugar e devolver um carro.