package team3.domain.model;

import java.time.LocalDateTime;

public class Aluguel {
    private final Veiculo veiculo;
    private final Cliente cliente;
    private final String local;
    private final LocalDateTime dataAluguel;
    private LocalDateTime dataDevolucao;

    public Aluguel(Veiculo veiculo, Cliente cliente, String local) {
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.local = local;
        this.dataAluguel = LocalDateTime.now(); // Pega a data e hora atuais
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getLocal() {
        return local;
    }

    public LocalDateTime getDataAluguel() {
        return dataAluguel;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }
}
