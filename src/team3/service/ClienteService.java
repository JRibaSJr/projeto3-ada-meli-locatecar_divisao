package team3.service;

import team3.domain.model.Cliente;
import team3.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void cadastrarCliente(Cliente cliente) {
        clienteRepository.cadastrar(cliente);
    }

    public void alterarCliente(Cliente cliente) {
        clienteRepository.alterar(cliente);
    }

    public Optional<Cliente> buscarClientePorDocumento(String documento) {
        return clienteRepository.buscarPorId(documento);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.listarTodos();
    }
}
