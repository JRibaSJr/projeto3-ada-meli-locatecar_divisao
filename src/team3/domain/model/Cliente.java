package team3.domain.model;

import java.io.Serializable;

public abstract class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String email;
    private String telefone;

    public abstract String getDocumento();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'';
    }
}