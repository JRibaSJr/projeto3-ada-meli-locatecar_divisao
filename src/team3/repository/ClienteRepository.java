package team3.repository;

import team3.domain.model.Cliente;
import team3.domain.model.PessoaFisica;
import team3.domain.model.PessoaJuridica;
import team3.util.FileManager;
import team3.util.ValidationUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClienteRepository implements IRepository<Cliente, String> {

    private static final List<Cliente> clientes = new ArrayList<>();
    private static final String ARQUIVO_CLIENTES = "clientes.dat";

    public ClienteRepository() {
        try {
            carregarDados();
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar dados de clientes: " + e.getMessage());
        }
    }

    @Override
    public void cadastrar(Cliente cliente) {
        // Validação usando Predicate personalizado
        if (!ValidationUtils.DOCUMENTO_VALIDATOR.isValid(cliente.getDocumento())) {
            throw new IllegalArgumentException("Erro: Documento inválido: " + cliente.getDocumento());
        }

        if (!ValidationUtils.EMAIL_VALIDATOR.test(cliente.getEmail())) {
            throw new IllegalArgumentException("Erro: Email inválido: " + cliente.getEmail());
        }

        // Busca usando Stream
        boolean existe = stream()
            .anyMatch(c -> c.getDocumento().equals(cliente.getDocumento()));
            
        if (existe) {
            throw new IllegalArgumentException("Erro: Cliente com o documento " + cliente.getDocumento() + " já existe.");
        }
        
        clientes.add(cliente);
        
        try {
            salvarDados();
        } catch (Exception e) {
            System.err.println("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Cliente clienteAtualizado) {
        // Usando Stream com filter e findFirst
        Optional<Cliente> clienteExistente = stream()
            .filter(c -> c.getDocumento().equals(clienteAtualizado.getDocumento()))
            .findFirst();
            
        clienteExistente.ifPresent(existente -> {
            clientes.remove(existente);
            clientes.add(clienteAtualizado);
            
            try {
                salvarDados();
            } catch (Exception e) {
                System.err.println("Erro ao salvar alteração: " + e.getMessage());
            }
        });
    }

    @Override
    public Optional<Cliente> buscarPorId(String documento) {
        return stream()
            .filter(c -> c.getDocumento().equals(documento))
            .findFirst();
    }

    // Busca por nome usando Stream
    public List<Cliente> buscarPorNome(String nome) {
        return buscarComFiltro(c -> c.getNome().toLowerCase()
            .contains(nome.toLowerCase()));
    }

    // Busca apenas Pessoas Físicas usando Stream
    public List<PessoaFisica> buscarPessoasFisicas() {
        return stream()
            .filter(c -> c instanceof PessoaFisica)
            .map(c -> (PessoaFisica) c)
            .collect(Collectors.toList());
    }

    // Busca apenas Pessoas Jurídicas usando Stream
    public List<PessoaJuridica> buscarPessoasJuridicas() {
        return stream()
            .filter(c -> c instanceof PessoaJuridica)
            .map(c -> (PessoaJuridica) c)
            .collect(Collectors.toList());
    }

    // Busca por email usando Stream
    public Optional<Cliente> buscarPorEmail(String email) {
        return stream()
            .filter(c -> c.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

    @Override
    public List<Cliente> listarTodos() {
        return stream().collect(Collectors.toList());
    }

    @Override
    public Stream<Cliente> stream() {
        return clientes.stream();
    }

    @Override
    public List<Cliente> buscarComFiltro(Predicate<Cliente> filtro) {
        return stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    @Override
    public List<Cliente> listarComPaginacao(int pagina, int tamanhoPagina) {
        return stream()
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Listagem ordenada por nome com paginação usando Stream pipeline
    public List<Cliente> listarOrdenadoPorNome(int pagina, int tamanhoPagina) {
        return stream()
            .sorted(Comparator.comparing(Cliente::getNome))
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Pipeline complexo: obter domínios de email mais comuns
    public Map<String, Long> obterDominiosEmail() {
        return stream()
            .map(Cliente::getEmail)
            .filter(email -> email.contains("@"))
            .map(email -> email.substring(email.indexOf("@") + 1))
            .collect(Collectors.groupingBy(
                dominio -> dominio,
                Collectors.counting()
            ));
    }

    @Override
    public long contarTotal() {
        return stream().count();
    }

    // Estatísticas usando Stream
    public Map<String, Long> contarPorTipo() {
        return stream()
            .collect(Collectors.groupingBy(
                cliente -> cliente instanceof PessoaFisica ? "Pessoa Física" : "Pessoa Jurídica",
                Collectors.counting()
            ));
    }

    @Override
    public void salvarDados() throws Exception {
        try {
            FileManager.writeObjectToFile(new ArrayList<>(clientes), ARQUIVO_CLIENTES);
        } catch (IOException e) {
            throw new Exception("Erro ao salvar dados de clientes: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void carregarDados() throws Exception {
        if (FileManager.fileExists(ARQUIVO_CLIENTES)) {
            try {
                List<Cliente> clientesCarregados = (List<Cliente>) 
                    FileManager.readObjectFromFile(ARQUIVO_CLIENTES);
                clientes.clear();
                clientes.addAll(clientesCarregados);
            } catch (IOException | ClassNotFoundException e) {
                throw new Exception("Erro ao carregar dados de clientes: " + e.getMessage(), e);
            }
        }
    }
}