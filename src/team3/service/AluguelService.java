package team3.service;

import team3.domain.model.Aluguel;
import team3.domain.model.Cliente;
import team3.domain.model.Veiculo;
import team3.repository.AluguelRepository;
import team3.repository.VeiculoRepository;
import team3.util.DiscountRules;
import team3.util.FileManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AluguelService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteService clienteService;
    private final AluguelRepository aluguelRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Function para calcular diárias
    private final Function<Aluguel, Long> CALCULAR_DIARIAS = aluguel -> {
        if (aluguel.getDataDevolucao() == null) return 0L;
        
        Duration duracao = Duration.between(aluguel.getDataAluguel(), aluguel.getDataDevolucao());
        long horas = duracao.toHours();
        
        if (duracao.toMinutes() % 60 > 0 || horas == 0) {
            horas++;
        }
        
        long diarias = (horas / 24);
        if (horas % 24 > 0 || diarias == 0) {
            diarias++;
        }
        
        return diarias;
    };

    // Function para calcular valor base
    private final Function<Aluguel, Double> CALCULAR_VALOR_BASE = aluguel -> {
        long diarias = CALCULAR_DIARIAS.apply(aluguel);
        return diarias * aluguel.getVeiculo().getTipo().getValorDiaria();
    };

    // Consumer para imprimir recibo formatado
    private final Consumer<Aluguel> PRINT_RECIBO = aluguel -> {
        if (aluguel.getDataDevolucao() == null) return;
        
        long diarias = CALCULAR_DIARIAS.apply(aluguel);
        double valorBase = aluguel.getValorBase();
        double desconto = aluguel.getDesconto();
        double valorFinal = aluguel.getValorTotal();

        System.out.println("--- Recibo de Devolução ---");
        System.out.println("Veículo: " + aluguel.getVeiculo().getModelo() + " | Placa: " + aluguel.getVeiculo().getPlaca());
        System.out.println("Cliente: " + aluguel.getCliente().getNome());
        System.out.println("Data do Aluguel: " + aluguel.getDataAluguel().format(formatter));
        System.out.println("Data da Devolução: " + aluguel.getDataDevolucao().format(formatter));
        System.out.println("Total de diárias: " + diarias);
        System.out.println("Valor base (R$): " + String.format("%.2f", valorBase));
        if (desconto > 0) {
            System.out.println("Desconto aplicado (" + (int)(desconto * 100) + "%): R$ " + String.format("%.2f", valorBase * desconto));
        }
        System.out.println("Valor final a pagar (R$): " + String.format("%.2f", valorFinal));
        System.out.println("---------------------------");
    };

    public AluguelService(VeiculoRepository veiculoRepository, ClienteService clienteService) {
        this.veiculoRepository = veiculoRepository;
        this.clienteService = clienteService;
        this.aluguelRepository = new AluguelRepository();
    }

    public void alugarVeiculo(String placa, String documentoCliente, String local) {
        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarPorId(placa);
        if (veiculoOpt.isEmpty()) {
            System.out.println("Erro: Veículo não encontrado.");
            return;
        }

        Veiculo veiculo = veiculoOpt.get();
        if (!veiculo.isDisponivel()) {
            System.out.println("Erro: Veículo já está alugado.");
            return;
        }

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorDocumento(documentoCliente);
        if (clienteOpt.isEmpty()) {
            System.out.println("Erro: Cliente não encontrado.");
            return;
        }

        veiculo.setDisponivel(false);
        veiculoRepository.alterar(veiculo);

        Aluguel novoAluguel = new Aluguel(veiculo, clienteOpt.get(), local);
        aluguelRepository.cadastrar(novoAluguel);

        System.out.println("Veículo alugado com sucesso!");
        System.out.println("Data e hora do aluguel: " + novoAluguel.getDataAluguel().format(formatter));
        
        // Salvar recibo de aluguel
        salvarReciboAluguel(novoAluguel);
    }

    public void devolverVeiculo(String placa) {
        Optional<Aluguel> aluguelOpt = aluguelRepository.buscarPorId(placa);

        if (aluguelOpt.isEmpty()) {
            System.out.println("Erro: Não há um aluguel ativo para este veículo.");
            return;
        }

        Aluguel aluguel = aluguelOpt.get();
        aluguel.setDataDevolucao(LocalDateTime.now());

        long diarias = CALCULAR_DIARIAS.apply(aluguel);
        double valorBase = CALCULAR_VALOR_BASE.apply(aluguel);
        
        // Usando interface funcional personalizada para calcular desconto
        double percentualDesconto = DiscountRules.STANDARD_DISCOUNT
            .calculateDiscount(aluguel.getCliente(), diarias);
        double valorDesconto = valorBase * percentualDesconto;
        double valorFinal = valorBase - valorDesconto;

        // Atualizar valores no aluguel
        aluguel.setValorBase(valorBase);
        aluguel.setDesconto(percentualDesconto);
        aluguel.setValorTotal(valorFinal);

        // Usar Consumer para imprimir recibo
        PRINT_RECIBO.accept(aluguel);

        Veiculo veiculo = aluguel.getVeiculo();
        veiculo.setDisponivel(true);
        veiculoRepository.alterar(veiculo);

        aluguelRepository.alterar(aluguel);
        
        // Salvar recibo de devolução
        salvarReciboDevolucao(aluguel);
    }

    // Listar aluguéis ativos com paginação usando Stream
    public List<Aluguel> listarAlugueisAtivos(int pagina, int tamanhoPagina) {
        return aluguelRepository.stream()
            .filter(a -> a.getDataDevolucao() == null)
            .skip((long) (pagina - 1) * tamanhoPagina)
            .limit(tamanhoPagina)
            .collect(Collectors.toList());
    }

    // Listar histórico de aluguéis com paginação usando Stream
    public List<Aluguel> listarHistoricoAlugueis(int pagina, int tamanhoPagina) {
        return aluguelRepository.listarOrdenadoPorData(pagina, tamanhoPagina);
    }

    // Relatório de faturamento por período usando Stream
    public void gerarRelatorioFaturamento(LocalDateTime inicio, LocalDateTime fim) {
        List<Aluguel> alugueisFinalizados = aluguelRepository.stream()
            .filter(a -> a.getDataDevolucao() != null)
            .filter(a -> a.getDataAluguel().isAfter(inicio.minusSeconds(1)) && 
                        a.getDataAluguel().isBefore(fim.plusSeconds(1)))
            .collect(Collectors.toList());

        double faturamentoTotal = alugueisFinalizados.stream()
            .filter(a -> a.getValorTotal() != null)
            .mapToDouble(Aluguel::getValorTotal)
            .sum();

        long totalAlugueis = alugueisFinalizados.size();
        
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATÓRIO DE FATURAMENTO\n");
        relatorio.append("Período: ").append(inicio.format(formatter))
                 .append(" a ").append(fim.format(formatter)).append("\n\n");
        relatorio.append("Total de aluguéis: ").append(totalAlugueis).append("\n");
        relatorio.append("Faturamento total: R$ ").append(String.format("%.2f", faturamentoTotal)).append("\n\n");
        
        if (!alugueisFinalizados.isEmpty()) {
            double ticketMedio = faturamentoTotal / totalAlugueis;
            relatorio.append("Ticket médio: R$ ").append(String.format("%.2f", ticketMedio)).append("\n\n");
            
            relatorio.append("DETALHAMENTO:\n");
            alugueisFinalizados.forEach(aluguel -> {
                relatorio.append(String.format("- %s (%s) | %s | R$ %.2f\n",
                    aluguel.getVeiculo().getModelo(),
                    aluguel.getVeiculo().getPlaca(),
                    aluguel.getCliente().getNome(),
                    aluguel.getValorTotal()));
            });
        }

        System.out.println(relatorio.toString());
        
        try {
            FileManager.saveReport("faturamento", relatorio.toString());
        } catch (Exception e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }

    // Relatório de veículos mais alugados usando Stream
    public void gerarRelatorioVeiculosMaisAlugados() {
        Map<String, Long> veiculosMaisAlugados = aluguelRepository.obterVeiculosMaisAlugados();
        
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATÓRIO - VEÍCULOS MAIS ALUGADOS\n\n");
        
        veiculosMaisAlugados.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> relatorio.append(String.format("%s: %d aluguéis\n", 
                entry.getKey(), entry.getValue())));

        System.out.println(relatorio.toString());
        
        try {
            FileManager.saveReport("veiculos_mais_alugados", relatorio.toString());
        } catch (Exception e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }

    // Relatório de clientes mais ativos usando Stream
    public void gerarRelatorioClientesMaisAtivos() {
        Map<String, Long> clientesMaisAtivos = aluguelRepository.obterClientesMaisAtivos();
        
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATÓRIO - CLIENTES MAIS ATIVOS\n\n");
        
        clientesMaisAtivos.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> relatorio.append(String.format("%s: %d aluguéis\n", 
                entry.getKey(), entry.getValue())));

        System.out.println(relatorio.toString());
        
        try {
            FileManager.saveReport("clientes_mais_ativos", relatorio.toString());
        } catch (Exception e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }

    // Salvar recibo de aluguel em arquivo
    private void salvarReciboAluguel(Aluguel aluguel) {
        try {
            String recibo = String.format("RECIBO DE ALUGUEL\nData: %s\nVeículo: %s (%s)\nCliente: %s\nLocal: %s\n",
                aluguel.getDataAluguel().format(formatter),
                aluguel.getVeiculo().getModelo(),
                aluguel.getVeiculo().getPlaca(),
                aluguel.getCliente().getNome(),
                aluguel.getLocal());
                
            FileManager.saveReport("recibo_aluguel_" + aluguel.getVeiculo().getPlaca(), recibo);
        } catch (Exception e) {
            System.err.println("Erro ao salvar recibo de aluguel: " + e.getMessage());
        }
    }

    // Salvar recibo de devolução em arquivo
    private void salvarReciboDevolucao(Aluguel aluguel) {
        try {
            long diarias = CALCULAR_DIARIAS.apply(aluguel);
            String recibo = String.format("RECIBO DE DEVOLUÇÃO\n" +
                "Veículo: %s (%s)\n" +
                "Cliente: %s\n" +
                "Data Aluguel: %s\n" +
                "Data Devolução: %s\n" +
                "Diárias: %d\n" +
                "Valor Base: R$ %.2f\n" +
                "Desconto: %.1f%%\n" +
                "Valor Final: R$ %.2f\n",
                aluguel.getVeiculo().getModelo(),
                aluguel.getVeiculo().getPlaca(),
                aluguel.getCliente().getNome(),
                aluguel.getDataAluguel().format(formatter),
                aluguel.getDataDevolucao().format(formatter),
                diarias,
                aluguel.getValorBase(),
                aluguel.getDesconto() * 100,
                aluguel.getValorTotal());
                
            FileManager.saveReport("recibo_devolucao_" + aluguel.getVeiculo().getPlaca(), recibo);
        } catch (Exception e) {
            System.err.println("Erro ao salvar recibo de devolução: " + e.getMessage());
        }
    }
}