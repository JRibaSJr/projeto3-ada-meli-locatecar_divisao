# Projeto ADA LocateCar - Locadora de Veículos (Refatorado)

Este projeto é uma aplicação de console para gerenciar o aluguel de veículos, desenvolvida como parte do curso da ADA Tech. A aplicação foi **completamente refatorada** para aplicar conceitos avançados de Java, incluindo Streams, Functional Interfaces e persistência com arquivos.

## 🚀 Tecnologias e Recursos Utilizados
- **Java 24** (ou superior)
- **Streams API** - Para processamento funcional de coleções
- **Functional Interfaces** - Predicate, Consumer, Function, Supplier + personalizadas
- **Files API (java.nio.file)** - Para persistência e manipulação de arquivos
- **InputStream/OutputStream** - Para operações de I/O
- **Serialização** - Para persistência de objetos
- **Lambda Expressions** - Para código mais conciso e expressivo

## ⚡ Como Executar
1. Clone o repositório para a sua máquina local
2. Abra o projeto em sua IDE de preferência (IntelliJ, Eclipse, VS Code)
3. Execute o método `main` na classe `Main.java` localizada em `src/team3/Main.java`
4. Os dados são automaticamente persistidos nos diretórios `data/` e `reports/`

## 🏗️ Estrutura do Projeto Refatorado
```
src/team3/
├── domain/
│   ├── model/          # Entidades (Cliente, Veiculo, Aluguel) - agora Serializable
│   ├── enums/          # TipoVeiculo com valores de diária
│   └── functional/     # Interfaces funcionais personalizadas
├── repository/         # Camada de dados com persistência e Streams
├── service/           # Lógica de negócio modernizada
├── util/              # Utilitários (FileManager, Validators, etc.)
└── Main.java          # Interface de console aprimorada
```

### Novos Diretórios Criados Automaticamente:
- `data/` - Armazenamento persistente de dados
- `reports/` - Relatórios gerados automaticamente

## 🔄 Principais Refatorações Implementadas

### ✅ Streams em Java
- **Substituição completa de loops:** Todos os for/while foram substituídos por operações Stream
- **Paginação obrigatória:** Implementado `skip()` e `limit()` em todas as listagens
- **Pipelines compostos:** Criados pipelines `map + filter + sorted + collect` complexos
- **Operações funcionais:** Uso extensivo de `filter()`, `map()`, `collect()`, `groupingBy()`

**Exemplos implementados:**
```java
// Pipeline completo para busca avançada
return stream()
    .filter(filtro)
    .skip((long) (pagina - 1) * tamanhoPagina)
    .limit(tamanhoPagina)
    .collect(Collectors.toList());

// Estatísticas usando Streams
public Map<TipoVeiculo, Long> contarPorTipo() {
    return stream()
        .collect(Collectors.groupingBy(
            Veiculo::getTipo,
            Collectors.counting()));
}
```

### ✅ Functional Interfaces
- **Interfaces nativas:** Uso extensivo de `Predicate`, `Consumer`, `Function`, `Supplier`
- **Interfaces personalizadas:** Criadas para regras específicas do negócio
  - `ValidationRule<T>` - Validações personalizadas
  - `DiscountCalculator` - Cálculo de descontos
  - `ReportGenerator<T>` - Geração de relatórios
  - `DataSupplier<T>` - Fornecimento de dados fictícios

**Exemplos implementados:**
```java
// Predicate para validação de CPF
public static final Predicate<String> CPF_VALIDATOR = cpf -> { /* validação */ };

// Consumer para impressão formatada
private static final Consumer<Veiculo> PRINT_VEICULO = veiculo -> 
    System.out.println("🚗 " + veiculo);

// Function para cálculo de valores
private final Function<Aluguel, Double> CALCULAR_VALOR_BASE = aluguel -> {
    long diarias = CALCULAR_DIARIAS.apply(aluguel);
    return diarias * aluguel.getVeiculo().getTipo().getValorDiaria();
};
```

### ✅ Files, InputStream e OutputStream
- **Persistência completa:** Dados salvos automaticamente em arquivos
- **Manipulação com Files API:** Verificação, criação e leitura de arquivos
- **Serialização de objetos:** Uso de `ObjectInputStream`/`ObjectOutputStream`
- **Relatórios em arquivos:** Geração automática com timestamp

**Recursos implementados:**
- Salvamento automático de veículos, clientes e aluguéis
- Relatórios de faturamento, veículos mais alugados, clientes ativos
- Recibos de aluguel e devolução em arquivos
- Carregamento automático na inicialização

## 📊 Novas Funcionalidades
1. **Sistema de Paginação:** Todas as listagens agora suportam paginação
2. **Busca Avançada:** Filtros múltiplos combinados com Streams
3. **Relatórios Automáticos:** Faturamento, rankings, estatísticas
4. **Gerador de Dados:** Supplier para criar dados fictícios para testes
5. **Persistência Automática:** Dados salvos e carregados automaticamente
6. **Validações Avançadas:** CPF, CNPJ, email, telefone usando Predicates

## 🎯 Conceitos SOLID e Padrões Mantidos
- **Single Responsibility:** Cada classe tem responsabilidade única
- **Open/Closed:** Extensível via interfaces funcionais
- **Liskov Substitution:** Hierarquia Cliente mantida
- **Interface Segregation:** Interfaces específicas e coesas
- **Dependency Inversion:** Dependência de abstrações

## 📈 Melhorias Percebidas com a Refatoração

### Principais Ganhos:
1. **Código mais conciso:** Redução significativa de linhas com lambdas e Streams
2. **Melhor legibilidade:** Pipelines Stream são auto-documentados
3. **Maior flexibilidade:** Composição de filtros e operações funcionais
4. **Persistência robusta:** Dados não se perdem entre execuções
5. **Relatórios automáticos:** Insights de negócio disponíveis
6. **Paginação universal:** Performance melhorada para grandes volumes

### Performance:
- **Lazy evaluation:** Streams processam apenas o necessário
- **Operações otimizadas:** Aproveitamento de operações terminais
- **Gestão de memória:** Paginação evita carregar todos os dados

## 🚧 Dificuldades Encontradas e Superadas

### Principais Desafios:
1. **Migração de loops para Streams:** Repensar a lógica procedural para funcional
2. **Serialização de objetos:** Garantir compatibilidade entre versões
3. **Composição de Predicates:** Combinação dinâmica de filtros
4. **Gestão de arquivos:** Tratamento de exceções e criação de diretórios
5. **Paginação universal:** Implementar skip/limit em todas as operações

### Soluções Implementadas:
- Criação de utility classes para reutilização
- Interfaces funcionais personalizadas para regras de negócio
- FileManager centralizado para operações de I/O
- Padrão Builder para construção de filtros complexos
- Consumer/Supplier para operações comuns

## 🔧 Checklist de Refatoração Completo

- [x] Substituir loops por Streams nas buscas e filtros
- [x] Implementar paginação com `Stream.skip()` e `Stream.limit()`
- [x] Criar `Comparator` com lambda para ordenações
- [x] Usar `Predicate` para validações (CPF/CNPJ/Email/Telefone)
- [x] Usar `Function` para cálculos (valores, diárias, descontos)
- [x] Usar `Consumer` para formatação de saída
- [x] Usar `Supplier` para geração de dados fictícios
- [x] Persistir dados com `Files` API
- [x] Implementar I/O com `InputStream`/`OutputStream`
- [x] Criar interfaces funcionais personalizadas
- [x] Implementar relatórios com Streams e arquivos
- [x] Atualizar README.md com documentação completa

## 💡 Próximos Passos Sugeridos
1. Implementação de índices para buscas mais rápidas
2. Interface gráfica com JavaFX
3. API REST com Spring Boot
4. Integração com banco de dados
5. Testes unitários automatizados
6. Sistema de backup automático

---

**Desenvolvido com ❤️ usando conceitos avançados de Java**

---

## 📋 Divisão do Projeto em Branches (Git Flow)

Para facilitar o desenvolvimento e revisão, este projeto foi dividido em **4 partes lógicas**:

### **🔹 BRANCH 1: estrutura-base-interfaces**
**Arquivos:** `domain/functional/`, `domain/model/`, `util/`
- 4 interfaces funcionais personalizadas
- Entidades Serializable para persistência  
- Utilitários de validação com Predicates
- FileManager para operações de I/O

### **🔹 BRANCH 2: repositorios-streams**  
**Arquivos:** `repository/`
- Interface IRepository modernizada
- Repositórios com Streams e paginação
- Persistência automática em arquivos
- AluguelRepository para relatórios

### **🔹 BRANCH 3: services-funcionais**
**Arquivos:** `service/`, `util/DataSuppliers.java`
- Services com Consumer, Function, Supplier
- Sistema de relatórios automáticos
- DataSuppliers para dados fictícios
- Busca avançada com filtros combinados

### **🔹 BRANCH 4: interface-documentacao**
**Arquivos:** `Main.java`, `README.md`  
- Interface de console expandida (8 opções)
- Menu de relatórios e estatísticas
- Documentação completa das refatorações
- Correções e ajustes finais

### **🔀 Merge Final → `develop`**
Após merge das 4 branches via pull request, o projeto final estará completo com todas as refatorações aplicadas.