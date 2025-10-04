package team3.service;

import team3.domain.enums.TipoVeiculo;
import team3.domain.model.Veiculo;
import team3.repository.VeiculoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    // Consumer para imprimir veículo formatado
    private static final Consumer<Veiculo> PRINT_VEICULO = veiculo -> 
        System.out.println("🚗 " + veiculo);

    // Consumer para imprimir lista paginada
    private static final Consumer<List<Veiculo>> PRINT_LISTA_PAGINADA = veiculos -> {
        if (veiculos.isEmpty()) {
            System.out.println("📄 Nenhum veículo encontrado nesta página.");
        } else {
            System.out.println("📋 Listagem de Veículos:");
            veiculos.forEach(PRINT_VEICULO);
        }
    };

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public void cadastrarVeiculo(Veiculo veiculo) {
        veiculoRepository.cadastrar(veiculo);
    }

    public void alterarVeiculo(Veiculo veiculo) {
        veiculoRepository.alterar(veiculo);
    }

    // Refatorado usando Stream
    public List<Veiculo> buscarVeiculoPorModelo(String modelo) {
        return veiculoRepository.buscarPorParteDoNome(modelo);
    }

    public Optional<Veiculo> buscarVeiculoPorPlaca(String placa) {
        return veiculoRepository.buscarPorId(placa);
    }

    // Método com paginação obrigatória usando Stream
    public List<Veiculo> listarVeiculosComPaginacao(int pagina, int tamanhoPagina) {
        List<Veiculo> veiculos = veiculoRepository.listarComPaginacao(pagina, tamanhoPagina);
        PRINT_LISTA_PAGINADA.accept(veiculos);
        return veiculos;
    }

    // Listagem com ordenação e paginação
    public List<Veiculo> listarVeiculosOrdenados(int pagina, int tamanhoPagina) {
        List<Veiculo> veiculos = veiculoRepository.listarOrdenadoPorModelo(pagina, tamanhoPagina);
        PRINT_LISTA_PAGINADA.accept(veiculos);
        return veiculos;
    }

    // Método tradicional mantido para compatibilidade, mas usando paginação
    public List<Veiculo> listarTodosVeiculos() {
        return listarVeiculosComPaginacao(1, Integer.MAX_VALUE);
    }

    // Busca por tipo usando Predicate e Stream
    public List<Veiculo> buscarPorTipo(TipoVeiculo tipo) {
        return veiculoRepository.buscarPorTipo(tipo);
    }

    // Busca veículos disponíveis usando Stream
    public List<Veiculo> listarVeiculosDisponiveis(int pagina, int tamanhoPagina) {
        List<Veiculo> disponiveis = veiculoRepository.stream()
            .filter(Veiculo::isDisponivel)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        PRINT_LISTA_PAGINADA.accept(disponiveis);
        return disponiveis;
    }

    // Pipeline complexo: buscar veículos por critérios múltiplos
    public List<Veiculo> buscarPorCriteriosMultiplos(
            Optional<TipoVeiculo> tipo,
            Optional<String> fabricante,
            Optional<Boolean> disponivel,
            int pagina,
            int tamanhoPagina) {
        
        Predicate<Veiculo> filtro = v -> true; // Começar com filtro que aceita tudo
        
        if (tipo.isPresent()) {
            filtro = filtro.and(v -> v.getTipo() == tipo.get());
        }
        
        if (fabricante.isPresent()) {
            filtro = filtro.and(v -> v.getFabricante().toLowerCase()
                .contains(fabricante.get().toLowerCase()));
        }
        
        if (disponivel.isPresent()) {
            filtro = filtro.and(v -> v.isDisponivel() == disponivel.get());
        }
        
        List<Veiculo> resultado = veiculoRepository.stream()
            .filter(filtro)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        PRINT_LISTA_PAGINADA.accept(resultado);
        return resultado;
    }

    // Obter estatísticas usando Stream
    public void exibirEstatisticas() {
        long total = veiculoRepository.contarTotal();
        long disponiveis = veiculoRepository.stream()
            .filter(Veiculo::isDisponivel)
            .count();
        
        Map<TipoVeiculo, Long> porTipo = veiculoRepository.contarPorTipo();
        
        System.out.println("📊 Estatísticas de Veículos:");
        System.out.println("Total: " + total);
        System.out.println("Disponíveis: " + disponiveis);
        System.out.println("Alugados: " + (total - disponiveis));
        System.out.println("Por tipo:");
        porTipo.forEach((tipo, count) -> 
            System.out.println("  " + tipo + ": " + count));
    }

    // Obter modelos únicos usando Stream pipeline
    public List<String> obterModelosDisponiveis() {
        return veiculoRepository.obterModelosDisponiveis();
    }

    // Método para buscar com filtro personalizado
    public List<Veiculo> buscarComFiltroPersonalizado(Predicate<Veiculo> filtro, int pagina, int tamanhoPagina) {
        List<Veiculo> resultado = veiculoRepository.stream()
            .filter(filtro)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
            
        PRINT_LISTA_PAGINADA.accept(resultado);
        return resultado;
    }
}