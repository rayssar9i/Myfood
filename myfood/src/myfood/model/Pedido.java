package myfood.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numero;
    private int clienteId;
    private int empresaId;
    private String estado; // "aberto" ou "preparando"
    private List<Integer> produtoIds; // lista de ids (pode repetir)

    public Pedido() {
        this.produtoIds = new ArrayList<>();
    }

    public Pedido(int numero, int clienteId, int empresaId) {
        this.numero = numero;
        this.clienteId = clienteId;
        this.empresaId = empresaId;
        this.estado = "aberto";
        this.produtoIds = new ArrayList<>();
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getEmpresaId() { return empresaId; }
    public void setEmpresaId(int empresaId) { this.empresaId = empresaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Integer> getProdutoIds() { return produtoIds; }
    public void setProdutoIds(List<Integer> produtoIds) { this.produtoIds = produtoIds; }

    public boolean isAberto() { return "aberto".equals(estado); }
}
