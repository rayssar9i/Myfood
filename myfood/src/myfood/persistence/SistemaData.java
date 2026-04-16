package myfood.persistence;

import myfood.model.*;
import java.io.Serializable;
import java.util.*;

public class SistemaData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private Map<Integer, Empresa> empresas = new LinkedHashMap<>();
    private Map<Integer, Produto> produtos = new LinkedHashMap<>();
    private Map<Integer, Pedido> pedidos = new LinkedHashMap<>();

    private int nextUserId = 1;
    private int nextEmpresaId = 1;
    private int nextProdutoId = 1;
    private int nextPedidoNum = 1;

    public Map<Integer, Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(Map<Integer, Usuario> usuarios) { this.usuarios = usuarios; }

    public Map<Integer, Empresa> getEmpresas() { return empresas; }
    public void setEmpresas(Map<Integer, Empresa> empresas) { this.empresas = empresas; }

    public Map<Integer, Produto> getProdutos() { return produtos; }
    public void setProdutos(Map<Integer, Produto> produtos) { this.produtos = produtos; }

    public Map<Integer, Pedido> getPedidos() { return pedidos; }
    public void setPedidos(Map<Integer, Pedido> pedidos) { this.pedidos = pedidos; }

    public int getNextUserId() { return nextUserId; }
    public void setNextUserId(int nextUserId) { this.nextUserId = nextUserId; }

    public int getNextEmpresaId() { return nextEmpresaId; }
    public void setNextEmpresaId(int nextEmpresaId) { this.nextEmpresaId = nextEmpresaId; }

    public int getNextProdutoId() { return nextProdutoId; }
    public void setNextProdutoId(int nextProdutoId) { this.nextProdutoId = nextProdutoId; }

    public int getNextPedidoNum() { return nextPedidoNum; }
    public void setNextPedidoNum(int nextPedidoNum) { this.nextPedidoNum = nextPedidoNum; }

    public int allocUserId() { return nextUserId++; }
    public int allocEmpresaId() { return nextEmpresaId++; }
    public int allocProdutoId() { return nextProdutoId++; }
    public int allocPedidoNum() { return nextPedidoNum++; }
}
