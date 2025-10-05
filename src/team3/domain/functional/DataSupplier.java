package team3.domain.functional;

import java.util.List;
import java.util.function.Supplier;

@FunctionalInterface
public interface DataSupplier<T> extends Supplier<List<T>> {
    @Override
    List<T> get();
}