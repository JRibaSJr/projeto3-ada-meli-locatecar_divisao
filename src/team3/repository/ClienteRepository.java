package team3.repository;

import team3.domain.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository implements IRepository<Cliente, String> {

    private static final List<Cliente> clientes = new ArrayList<>();

    @Override
    public void cadastrar(Cliente cliente) {
        if (buscarPorId(cliente.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Erro: Cliente com o documento " + cliente.getDocumento() + " já existe.");
        }
        clientes.add(cliente);
    }

    @Override
    public void alterar(Cliente clienteAtualizado) {
        buscarPorId(clienteAtualizado.getDocumento()).ifPresent(clienteExistente -> {
            clientes.remove(clienteExistente);
            clientes.add(clienteAtualizado);
        });
    }

    @Override
    public Optional<Cliente> buscarPorId(String documento) {
        return clientes.stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst();
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }
}
