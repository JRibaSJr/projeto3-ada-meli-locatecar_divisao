package team3.repository;

import java.util.List;
import java.util.Optional;

// T é o tipo da entidade (ex: Veiculo)
// ID é o tipo do identificador (ex: String para a placa)
public interface IRepository<T, ID> {
    void cadastrar(T entidade);
    void alterar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}
