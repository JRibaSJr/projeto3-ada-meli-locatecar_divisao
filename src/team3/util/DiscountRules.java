package team3.util;

import team3.domain.functional.DiscountCalculator;
import team3.domain.model.PessoaFisica;
import team3.domain.model.PessoaJuridica;

public class DiscountRules {

    // Interface funcional personalizada para cálculo de desconto
    public static final DiscountCalculator STANDARD_DISCOUNT = (cliente, diarias) -> {
        if (cliente instanceof PessoaFisica && diarias > 5) {
            return 0.05; // 5% para PF acima de 5 diárias
        }
        if (cliente instanceof PessoaJuridica && diarias > 3) {
            return 0.10; // 10% para PJ acima de 3 diárias
        }
        return 0.0;
    };

    // Desconto promocional adicional
    public static final DiscountCalculator PROMOTIONAL_DISCOUNT = (cliente, diarias) -> {
        double standardDiscount = STANDARD_DISCOUNT.calculateDiscount(cliente, diarias);
        if (diarias > 10) {
            return Math.max(standardDiscount, 0.15); // 15% para aluguéis longos
        }
        return standardDiscount;
    };

    // Desconto combinado usando composição de funções
    public static DiscountCalculator combineDiscounts(DiscountCalculator... calculators) {
        return (cliente, diarias) -> {
            double maxDiscount = 0.0;
            for (DiscountCalculator calculator : calculators) {
                maxDiscount = Math.max(maxDiscount, calculator.calculateDiscount(cliente, diarias));
            }
            return Math.min(maxDiscount, 0.20); // Máximo de 20% de desconto
        };
    }
}