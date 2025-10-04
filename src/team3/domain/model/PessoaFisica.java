package team3.domain.model;

public class PessoaFisica extends Cliente {
    private final String cpf;

    public PessoaFisica(String nome, String email, String telefone, String cpf) {
        this.setNome(nome);
        this.setEmail(email);
        this.setTelefone(telefone);
        this.cpf = cpf;
    }

    @Override
    public String getDocumento() {
        return this.cpf;
    }

    @Override
    public String toString() {
        return "PessoaFisica {" +
                "cpf='" + cpf + '\'' + ", " +
                super.toString() +
                '}';
    }
}
