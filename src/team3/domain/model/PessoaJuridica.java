package team3.domain.model;

public class PessoaJuridica extends Cliente {
    private final String cnpj;

    public PessoaJuridica(String nome, String email, String telefone, String cnpj) {
        this.setNome(nome);
        this.setEmail(email);
        this.setTelefone(telefone);
        this.cnpj = cnpj;
    }

    @Override
    public String getDocumento() {
        return this.cnpj;
    }

    @Override
    public String toString() {
        return "PessoaJuridica {" +
                "cnpj='" + cnpj + '\'' + ", " +
                super.toString() +
                '}';
    }
}
