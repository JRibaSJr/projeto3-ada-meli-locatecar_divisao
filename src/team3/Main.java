package team3;

import team3.domain.enums.TipoVeiculo;
import team3.domain.model.Cliente;
import team3.domain.model.PessoaFisica;
import team3.domain.model.PessoaJuridica;
import team3.domain.model.Veiculo;
import team3.repository.ClienteRepository;
import team3.repository.VeiculoRepository;
import team3.service.AluguelService;
import team3.service.ClienteService;
import team3.service.VeiculoService;
import team3.util.DataSuppliers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static team3.domain.enums.TipoVeiculo.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final VeiculoRepository veiculoRepository = new VeiculoRepository();
    private static final ClienteRepository clienteRepository = new ClienteRepository();
    private static final VeiculoService veiculoService = new VeiculoService(veiculoRepository);
    private static final ClienteService clienteService = new ClienteService(clienteRepository);
    private static final AluguelService aluguelService = new AluguelService(veiculoRepository, clienteService);

    // Consumer para exibir mensagens de erro
    private static final Consumer<String> PRINT_ERRO = erro -> 
        System.out.println("❌ " + erro);

    // Consumer para exibir mensagens de sucesso
    private static final Consumer<String> PRINT_SUCESSO = sucesso -> 
        System.out.println("✅ " + sucesso);

    // Supplier para obter tamanho de página padrão
    private static final Supplier<Integer> TAMANHO_PAGINA_PADRAO = () -> 10;

    public static void main(String[] args) {
        System.out.println("🚗 Inicializando ADA LocateCar...");
        
        seedDados();

        while (true) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();
            if (!processarOpcao(opcao))
                System.exit(0);
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n--- ADA LocateCar - Locadora de Veículos ---");
        System.out.println("1. Gerenciar Veículos");
        System.out.println("2. Gerenciar Clientes");
        System.out.println("3. Alugar Veículo");
        System.out.println("4. Devolver Veículo");
        System.out.println("5. Relatórios");
        System.out.println("6. Estatísticas");
        System.out.println("7. Gerar Dados Fictícios");
        System.out.println("8. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void exibirSubMenu(String titulo) {
        System.out.println("\n--- Gerenciar " + titulo + " ---");
        System.out.println("1. Cadastrar");
        System.out.println("2. Alterar");
        System.out.println("3. Buscar");
        System.out.println("4. Listar Todos (com paginação)");
        System.out.println("5. Busca Avançada");
        System.out.println("6. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            PRINT_ERRO.accept("Opção inválida. Tente novamente.");
            return -1;
        }
    }

    private static int lerPagina() {
        System.out.print("Número da página (padrão 1): ");
        try {
            String input = scanner.nextLine();
            return input.isEmpty() ? 1 : Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static boolean processarOpcao(int opcao) {
        boolean retorno = true;
        switch (opcao) {
            case 1:
                gerenciarVeiculos();
                break;
            case 2:
                gerenciarClientes();
                break;
            case 3:
                alugarVeiculo();
                break;
            case 4:
                devolverVeiculo();
                break;
            case 5:
                menuRelatorios();
                break;
            case 6:
                exibirEstatisticas();
                break;
            case 7:
                gerarDadosFicticios();
                break;
            case 8:
                System.out.println("Obrigado por utilizar o sistema!");
                retorno = false;
                break;
            default:
                PRINT_ERRO.accept("Opção inválida.");
        }
        return retorno;
    }

    private static void gerenciarVeiculos() {
        while(true) {
            exibirSubMenu("Veículos");
            int opcao = lerOpcao();
            if(opcao == 6) break;

            switch(opcao) {
                case 1: cadastrarVeiculo(); break;
                case 2: alterarVeiculo(); break;
                case 3: buscarVeiculo(); break;
                case 4: listarTodosVeiculos(); break;
                case 5: buscarVeiculosAvancado(); break;
                default: PRINT_ERRO.accept("Opção inválida.");
            }
        }
    }

    private static void gerenciarClientes() {
        while(true) {
            exibirSubMenu("Clientes");
            int opcao = lerOpcao();
            if(opcao == 6) break;

            switch(opcao) {
                case 1: cadastrarCliente(); break;
                case 2: alterarCliente(); break;
                case 3: buscarCliente(); break;
                case 4: listarTodosClientes(); break;
                case 5: buscarClientesAvancado(); break;
                default: PRINT_ERRO.accept("Opção inválida.");
            }
        }
    }

    private static void cadastrarVeiculo() {
        try {
            System.out.print("Digite a placa (formato ABC-1A23): ");
            String placa = scanner.nextLine().toUpperCase();
            System.out.print("Digite o modelo: ");
            String modelo = scanner.nextLine();
            System.out.print("Digite o fabricante: ");
            String fabricante = scanner.nextLine();
            System.out.print("Digite o tipo (PEQUENO, MEDIO, SUV): ");
            TipoVeiculo tipo = TipoVeiculo.valueOf(scanner.nextLine().toUpperCase());

            Veiculo veiculo = new Veiculo(placa, modelo, fabricante, tipo);
            veiculoService.cadastrarVeiculo(veiculo);
            PRINT_SUCESSO.accept("Veículo cadastrado com sucesso!");
        } catch (Exception e) {
            PRINT_ERRO.accept(e.getMessage());
        }
    }

    private static void alterarVeiculo() {
        System.out.print("Digite a placa do veículo a ser alterado: ");
        String placa = scanner.nextLine();
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorPlaca(placa);

        if (veiculoOpt.isPresent()) {
            Veiculo veiculo = veiculoOpt.get();
            System.out.print("Novo modelo (atual: " + veiculo.getModelo() + "): ");
            String novoModelo = scanner.nextLine();
            if (!novoModelo.isEmpty()) {
                veiculo.setModelo(novoModelo);
            }
            
            System.out.print("Novo fabricante (atual: " + veiculo.getFabricante() + "): ");
            String novoFabricante = scanner.nextLine();
            if (!novoFabricante.isEmpty()) {
                veiculo.setFabricante(novoFabricante);
            }

            veiculoService.alterarVeiculo(veiculo);
            PRINT_SUCESSO.accept("Veículo alterado com sucesso!");
        } else {
            PRINT_ERRO.accept("Veículo não encontrado.");
        }
    }

    private static void buscarVeiculo() {
        System.out.print("Digite parte do modelo do veículo para buscar: ");
        String modelo = scanner.nextLine();
        List<Veiculo> veiculosEncontrados = veiculoService.buscarVeiculoPorModelo(modelo);

        if (veiculosEncontrados.isEmpty()) {
            PRINT_ERRO.accept("Nenhum veículo encontrado com este modelo.");
        } else {
            System.out.println("--- Veículos Encontrados ---");
            veiculosEncontrados.forEach(System.out::println);
        }
    }

    private static void listarTodosVeiculos() {
        int pagina = lerPagina();
        int tamanhoPagina = TAMANHO_PAGINA_PADRAO.get();
        
        List<Veiculo> veiculos = veiculoService.listarVeiculosComPaginacao(pagina, tamanhoPagina);
        
        if (veiculos.size() == tamanhoPagina) {
            System.out.println("💡 Digite uma página maior para ver mais resultados.");
        }
    }

    private static void buscarVeiculosAvancado() {
        System.out.println("=== Busca Avançada de Veículos ===");
        
        System.out.print("Tipo (PEQUENO/MEDIO/SUV ou vazio): ");
        String tipoStr = scanner.nextLine();
        Optional<TipoVeiculo> tipo = tipoStr.isEmpty() ? 
            Optional.empty() : Optional.of(TipoVeiculo.valueOf(tipoStr.toUpperCase()));
            
        System.out.print("Fabricante (ou vazio): ");
        String fabricante = scanner.nextLine();
        Optional<String> fabricanteOpt = fabricante.isEmpty() ? 
            Optional.empty() : Optional.of(fabricante);
            
        System.out.print("Apenas disponíveis? (s/n): ");
        String disponivelStr = scanner.nextLine();
        Optional<Boolean> disponivel = disponivelStr.isEmpty() ? 
            Optional.empty() : Optional.of(disponivelStr.toLowerCase().startsWith("s"));

        int pagina = lerPagina();
        int tamanhoPagina = TAMANHO_PAGINA_PADRAO.get();

        veiculoService.buscarPorCriteriosMultiplos(tipo, fabricanteOpt, disponivel, pagina, tamanhoPagina);
    }

    private static void cadastrarCliente() {
        System.out.print("Pessoa Física (1) ou Jurídica (2)? ");
        int tipoCliente = lerOpcao();

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        try {
            if(tipoCliente == 1) {
                System.out.print("CPF: ");
                String cpf = scanner.nextLine();
                clienteService.cadastrarCliente(new PessoaFisica(nome, email, telefone, cpf));
            } else if (tipoCliente == 2) {
                System.out.print("CNPJ: ");
                String cnpj = scanner.nextLine();
                clienteService.cadastrarCliente(new PessoaJuridica(nome, email, telefone, cnpj));
            } else {
                PRINT_ERRO.accept("Tipo de cliente inválido.");
                return;
            }
            PRINT_SUCESSO.accept("Cliente cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            PRINT_ERRO.accept(e.getMessage());
        }
    }

    private static void alterarCliente() {
        System.out.print("Digite o documento (CPF/CNPJ) do cliente a ser alterado: ");
        String documento = scanner.nextLine();
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorDocumento(documento);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            System.out.print("Novo nome (atual: " + cliente.getNome() + ", vazio para manter): ");
            String novoNome = scanner.nextLine();
            if (!novoNome.isEmpty()) {
                cliente.setNome(novoNome);
            }
            
            System.out.print("Novo email (atual: " + cliente.getEmail() + ", vazio para manter): ");
            String novoEmail = scanner.nextLine();
            if (!novoEmail.isEmpty()) {
                cliente.setEmail(novoEmail);
            }
            
            System.out.print("Novo telefone (atual: " + cliente.getTelefone() + ", vazio para manter): ");
            String novoTelefone = scanner.nextLine();
            if (!novoTelefone.isEmpty()) {
                cliente.setTelefone(novoTelefone);
            }

            clienteService.alterarCliente(cliente);
            PRINT_SUCESSO.accept("Cliente alterado com sucesso!");
        } else {
            PRINT_ERRO.accept("Cliente não encontrado.");
        }
    }

    private static void buscarCliente() {
        System.out.print("Digite o documento (CPF/CNPJ) do cliente: ");
        String documento = scanner.nextLine();
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorDocumento(documento);

        if (clienteOpt.isPresent()) {
            System.out.println("--- Cliente Encontrado ---");
            System.out.println(clienteOpt.get());
        } else {
            PRINT_ERRO.accept("Cliente não encontrado.");
        }
    }

    private static void listarTodosClientes() {
        int pagina = lerPagina();
        int tamanhoPagina = TAMANHO_PAGINA_PADRAO.get();
        
        List<Cliente> clientes = clienteService.listarClientesComPaginacao(pagina, tamanhoPagina);
        
        if (clientes.size() == tamanhoPagina) {
            System.out.println("💡 Digite uma página maior para ver mais resultados.");
        }
    }

    private static void buscarClientesAvancado() {
        System.out.println("=== Busca Avançada de Clientes ===");
        
        System.out.print("Nome (ou vazio): ");
        String nome = scanner.nextLine();
        Optional<String> nomeOpt = nome.isEmpty() ? Optional.empty() : Optional.of(nome);
        
        System.out.print("Email (ou vazio): ");
        String email = scanner.nextLine();
        Optional<String> emailOpt = email.isEmpty() ? Optional.empty() : Optional.of(email);
        
        System.out.print("Tipo (1=PF, 2=PJ, vazio=ambos): ");
        String tipoStr = scanner.nextLine();
        Optional<Class<? extends Cliente>> tipoCliente = Optional.empty();
        if ("1".equals(tipoStr)) {
            tipoCliente = Optional.of(PessoaFisica.class);
        } else if ("2".equals(tipoStr)) {
            tipoCliente = Optional.of(PessoaJuridica.class);
        }

        int pagina = lerPagina();
        int tamanhoPagina = TAMANHO_PAGINA_PADRAO.get();

        clienteService.buscarPorCriteriosMultiplos(nomeOpt, emailOpt, tipoCliente, pagina, tamanhoPagina);
    }

    private static void alugarVeiculo() {
        System.out.print("Digite a placa do veículo a ser alugado: ");
        String placa = scanner.nextLine();
        System.out.print("Digite o documento (CPF/CNPJ) do cliente: ");
        String documento = scanner.nextLine();
        System.out.print("Digite o local do aluguel: ");
        String local = scanner.nextLine();

        aluguelService.alugarVeiculo(placa, documento, local);
    }

    private static void devolverVeiculo() {
        System.out.print("Digite a placa do veículo a ser devolvido: ");
        String placa = scanner.nextLine();

        aluguelService.devolverVeiculo(placa);
    }

    private static void menuRelatorios() {
        System.out.println("\n=== Relatórios ===");
        System.out.println("1. Faturamento por Período");
        System.out.println("2. Veículos Mais Alugados");
        System.out.println("3. Clientes Mais Ativos");
        System.out.println("4. Histórico de Aluguéis");
        System.out.println("5. Voltar");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerOpcao();
        switch (opcao) {
            case 1:
                // Relatório de faturamento dos últimos 30 dias
                LocalDateTime fim = LocalDateTime.now();
                LocalDateTime inicio = fim.minusDays(30);
                aluguelService.gerarRelatorioFaturamento(inicio, fim);
                break;
            case 2:
                aluguelService.gerarRelatorioVeiculosMaisAlugados();
                break;
            case 3:
                aluguelService.gerarRelatorioClientesMaisAtivos();
                break;
            case 4:
                int pagina = lerPagina();
                int tamanhoPagina = TAMANHO_PAGINA_PADRAO.get();
                List<team3.domain.model.Aluguel> historico = aluguelService.listarHistoricoAlugueis(pagina, tamanhoPagina);
                historico.forEach(System.out::println);
                break;
            case 5:
                break;
            default:
                PRINT_ERRO.accept("Opção inválida.");
        }
    }

    private static void exibirEstatisticas() {
        System.out.println("\n📊 === ESTATÍSTICAS DO SISTEMA ===");
        
        System.out.println("\n🚗 Estatísticas de Veículos:");
        veiculoService.exibirEstatisticas();
        
        System.out.println("\n👥 Estatísticas de Clientes:");
        clienteService.exibirEstatisticas();
    }

    private static void gerarDadosFicticios() {
        System.out.print("Deseja gerar dados fictícios? Isso irá adicionar 20 veículos e 20 clientes (s/n): ");
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.toLowerCase().startsWith("s")) {
            DataSuppliers.popularDadosFicticios(
                veiculoService::cadastrarVeiculo,
                clienteService::cadastrarCliente
            );
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private static void seedDados(){
        try {
            // Usando supplier para verificar se já existem dados
            Supplier<Boolean> temDados = () -> 
                veiculoRepository.contarTotal() > 0 || clienteRepository.contarTotal() > 0;
                
            if (temDados.get()) {
                System.out.println("📂 Dados existentes carregados do arquivo.");
                return;
            }
            
            System.out.println("🌱 Criando dados iniciais...");
            
            veiculoService.cadastrarVeiculo(new Veiculo("AAA-0A00", "MODELO1", "FABRICANTE1", PEQUENO));
            veiculoService.cadastrarVeiculo(new Veiculo("BBB-0B00", "MODELO2", "FABRICANTE2", MEDIO));
            veiculoService.cadastrarVeiculo(new Veiculo("CCC-0C00", "MODELO3", "FABRICANTE3", SUV));

            clienteService.cadastrarCliente(new PessoaFisica("CLIENTE PF1", "pf1@cliente.com.br", "1199999-9999", "12345678901"));
            clienteService.cadastrarCliente(new PessoaFisica("CLIENTE PF2", "pf2@cliente.com.br", "1188888-9999", "98765432101"));
            clienteService.cadastrarCliente(new PessoaFisica("CLIENTE PF3", "pf3@cliente.com.br", "1177777-7777", "11122233344"));

            clienteService.cadastrarCliente(new PessoaJuridica("CLIENTE PJ1", "pj1@cliente.com.br", "1199999-9999", "12345678000155"));
            clienteService.cadastrarCliente(new PessoaJuridica("CLIENTE PJ2", "pj2@cliente.com.br", "1188888-8888", "98765432000144"));
            clienteService.cadastrarCliente(new PessoaJuridica("CLIENTE PJ3", "pj3@cliente.com.br", "1177777-8888", "11122233000133"));
            
            PRINT_SUCESSO.accept("Dados iniciais criados!");
        } catch (Exception e) {
            PRINT_ERRO.accept("Erro ao criar dados iniciais: " + e.getMessage());
        }
    }
}