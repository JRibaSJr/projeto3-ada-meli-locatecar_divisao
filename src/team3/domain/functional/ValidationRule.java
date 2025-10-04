package team3.domain.functional;

@FunctionalInterface
public interface ValidationRule<T> {
    boolean isValid(T entity);
}