package team3.util;

import team3.domain.functional.ValidationRule;

import java.util.function.Predicate;

public class ValidationUtils {

    // Validação de CPF usando Predicate (simplificada para o projeto)
    public static final Predicate<String> CPF_VALIDATOR = cpf -> {
        if (cpf == null) {
            return false;
        }
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        // Aceita CPFs com 11 dígitos ou padrões de teste
        return cleanCpf.length() == 11 && !cleanCpf.matches("(\\d)\\1{10}");
    };

    // Validação de CNPJ usando Predicate (simplificada para o projeto)
    public static final Predicate<String> CNPJ_VALIDATOR = cnpj -> {
        if (cnpj == null) {
            return false;
        }
        String cleanCnpj = cnpj.replaceAll("[^0-9]", "");
        
        // Aceita CNPJs with 14 dígitos e que não sejam todos iguais
        return cleanCnpj.length() == 14 && !cleanCnpj.matches("(\\d)\\1{13}");
    };

    // Validação de email usando Predicate
    public static final Predicate<String> EMAIL_VALIDATOR = email -> 
        email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Validação de telefone usando Predicate
    public static final Predicate<String> PHONE_VALIDATOR = phone -> 
        phone != null && phone.replaceAll("[^0-9]", "").length() >= 10;

    // Validação de placa usando Predicate
    public static final Predicate<String> PLACA_VALIDATOR = placa -> 
        placa != null && placa.matches("^[A-Z]{3}-[0-9][A-Z0-9][0-9]{2}$");

    // Interface funcional personalizada para validação completa
    public static final ValidationRule<String> DOCUMENTO_VALIDATOR = documento -> 
        CPF_VALIDATOR.test(documento) || CNPJ_VALIDATOR.test(documento);
}