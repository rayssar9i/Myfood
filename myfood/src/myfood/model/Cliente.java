package myfood.model;

import java.io.Serializable;

public class Cliente extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    public Cliente() {}

    public Cliente(int id, String nome, String email, String senha, String endereco) {
        super(id, nome, email, senha, endereco);
    }

    @Override
    public boolean isDonoEmpresa() { return false; }
}
