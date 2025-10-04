package team3.util;

import team3.domain.enums.TipoVeiculo;
import team3.domain.functional.DataSupplier;
import team3.domain.model.Cliente;
import team3.domain.model.PessoaFisica;
import team3.domain.model.PessoaJuridica;
import team3.domain.model.Veiculo;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataSuppliers {

    private static final Random random = new Random();

    // Supplier para gerar veículos fictícios
    public static final DataSupplier<Veiculo> GERAR_VEICULOS_FICTICIOS = () -> {
        String[] modelos = {"Corolla", "Civic", "HB20", "Onix", "Compass", "HR-V", "T-Cross", "Kicks", "Argo", "Ka"};
        String[] fabricantes = {"Toyota", "Honda", "Hyundai", "Chevrolet", "Jeep", "Volkswagen", "Nissan", "Fiat", "Ford"};
        TipoVeiculo[] tipos = TipoVeiculo.values();
        
        return IntStream.range(1, 21)
            .mapToObj(i -> {
                String placa = String.format("ABC-%d%c%02d", 
                    random.nextInt(10), 
                    (char)('A' + random.nextInt(26)), 
                    random.nextInt(100));
                String modelo = modelos[random.nextInt(modelos.length)];
                String fabricante = fabricantes[random.nextInt(fabricantes.length)];
                TipoVeiculo tipo = tipos[random.nextInt(tipos.length)];
                
                return new Veiculo(placa, modelo, fabricante, tipo);
            })
            .collect(Collectors.toList());
    };

    // Supplier para gerar clientes fictícios
    public static final DataSupplier<Cliente> GERAR_CLIENTES_FICTICIOS = () -> {
        String[] nomesPF = {"Joao Silva", "Maria Santos", "Pedro Oliveira", "Ana Costa", "Carlos Souza", 
                           "Lucia Ferreira", "Roberto Lima", "Patricia Rocha", "Fernando Alves", "Camila Nunes"};
        String[] emailsPF = {"joao.silva", "maria.santos", "pedro.oliveira", "ana.costa", "carlos.souza",
                            "lucia.ferreira", "roberto.lima", "patricia.rocha", "fernando.alves", "camila.nunes"};
        String[] nomesPJ = {"Tech Solutions Ltda", "Inovacao Digital SA", "Consultoria Moderna", 
                           "Servicos Avancados", "Empresa Beta", "Alpha Negocios", "Gamma Solucoes", 
                           "Delta Sistemas", "Epsilon Tech", "Zeta Consultoria"};
        String[] emailsPJ = {"tech.solutions", "inovacao.digital", "consultoria.moderna",
                            "servicos.avancados", "empresa.beta", "alpha.negocios", "gamma.solucoes",
                            "delta.sistemas", "epsilon.tech", "zeta.consultoria"};
        
        return IntStream.range(0, 20)
            .mapToObj(i -> {
                String email, telefone;
                if (i < 10) {
                    // Pessoa Física
                    String nome = nomesPF[i];
                    String cpf = String.format("%03d.%03d.%03d-%02d", 
                        random.nextInt(1000), random.nextInt(1000), 
                        random.nextInt(1000), random.nextInt(100));
                    email = emailsPF[i] + "@email.com";
                    telefone = String.format("(11) 9%04d-%04d", random.nextInt(10000), random.nextInt(10000));
                    
                    return new PessoaFisica(nome, email, telefone, cpf);
                } else {
                    // Pessoa Jurídica
                    String nome = nomesPJ[i - 10];
                    String cnpj = String.format("%02d.%03d.%03d/0001-%02d", 
                        random.nextInt(100), random.nextInt(1000), 
                        random.nextInt(1000), random.nextInt(100));
                    email = emailsPJ[i - 10] + "@empresa.com";
                    telefone = String.format("(11) 3%04d-%04d", random.nextInt(10000), random.nextInt(10000));
                    
                    return new PessoaJuridica(nome, email, telefone, cnpj);
                }
            })
            .collect(Collectors.toList());
    };

    // Supplier combinado para gerar dados completos
    public static final Supplier<String> GERAR_DADOS_COMPLETOS = () -> {
        List<Veiculo> veiculos = GERAR_VEICULOS_FICTICIOS.get();
        List<Cliente> clientes = GERAR_CLIENTES_FICTICIOS.get();
        
        return String.format("Dados gerados: %d veículos e %d clientes", 
            veiculos.size(), clientes.size());
    };

    // Método para popular dados usando os Suppliers
    public static void popularDadosFicticios(
            java.util.function.Consumer<Veiculo> adicionarVeiculo,
            java.util.function.Consumer<Cliente> adicionarCliente) {
        
        System.out.println("🔄 Gerando dados fictícios...");
        
        GERAR_VEICULOS_FICTICIOS.get().forEach(adicionarVeiculo);
        GERAR_CLIENTES_FICTICIOS.get().forEach(adicionarCliente);
        
        System.out.println("✅ Dados fictícios gerados com sucesso!");
    }
}