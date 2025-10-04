package team3.repository;

import team3.domain.model.Aluguel;
import team3.domain.model.Cliente;
import team3.domain.model.Veiculo;
import team3.util.FileManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AluguelRepository implements IRepository<Aluguel, String> {

    private static final List<Aluguel> alugueis = new ArrayList<>();
    private static final String ARQUIVO_ALUGUEIS = "alugueis.dat";

    public AluguelRepository() {
        try {
            carregarDados();
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar dados de aluguéis: " + e.getMessage());
        }
    }

    @Override
    public void cadastrar(Aluguel aluguel) {
        alugueis.add(aluguel);
        try {
            salvarDados();
        } catch (Exception e) {
            System.err.println("Erro ao salvar aluguel: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Aluguel aluguelAtualizado) {
        // Busca por placa do veículo usando Stream
        Optional<Aluguel> aluguelExistente = stream()
            .filter(a -> a.getVeiculo().getPlaca().equals(
                aluguelAtualizado.getVeiculo().getPlaca()) 
                && a.getDataDevolucao() == null)
            .findFirst();
            
        aluguelExistente.ifPresent(existente -> {
            alugueis.remove(existente);
            alugueis.add(aluguelAtualizado);
            
            try {
                salvarDados();
            } catch (Exception e) {
                System.err.println("Erro ao salvar alteração: " + e.getMessage());
            }
        });
    }

    @Override
    public Optional<Aluguel> buscarPorId(String placa) {
        return stream()
            .filter(a -> a.getVeiculo().getPlaca().equalsIgnoreCase(placa) 
                && a.getDataDevolucao() == null)
            .findFirst();
    }

    // Busca aluguéis ativos usando Stream
    public List<Aluguel> buscarAlugueisAtivos() {
        return buscarComFiltro(a -> a.getDataDevolucao() == null);
    }

    // Busca aluguéis finalizados usando Stream
    public List<Aluguel> buscarAlugueisFinalizados() {
        return buscarComFiltro(a -> a.getDataDevolucao() != null);
    }

    // Busca por cliente usando Stream
    public List<Aluguel> buscarPorCliente(Cliente cliente) {
        return buscarComFiltro(a -> a.getCliente().getDocumento()
            .equals(cliente.getDocumento()));
    }

    // Busca por veículo usando Stream
    public List<Aluguel> buscarPorVeiculo(Veiculo veiculo) {
        return buscarComFiltro(a -> a.getVeiculo().getPlaca()
            .equals(veiculo.getPlaca()));
    }

    // Busca por período usando Stream
    public List<Aluguel> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return buscarComFiltro(a -> 
            a.getDataAluguel().isAfter(inicio.minusSeconds(1)) && 
            a.getDataAluguel().isBefore(fim.plusSeconds(1)));
    }

    @Override
    public List<Aluguel> listarTodos() {
        return stream().collect(Collectors.toList());
    }

    @Override
    public Stream<Aluguel> stream() {
        return alugueis.stream();
    }

    @Override
    public List<Aluguel> buscarComFiltro(Predicate<Aluguel> filtro) {
        return stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    @Override
    public List<Aluguel> listarComPaginacao(int pagina, int tamanhoPagina) {
        return stream()
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Listagem ordenada por data com paginação usando Stream pipeline
    public List<Aluguel> listarOrdenadoPorData(int pagina, int tamanhoPagina) {
        return stream()
            .sorted(Comparator.comparing(Aluguel::getDataAluguel).reversed())
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Relatórios usando Streams
    public Map<String, Long> obterVeiculosMaisAlugados() {
        return stream()
            .collect(Collectors.groupingBy(
                a -> a.getVeiculo().getPlaca() + " - " + a.getVeiculo().getModelo(),
                Collectors.counting()
            ));
    }

    public Map<String, Long> obterClientesMaisAtivos() {
        return stream()
            .collect(Collectors.groupingBy(
                a -> a.getCliente().getNome() + " (" + a.getCliente().getDocumento() + ")",
                Collectors.counting()
            ));
    }

    // Faturamento total usando Stream
    public double calcularFaturamentoTotal() {
        return stream()
            .filter(a -> a.getValorTotal() != null)
            .mapToDouble(Aluguel::getValorTotal)
            .sum();
    }

    // Faturamento por período usando Stream
    public double calcularFaturamentoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return buscarPorPeriodo(inicio, fim).stream()
            .filter(a -> a.getValorTotal() != null)
            .mapToDouble(Aluguel::getValorTotal)
            .sum();
    }

    @Override
    public long contarTotal() {
        return stream().count();
    }

    @Override
    public void salvarDados() throws Exception {
        try {
            FileManager.writeObjectToFile(new ArrayList<>(alugueis), ARQUIVO_ALUGUEIS);
        } catch (IOException e) {
            throw new Exception("Erro ao salvar dados de aluguéis: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void carregarDados() throws Exception {
        if (FileManager.fileExists(ARQUIVO_ALUGUEIS)) {
            try {
                List<Aluguel> alugueisCarregados = (List<Aluguel>) 
                    FileManager.readObjectFromFile(ARQUIVO_ALUGUEIS);
                alugueis.clear();
                alugueis.addAll(alugueisCarregados);
            } catch (IOException | ClassNotFoundException e) {
                throw new Exception("Erro ao carregar dados de aluguéis: " + e.getMessage(), e);
            }
        }
    }
}