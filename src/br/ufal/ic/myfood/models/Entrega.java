package br.ufal.ic.myfood.models;

import java.io.Serializable;

/**
 * Representa uma entrega criada quando um entregador assume um pedido.
 *
 * Atributos consultáveis via getEntrega:
 *   cliente, empresa, pedido, entregador, destino, produtos
 */
public class Entrega implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    id;
    private int    pedidoNumero;
    private String entregadorId;
    private String destino;

    public Entrega(int id, int pedidoNumero, String entregadorId, String destino) {
        this.id            = id;
        this.pedidoNumero  = pedidoNumero;
        this.entregadorId  = entregadorId;
        this.destino       = destino;
    }

    public int    getId()           { return id; }
    public int    getPedidoNumero() { return pedidoNumero; }
    public String getEntregadorId() { return entregadorId; }
    public String getDestino()      { return destino; }
}
