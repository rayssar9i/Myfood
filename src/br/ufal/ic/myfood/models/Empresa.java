package br.ufal.ic.myfood.models;

import java.io.Serializable;

/**
 * Representa uma empresa cadastrada no sistema.
 * Tipos suportados: "restaurante", "mercado", "farmacia".
 *
 * Regras:
 *   - Dois donos DIFERENTES não podem ter empresas com o mesmo nome.
 *   - O MESMO dono não pode ter duas empresas com mesmo nome E mesmo endereço.
 *   - Apenas usuários com CPF (donos) podem criar empresas.
 */
public class Empresa implements Serializable {

    private static final long serialVersionUID = 2L;

    private int    id;
    private String tipoEmpresa;
    private String nome;
    private String endereco;
    private String donoId;

    // Restaurante
    private String tipoCozinha;

    // Mercado
    private String abre;
    private String fecha;
    private String tipoMercado;

    // Farmacia
    private boolean aberto24Horas;
    private int     numeroFuncionarios;

    /** Construtor para restaurante. */
    public Empresa(int id, String nome, String endereco, String tipoCozinha, String donoId) {
        this.id          = id;
        this.tipoEmpresa = "restaurante";
        this.nome        = nome;
        this.endereco    = endereco;
        this.tipoCozinha = tipoCozinha;
        this.donoId      = donoId;
    }

    /** Construtor para mercado. */
    public Empresa(int id, String nome, String endereco, String donoId,
                   String abre, String fecha, String tipoMercado) {
        this.id          = id;
        this.tipoEmpresa = "mercado";
        this.nome        = nome;
        this.endereco    = endereco;
        this.donoId      = donoId;
        this.abre        = abre;
        this.fecha       = fecha;
        this.tipoMercado = tipoMercado;
    }

    /** Construtor para farmacia. */
    public Empresa(int id, String nome, String endereco, String donoId,
                   boolean aberto24Horas, int numeroFuncionarios) {
        this.id                 = id;
        this.tipoEmpresa        = "farmacia";
        this.nome               = nome;
        this.endereco           = endereco;
        this.donoId             = donoId;
        this.aberto24Horas      = aberto24Horas;
        this.numeroFuncionarios = numeroFuncionarios;
    }

    // --- Getters e Setters ---

    public int    getId()          { return id; }

    public String getTipoEmpresa() { return tipoEmpresa; }

    public String getNome()        { return nome; }
    public void   setNome(String nome) { this.nome = nome; }

    public String getEndereco()    { return endereco; }
    public void   setEndereco(String endereco) { this.endereco = endereco; }

    public String getDonoId()      { return donoId; }
    public void   setDonoId(String donoId) { this.donoId = donoId; }

    public String getTipoCozinha() { return tipoCozinha; }
    public void   setTipoCozinha(String tipoCozinha) { this.tipoCozinha = tipoCozinha; }

    public String getAbre()        { return abre; }
    public void   setAbre(String abre) { this.abre = abre; }

    public String getFecha()       { return fecha; }
    public void   setFecha(String fecha) { this.fecha = fecha; }

    public String getTipoMercado() { return tipoMercado; }
    public void   setTipoMercado(String tipoMercado) { this.tipoMercado = tipoMercado; }

    public boolean isAberto24Horas()             { return aberto24Horas; }
    public void    setAberto24Horas(boolean v)   { this.aberto24Horas = v; }

    public int  getNumeroFuncionarios()          { return numeroFuncionarios; }
    public void setNumeroFuncionarios(int v)     { this.numeroFuncionarios = v; }

    public boolean isFarmacia() { return "farmacia".equals(tipoEmpresa); }
    public boolean isMercado()  { return "mercado".equals(tipoEmpresa); }
}
