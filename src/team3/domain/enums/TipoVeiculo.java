package team3.domain.enums;

public enum TipoVeiculo {
    PEQUENO(100.00),
    MEDIO(150.00),
    SUV(200.00);

    private final double valorDiaria;

    TipoVeiculo(double valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }
}
