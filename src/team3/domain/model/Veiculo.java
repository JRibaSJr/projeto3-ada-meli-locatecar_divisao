package team3.domain.model;

import team3.domain.enums.TipoVeiculo;

public class Veiculo {
    private final String placa;
    private String modelo;
    private String fabricante;
    private boolean disponivel = true;
    private TipoVeiculo tipo;

    public Veiculo(String placa, String modelo, String fabricante, TipoVeiculo tipo) {
        this.placa = placa;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Veiculo {" +
                "placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", tipo=" + tipo +
                ", disponivel=" + (disponivel ? "Sim" : "Não") +
                '}';
    }
}