package team3.repository;

import team3.domain.model.Veiculo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VeiculoRepository implements IRepository<Veiculo, String> {

    private static final List<Veiculo> veiculos = new ArrayList<>();

    @Override
    public void cadastrar(Veiculo veiculo) {
        if (buscarPorId(veiculo.getPlaca()).isPresent()) {
            throw new IllegalArgumentException("Erro: Veículo com a placa " + veiculo.getPlaca() + " já existe.");
        }
        veiculos.add(veiculo);
    }

    @Override
    public void alterar(Veiculo veiculoAtualizado) {
        buscarPorId(veiculoAtualizado.getPlaca()).ifPresent(veiculoExistente -> {
            veiculos.remove(veiculoExistente);
            veiculos.add(veiculoAtualizado);
        });
    }

    @Override
    public Optional<Veiculo> buscarPorId(String placa) {
        return veiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                .findFirst();
    }

    public List<Veiculo> buscarPorParteDoNome(String nome) {
        return veiculos.stream()
                .filter(v -> v.getModelo().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Veiculo> listarTodos() {
        return new ArrayList<>(veiculos);
    }
}
