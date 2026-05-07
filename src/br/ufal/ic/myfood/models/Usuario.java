package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa um usuário do sistema MyFood.
 *
 * Tipos identificados pelos campos preenchidos:
 *   - Cliente:    sem cpf, sem veiculo/placa
 *   - Dono:       com cpf
 *   - Entregador: com veiculo e placa (sem cpf)
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 2L;

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String endereco;

    // Dono de empresa
    private String cpf;

    // Entregador
    private String veiculo;
    private String placa;

    // Empresas a que o entregador pertence (IDs)
    private List<Integer> empresasVinculadas;

    public Usuario(String nome, String email, String senha, String endereco) {
        this(nome, email, senha, endereco, null, null, null);
    }

    public Usuario(String nome, String email, String senha, String endereco, String cpf) {
        this(nome, email, senha, endereco, cpf, null, null);
    }

    public Usuario(String nome, String email, String senha, String endereco,
                   String cpf, String veiculo, String placa) {
        this.nome              = nome;
        this.email             = email;
        this.senha             = senha;
        this.endereco          = endereco;
        this.cpf               = cpf;
        this.veiculo           = veiculo;
        this.placa             = placa;
        this.id                = UUID.randomUUID().toString();
        this.empresasVinculadas = new ArrayList<>();
    }

    // --- Tipo helpers ---

    public boolean isDono()       { return cpf != null && !cpf.isEmpty(); }
    public boolean isEntregador() { return veiculo != null && !veiculo.isEmpty(); }
    public boolean isCliente()    { return !isDono() && !isEntregador(); }

    // --- Getters e Setters ---

    public String getId()       { return id; }
    public void   setId(String id) { this.id = id; }

    public String getNome()     { return nome; }
    public void   setNome(String nome) { this.nome = nome; }

    public String getEmail()    { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getSenha()    { return senha; }
    public void   setSenha(String senha) { this.senha = senha; }

    public String getEndereco() { return endereco; }
    public void   setEndereco(String endereco) { this.endereco = endereco; }

    public String getCpf()      { return cpf; }
    public void   setCpf(String cpf) { this.cpf = cpf; }

    public String getVeiculo()  { return veiculo; }
    public void   setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public String getPlaca()    { return placa; }
    public void   setPlaca(String placa) { this.placa = placa; }

    public List<Integer> getEmpresasVinculadas() { return empresasVinculadas; }

    public void vincularEmpresa(int empresaId) {
        if (!empresasVinculadas.contains(empresaId)) {
            empresasVinculadas.add(empresaId);
        }
    }
}
