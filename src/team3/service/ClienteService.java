package team3.service;

import team3.domain.model.Cliente;
import team3.domain.model.PessoaFisica;
import team3.domain.model.PessoaJuridica;
import team3.repository.ClienteRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Consumer para imprimir cliente formatado
    private static final Consumer<Cliente> PRINT_CLIENTE = cliente -> {
        String icone = cliente instanceof PessoaFisica ? "👤" : "🏢";
        System.out.println(icone + " " + cliente);
    };

    // Consumer para imprimir lista paginada
    private static final Consumer<List<Cliente>> PRINT_LISTA_PAGINADA = clientes -> {
        if (clientes.isEmpty()) {
            System.out.println("📄 Nenhum cliente encontrado nesta página.");
        } else {
            System.out.println("📋 Listagem de Clientes:");
            clientes.forEach(PRINT_CLIENTE);
        }
    };

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void cadastrarCliente(Cliente cliente) {
        clienteRepository.cadastrar(cliente);
    }

    public void alterarCliente(Cliente cliente) {
        clienteRepository.alterar(cliente);
    }

    public Optional<Cliente> buscarClientePorDocumento(String documento) {
        return clienteRepository.buscarPorId(documento);
    }

    // Método com paginação obrigatória usando Stream
    public List<Cliente> listarClientesComPaginacao(int pagina, int tamanhoPagina) {
        List<Cliente> clientes = clienteRepository.listarComPaginacao(pagina, tamanhoPagina);
        PRINT_LISTA_PAGINADA.accept(clientes);
        return clientes;
    }

    // Listagem ordenada por nome com paginação
    public List<Cliente> listarClientesOrdenados(int pagina, int tamanhoPagina) {
        List<Cliente> clientes = clienteRepository.listarOrdenadoPorNome(pagina, tamanhoPagina);
        PRINT_LISTA_PAGINADA.accept(clientes);
        return clientes;
    }

    // Método tradicional mantido para compatibilidade, mas usando paginação
    public List<Cliente> listarTodosClientes() {
        return listarClientesComPaginacao(1, Integer.MAX_VALUE);
    }

    // Busca por nome usando Stream
    public List<Cliente> buscarClientePorNome(String nome, int pagina, int tamanhoPagina) {
        List<Cliente> encontrados = clienteRepository.stream()
            .filter(c -> c.getNome().toLowerCase().contains(nome.toLowerCase()))
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        PRINT_LISTA_PAGINADA.accept(encontrados);
        return encontrados;
    }

    // Busca por email usando Stream
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.buscarPorEmail(email);
    }

    // Listar apenas Pessoas Físicas com paginação
    public List<PessoaFisica> listarPessoasFisicas(int pagina, int tamanhoPagina) {
        List<PessoaFisica> pfs = clienteRepository.stream()
            .filter(c -> c instanceof PessoaFisica)
            .map(c -> (PessoaFisica) c)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        if (pfs.isEmpty()) {
            System.out.println("📄 Nenhuma Pessoa Física encontrada nesta página.");
        } else {
            System.out.println("📋 Listagem de Pessoas Físicas:");
            pfs.forEach(pf -> System.out.println("👤 " + pf));
        }
        return pfs;
    }

    // Listar apenas Pessoas Jurídicas com paginação
    public List<PessoaJuridica> listarPessoasJuridicas(int pagina, int tamanhoPagina) {
        List<PessoaJuridica> pjs = clienteRepository.stream()
            .filter(c -> c instanceof PessoaJuridica)
            .map(c -> (PessoaJuridica) c)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        if (pjs.isEmpty()) {
            System.out.println("📄 Nenhuma Pessoa Jurídica encontrada nesta página.");
        } else {
            System.out.println("📋 Listagem de Pessoas Jurídicas:");
            pjs.forEach(pj -> System.out.println("🏢 " + pj));
        }
        return pjs;
    }

    // Pipeline complexo: buscar clientes por critérios múltiplos
    public List<Cliente> buscarPorCriteriosMultiplos(
            Optional<String> nome,
            Optional<String> email,
            Optional<Class<? extends Cliente>> tipoCliente,
            int pagina,
            int tamanhoPagina) {
        
        Predicate<Cliente> filtro = c -> true; // Começar com filtro que aceita tudo
        
        if (nome.isPresent()) {
            filtro = filtro.and(c -> c.getNome().toLowerCase()
                .contains(nome.get().toLowerCase()));
        }
        
        if (email.isPresent()) {
            filtro = filtro.and(c -> c.getEmail().toLowerCase()
                .contains(email.get().toLowerCase()));
        }
        
        if (tipoCliente.isPresent()) {
            filtro = filtro.and(c -> tipoCliente.get().isInstance(c));
        }
        
        List<Cliente> resultado = clienteRepository.stream()
            .filter(filtro)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        PRINT_LISTA_PAGINADA.accept(resultado);
        return resultado;
    }

    // Obter estatísticas usando Stream
    public void exibirEstatisticas() {
        long total = clienteRepository.contarTotal();
        Map<String, Long> porTipo = clienteRepository.contarPorTipo();
        Map<String, Long> dominiosEmail = clienteRepository.obterDominiosEmail();
        
        System.out.println("📊 Estatísticas de Clientes:");
        System.out.println("Total: " + total);
        System.out.println("Por tipo:");
        porTipo.forEach((tipo, count) -> 
            System.out.println("  " + tipo + ": " + count));
        
        System.out.println("Domínios de email mais comuns:");
        dominiosEmail.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> 
                System.out.println("  " + entry.getKey() + ": " + entry.getValue()));
    }

    // Método para buscar com filtro personalizado
    public List<Cliente> buscarComFiltroPersonalizado(Predicate<Cliente> filtro, int pagina, int tamanhoPagina) {
        List<Cliente> resultado = clienteRepository.stream()
            .filter(filtro)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        PRINT_LISTA_PAGINADA.accept(resultado);
        return resultado;
    }
}