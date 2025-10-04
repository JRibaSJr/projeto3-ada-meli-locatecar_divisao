package team3.service;

import team3.domain.model.*;
import team3.repository.VeiculoRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AluguelService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteService clienteService;
    private final List<Aluguel> alugueisAtivos = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AluguelService(VeiculoRepository veiculoRepository, ClienteService clienteService) {
        this.veiculoRepository = veiculoRepository;
        this.clienteService = clienteService;
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
        alugueisAtivos.add(novoAluguel);

        System.out.println("Veículo alugado com sucesso!");
        System.out.println("Data e hora do aluguel: " + novoAluguel.getDataAluguel().format(formatter));
    }

    public void devolverVeiculo(String placa) {
        Optional<Aluguel> aluguelOpt = alugueisAtivos.stream()
                .filter(a -> a.getVeiculo().getPlaca().equalsIgnoreCase(placa))
                .findFirst();

        if (aluguelOpt.isEmpty()) {
            System.out.println("Erro: Não há um aluguel ativo para este veículo.");
            return;
        }

        Aluguel aluguel = aluguelOpt.get();
        aluguel.setDataDevolucao(LocalDateTime.now());

        long diarias = calcularDiarias(aluguel.getDataAluguel(), aluguel.getDataDevolucao());
        double valorBase = diarias * aluguel.getVeiculo().getTipo().getValorDiaria();
        double desconto = calcularDesconto(aluguel.getCliente(), diarias);
        double valorFinal = valorBase * (1 - desconto);

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


        Veiculo veiculo = aluguel.getVeiculo();
        veiculo.setDisponivel(true);
        veiculoRepository.alterar(veiculo);

        alugueisAtivos.remove(aluguel);
    }

    private long calcularDiarias(LocalDateTime inicio, LocalDateTime fim) {
        Duration duracao = Duration.between(inicio, fim);
        long horas = duracao.toHours();
        if (duracao.toMinutes() % 60 > 0 || horas == 0) {
            horas++;
        }
        long diarias = (horas / 24);
        if (horas % 24 > 0 || diarias == 0) {
            diarias++;
        }
        return diarias;
    }

    private double calcularDesconto(Cliente cliente, long diarias) {
        if (cliente instanceof PessoaFisica && diarias > 5) {
            return 0.05; // 5%
        }
        if (cliente instanceof PessoaJuridica && diarias > 3) {
            return 0.10; // 10%
        }
        return 0.0;
    }
}
