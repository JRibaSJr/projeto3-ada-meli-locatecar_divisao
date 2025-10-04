package team3.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

// T é o tipo da entidade (ex: Veiculo)
// ID é o tipo do identificador (ex: String para a placa)
public interface IRepository<T, ID> {
    void cadastrar(T entidade);
    void alterar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    
    // Novos métodos com Streams
    Stream<T> stream();
    List<T> buscarComFiltro(Predicate<T> filtro);
    List<T> listarComPaginacao(int pagina, int tamanhoPagina);
    long contarTotal();
    
    // Persistência
    void salvarDados() throws Exception;
    void carregarDados() throws Exception;
}