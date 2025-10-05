package team3.domain.functional;

import team3.domain.model.Cliente;

@FunctionalInterface
public interface DiscountCalculator {
    double calculateDiscount(Cliente cliente, long diarias);
}