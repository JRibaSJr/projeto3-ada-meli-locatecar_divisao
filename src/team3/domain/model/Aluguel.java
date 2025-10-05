package team3.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Aluguel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Veiculo veiculo;
    private final Cliente cliente;
    private final String local;
    private final LocalDateTime dataAluguel;
    private LocalDateTime dataDevolucao;
    private Double valorTotal;
    private Double valorBase;
    private Double desconto;

    public Aluguel(Veiculo veiculo, Cliente cliente, String local) {
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.local = local;
        this.dataAluguel = LocalDateTime.now();
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

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    @Override
    public String toString() {
        return "Aluguel{" +
                "veiculo=" + veiculo.getPlaca() +
                ", cliente=" + cliente.getNome() +
                ", local='" + local + '\'' +
                ", dataAluguel=" + dataAluguel +
                ", dataDevolucao=" + dataDevolucao +
                ", valorTotal=" + valorTotal +
                '}';
    }
}