package team3.service;

import team3.domain.model.Veiculo;
import team3.repository.VeiculoRepository;

import java.util.List;
import java.util.Optional;

public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public void cadastrarVeiculo(Veiculo veiculo) {
        veiculoRepository.cadastrar(veiculo);
    }

    public void alterarVeiculo(Veiculo veiculo) {
        veiculoRepository.alterar(veiculo);
    }

    public List<Veiculo> buscarVeiculoPorModelo(String modelo) {
        return veiculoRepository.buscarPorParteDoNome(modelo);
    }

    public Optional<Veiculo> buscarVeiculoPorPlaca(String placa) {
        return veiculoRepository.buscarPorId(placa);
    }

    public List<Veiculo> listarTodosVeiculos() {
        return veiculoRepository.listarTodos();
    }
}
