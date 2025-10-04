package team3;

import team3.domain.enums.TipoVeiculo;
import team3.domain.model.Cliente;
import team3.domain.model.PessoaFisica;
import team3.domain.model.PessoaJuridica;
import team3.domain.model.Veiculo;
import team3.repository.*;
import team3.service.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static team3.domain.enums.TipoVeiculo.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final VeiculoRepository veiculoRepository = new VeiculoRepository();
    private static final ClienteRepository clienteRepository = new ClienteRepository();
    private static final VeiculoService veiculoService = new VeiculoService(veiculoRepository);
    private static final ClienteService clienteService = new ClienteService(clienteRepository);
    private static final AluguelService aluguelService = new AluguelService(veiculoRepository, clienteService);


    public static void main(String[] args) {

        //seedDados();

        while (true) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();
            processarOpcao(opcao);
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n--- ADA LocateCar - Locadora de Veículos ---");
        System.out.println("1. Gerenciar Veículos");
        System.out.println("2. Gerenciar Clientes");
        System.out.println("3. Alugar Veículo");
        System.out.println("4. Devolver Veículo");
        System.out.println("5. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void exibirSubMenu(String titulo) {
        System.out.println("\n--- Gerenciar " + titulo + " ---");
        System.out.println("1. Cadastrar");
        System.out.println("2. Alterar");
        System.out.println("3. Buscar");
        System.out.println("4. Listar Todos");
        System.out.println("5. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");
    }


    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Tente novamente.");
            return -1;
        }
    }

    private static void processarOpcao(int opcao) {
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
                System.out.println("Obrigado por utilizar o sistema!");
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private static void gerenciarVeiculos() {
        while(true) {
            exibirSubMenu("Veículos");
            int opcao = lerOpcao();
            if(opcao == 5) break;

            switch(opcao) {
                case 1: cadastrarVeiculo(); break;
                case 2: alterarVeiculo(); break;
                case 3: buscarVeiculo(); break;
                case 4: listarTodosVeiculos(); break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private static void gerenciarClientes() {
        while(true) {
            exibirSubMenu("Clientes");
            int opcao = lerOpcao();
            if(opcao == 5) break;

            switch(opcao) {
                case 1: cadastrarCliente(); break;
                case 2: alterarCliente(); break;
                case 3: buscarCliente(); break;
                case 4: listarTodosClientes(); break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarVeiculo() {
        try {
            System.out.print("Digite a placa: ");
            String placa = scanner.nextLine();
            System.out.print("Digite o modelo: ");
            String modelo = scanner.nextLine();
            System.out.print("Digite o fabricante: ");
            String fabricante = scanner.nextLine();
            System.out.print("Digite o tipo (PEQUENO, MEDIO, SUV): ");
            TipoVeiculo tipo = TipoVeiculo.valueOf(scanner.nextLine().toUpperCase());

            Veiculo veiculo = new Veiculo(placa, modelo, fabricante, tipo);
            veiculoService.cadastrarVeiculo(veiculo);
            System.out.println("Veículo cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void alterarVeiculo() {
        System.out.print("Digite a placa do veículo a ser alterado: ");
        String placa = scanner.nextLine();
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorPlaca(placa);

        if (veiculoOpt.isPresent()) {
            Veiculo veiculo = veiculoOpt.get();
            System.out.print("Novo modelo (atual: " + veiculo.getModelo() + "): ");
            veiculo.setModelo(scanner.nextLine());
            System.out.print("Novo fabricante (atual: " + veiculo.getFabricante() + "): ");
            veiculo.setFabricante(scanner.nextLine());

            veiculoService.alterarVeiculo(veiculo);
            System.out.println("Veículo alterado com sucesso!");
        } else {
            System.out.println("Veículo não encontrado.");
        }
    }

    private static void buscarVeiculo() {
        System.out.print("Digite parte do modelo do veículo para buscar: ");
        String modelo = scanner.nextLine();
        List<Veiculo> veiculosEncontrados = veiculoService.buscarVeiculoPorModelo(modelo);

        if (veiculosEncontrados.isEmpty()) {
            System.out.println("Nenhum veículo encontrado com este modelo.");
        } else {
            System.out.println("--- Veículos Encontrados ---");
            veiculosEncontrados.forEach(System.out::println);
        }
    }

    private static void listarTodosVeiculos() {
        List<Veiculo> veiculos = veiculoService.listarTodosVeiculos();
        if (veiculos.isEmpty()) {
            System.out.println("Nenhum veículo cadastrado.");
        } else {
            System.out.println("--- Lista de Veículos ---");
            veiculos.forEach(System.out::println);
        }
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
                System.out.println("Tipo de cliente inválido.");
                return;
            }
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void alterarCliente() {
        System.out.print("Digite o documento (CPF/CNPJ) do cliente a ser alterado: ");
        String documento = scanner.nextLine();
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorDocumento(documento);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            System.out.print("Novo nome (atual: " + cliente.getNome() + "): ");
            cliente.setNome(scanner.nextLine());
            System.out.print("Novo email (atual: " + cliente.getEmail() + "): ");
            cliente.setEmail(scanner.nextLine());
            System.out.print("Novo telefone (atual: " + cliente.getTelefone() + "): ");
            cliente.setTelefone(scanner.nextLine());

            clienteService.alterarCliente(cliente);
            System.out.println("Cliente alterado com sucesso!");
        } else {
            System.out.println("Cliente não encontrado.");
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
            System.out.println("Cliente não encontrado.");
        }
    }

    private static void listarTodosClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            System.out.println("--- Lista de Clientes ---");
            clientes.forEach(System.out::println);
        }
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

    private static void seedDados(){
        veiculoService.cadastrarVeiculo(new Veiculo("AAA-0A00", "MODELO1", "FABRICANTE1", PEQUENO));
        veiculoService.cadastrarVeiculo(new Veiculo("BBB-0B00", "MODELO2", "FABRICANTE2", MEDIO));
        veiculoService.cadastrarVeiculo(new Veiculo("CCC-0C00", "MODELO3", "FABRICANTE3", SUV));

        clienteService.cadastrarCliente(new PessoaFisica("CLIENTE PF1", "pf1@cliente.com.br", "1199999-9999", "99999999999"));
        clienteService.cadastrarCliente(new PessoaFisica("CLIENTE PF2", "pf2@cliente.com.br", "1188888-9999", "88888888888"));
        clienteService.cadastrarCliente(new PessoaFisica("CLIENTE PF3", "pf3@cliente.com.br", "1177777-7777", "77777777777"));

        clienteService.cadastrarCliente(new PessoaJuridica("CLIENTE PJ1", "pj1@cliente.com.br", "1199999-9999", "55.555.555.0001-55"));
        clienteService.cadastrarCliente(new PessoaJuridica("CLIENTE PJ2", "pj2@cliente.com.br", "1188888-8888", "44.444.444.0001-44"));
        clienteService.cadastrarCliente(new PessoaJuridica("CLIENTE PJ3", "pj3@cliente.com.br", "1177777-8888", "33.333.333.0001-33"));
    }
}