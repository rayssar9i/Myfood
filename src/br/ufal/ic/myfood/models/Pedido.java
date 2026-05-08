package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//
 // Representa um pedido feito por um cliente a uma empresa.
 // Ciclo de vida:
 //  "aberto"     → aceita adição/remoção de produtos
 //  "preparando" → fechado via fecharPedido()
 //  "pronto"     → liberado via liberarPedido(), disponível para entrega
 //  "entregando" → entregador assumiu via criarEntrega()
 //  "entregue"   → entregue via entregar()
 
public class Pedido implements Serializable {

    private static final long serialVersionUID = 2L;

    private int numero;
    private String clienteId;
    private int empresaId;
    private String estado;

    private List<Integer> produtosIds;

    public Pedido(int numero, String clienteId, int empresaId) {
        this.numero = numero;
        this.clienteId = clienteId;
        this.empresaId  = empresaId;
        this.estado  = "aberto";
        this.produtosIds = new ArrayList<>();
    }

    // --- Getters ---

    public int  getNumero()  { return numero; }
    public String  getClienteId() { return clienteId; }
    public int  getEmpresaId() { return empresaId; }
    public String  getEstado() { return estado; }
    public List<Integer> getProdutosIds() { return produtosIds; }

    // --- Ações de negócio ---

    public void adicionarProduto(int produtoId) { this.produtosIds.add(produtoId); }

    public boolean removerProduto(int produtoId) {
        return this.produtosIds.remove(Integer.valueOf(produtoId));
    }

    public void fechar() { this.estado = "preparando"; }
    public void liberar() { this.estado = "pronto"; }
    public void iniciarEntrega()  { this.estado = "entregando"; }
    public void finalizar() { this.estado = "entregue"; }

    public boolean isAberto()  { return "aberto".equals(this.estado); }
    public boolean isPronto(){ return "pronto".equals(this.estado); }
    public boolean isEntregando() { return "entregando".equals(this.estado); }
}
