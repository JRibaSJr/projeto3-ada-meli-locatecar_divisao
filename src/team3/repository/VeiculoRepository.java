package team3.repository;

import team3.domain.enums.TipoVeiculo;
import team3.domain.model.Veiculo;
import team3.util.FileManager;
import team3.util.ValidationUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VeiculoRepository implements IRepository<Veiculo, String> {

    private static final List<Veiculo> veiculos = new ArrayList<>();
    private static final String ARQUIVO_VEICULOS = "veiculos.dat";

    public VeiculoRepository() {
        try {
            carregarDados();
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar dados de veículos: " + e.getMessage());
        }
    }

    @Override
    public void cadastrar(Veiculo veiculo) {
        // Validação usando Predicate
        if (!ValidationUtils.PLACA_VALIDATOR.test(veiculo.getPlaca())) {
            throw new IllegalArgumentException("Erro: Placa inválida: " + veiculo.getPlaca());
        }

        // Busca usando Stream com filter
        boolean existe = stream()
            .anyMatch(v -> v.getPlaca().equalsIgnoreCase(veiculo.getPlaca()));
            
        if (existe) {
            throw new IllegalArgumentException("Erro: Veículo com a placa " + veiculo.getPlaca() + " já existe.");
        }
        
        veiculos.add(veiculo);
        
        try {
            salvarDados();
        } catch (Exception e) {
            System.err.println("Erro ao salvar veículo: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Veiculo veiculoAtualizado) {
        // Usando Stream com filter e findFirst
        Optional<Veiculo> veiculoExistente = stream()
            .filter(v -> v.getPlaca().equalsIgnoreCase(veiculoAtualizado.getPlaca()))
            .findFirst();
            
        veiculoExistente.ifPresent(existente -> {
            veiculos.remove(existente);
            veiculos.add(veiculoAtualizado);
            
            try {
                salvarDados();
            } catch (Exception e) {
                System.err.println("Erro ao salvar alteração: " + e.getMessage());
            }
        });
    }

    @Override
    public Optional<Veiculo> buscarPorId(String placa) {
        return stream()
            .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
            .findFirst();
    }

    // Método refatorado usando Stream com filter e collect
    public List<Veiculo> buscarPorParteDoNome(String nome) {
        return stream()
            .filter(v -> v.getModelo().toLowerCase().contains(nome.toLowerCase()))
            .collect(Collectors.toList());
    }

    // Busca por tipo usando Stream
    public List<Veiculo> buscarPorTipo(TipoVeiculo tipo) {
        return buscarComFiltro(v -> v.getTipo() == tipo);
    }

    // Busca veículos disponíveis usando Stream
    public List<Veiculo> buscarDisponiveis() {
        return buscarComFiltro(Veiculo::isDisponivel);
    }

    // Busca por fabricante usando Stream
    public List<Veiculo> buscarPorFabricante(String fabricante) {
        return buscarComFiltro(v -> v.getFabricante().toLowerCase()
            .contains(fabricante.toLowerCase()));
    }

    @Override
    public List<Veiculo> listarTodos() {
        return stream().collect(Collectors.toList());
    }

    @Override
    public Stream<Veiculo> stream() {
        return veiculos.stream();
    }

    @Override
    public List<Veiculo> buscarComFiltro(Predicate<Veiculo> filtro) {
        return stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    @Override
    public List<Veiculo> listarComPaginacao(int pagina, int tamanhoPagina) {
        return stream()
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Listagem ordenada com paginação usando Stream pipeline completo
    public List<Veiculo> listarOrdenadoPorModelo(int pagina, int tamanhoPagina) {
        return stream()
            .sorted(Comparator.comparing(Veiculo::getModelo))
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Pipeline complexo: filtrar, mapear, ordenar e coletar
    public List<String> obterModelosDisponiveis() {
        return stream()
            .filter(Veiculo::isDisponivel)
            .map(Veiculo::getModelo)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public long contarTotal() {
        return stream().count();
    }

    // Contagem por tipo usando Stream e groupingBy
    public Map<TipoVeiculo, Long> contarPorTipo() {
        return stream()
            .collect(Collectors.groupingBy(
                Veiculo::getTipo,
                Collectors.counting()
            ));
    }

    @Override
    public void salvarDados() throws Exception {
        try {
            FileManager.writeObjectToFile(new ArrayList<>(veiculos), ARQUIVO_VEICULOS);
        } catch (IOException e) {
            throw new Exception("Erro ao salvar dados de veículos: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void carregarDados() throws Exception {
        if (FileManager.fileExists(ARQUIVO_VEICULOS)) {
            try {
                List<Veiculo> veiculosCarregados = (List<Veiculo>) 
                    FileManager.readObjectFromFile(ARQUIVO_VEICULOS);
                veiculos.clear();
                veiculos.addAll(veiculosCarregados);
            } catch (IOException | ClassNotFoundException e) {
                throw new Exception("Erro ao carregar dados de veículos: " + e.getMessage(), e);
            }
        }
    }
}