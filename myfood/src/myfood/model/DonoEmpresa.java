package myfood.model;

import java.io.Serializable;

public class DonoEmpresa extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cpf;

    public DonoEmpresa() {}

    public DonoEmpresa(int id, String nome, String email, String senha, String endereco, String cpf) {
        super(id, nome, email, senha, endereco);
        this.cpf = cpf;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    @Override
    public boolean isDonoEmpresa() { return true; }
}
