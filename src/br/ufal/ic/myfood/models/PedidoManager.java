package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia todas as operações relacionadas a Pedidos.
 */
public class PedidoManager implements Serializable {

    private static final long serialVersionUID = 2L;

    private List<Pedido> pedidos;
    private int proximoNumero;

    public PedidoManager() {
        this.pedidos       = new ArrayList<>();
        this.proximoNumero = 1;
    }

    
    // CRIAÇÃO

    public int criarPedido(String clienteId, int empresaId, Usuario cliente) throws Exception {

        if (cliente.isDono()) {
            throw new Exception("Dono de empresa nao pode fazer um pedido");
        }

        boolean temAberto = pedidos.stream()
                .anyMatch(p -> p.getClienteId().equals(clienteId)
                        && p.getEmpresaId() == empresaId
                        && p.isAberto());

        if (temAberto) {
            throw new Exception("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
        }

        pedidos.add(new Pedido(proximoNumero, clienteId, empresaId));
        return proximoNumero++;
    }

    
    // PRODUTOS

    public void adicionarProduto(int numeroPedido, int produtoId, ProdutoManager produtoManager) throws Exception {

        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Nao existe pedido em aberto");

        if (!pedido.isAberto()) {
            throw new Exception("Nao e possivel adcionar produtos a um pedido fechado");
        }

        Produto produto = produtoManager.buscarPorId(produtoId);
        if (produto == null || produto.getEmpresaId() != pedido.getEmpresaId()) {
            throw new Exception("O produto nao pertence a essa empresa");
        }

        pedido.adicionarProduto(produtoId);
    }

    public void removerProduto(int numeroPedido, String nomeProduto,ProdutoManager produtoManager) throws Exception {

        if (nomeProduto == null || nomeProduto.isEmpty()) {
            throw new Exception("Produto invalido");
        }

        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");

        if (!pedido.isAberto()) {
            throw new Exception("Nao e possivel remover produtos de um pedido fechado");
        }

        Produto produto = produtoManager.buscarPorNomeNaEmpresa(nomeProduto, pedido.getEmpresaId());
        if (produto == null || !pedido.getProdutosIds().contains(produto.getId())) {
            throw new Exception("Produto nao encontrado");
        }

        pedido.removerProduto(produto.getId());
    }

    // ESTADO
    public void fecharPedido(int numeroPedido) throws Exception {
        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");
        pedido.fechar();
    }

    /** Muda estado de "preparando" para "pronto" (disponível para entrega). */
    public void liberarPedido(int numeroPedido) throws Exception {
        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");

        if (pedido.isPronto()) {
            throw new Exception("Pedido ja liberado");
        }
        if (!"preparando".equals(pedido.getEstado())) {
            throw new Exception("Nao e possivel liberar um produto que nao esta sendo preparado");
        }

        pedido.liberar();
    }

    /**
     * Retorna o pedido "pronto" mais antigo disponível para o entregador.
     * Pedidos de farmácia têm prioridade.
     */
    public int obterPedido(String entregadorId, UsuarioManager usuarioManager,
                           EmpresaManager empresaManager) throws Exception {

        Usuario entregador = usuarioManager.buscarPorId(entregadorId);
        if (entregador == null || !entregador.isEntregador()) {
            throw new Exception("Usuario nao e um entregador");
        }

        List<Integer> empresasDoEntregador = entregador.getEmpresasVinculadas();
        if (empresasDoEntregador.isEmpty()) {
            throw new Exception("Entregador nao estar em nenhuma empresa.");
        }

        // Pedidos prontos para empresas do entregador
        List<Pedido> candidatos = pedidos.stream()
                .filter(p -> p.isPronto() && empresasDoEntregador.contains(p.getEmpresaId()))
                .sorted((a, b) -> Integer.compare(a.getNumero(), b.getNumero()))
                .collect(Collectors.toList());

        if (candidatos.isEmpty()) {
            throw new Exception("Nao existe pedido para entrega");
        }

        // Farmácia tem prioridade
        for (Pedido p : candidatos) {
            Empresa emp = empresaManager.buscarPorId(p.getEmpresaId());
            if (emp != null && emp.isFarmacia()) {
                return p.getNumero();
            }
        }

        // Nenhuma farmácia — retorna o mais antigo disponível
        return candidatos.get(0).getNumero();
    }

    // CONSULTAS

    public String getPedidos(int numeroPedido, String atributo, UsuarioManager usuarioManager, EmpresaManager empresaManager, ProdutoManager produtoManager) throws Exception {

        if (atributo == null || atributo.isEmpty()) throw new Exception("Atributo invalido");

        Pedido pedido = buscarPorNumero(numeroPedido);
        if (pedido == null) throw new Exception("Pedido nao encontrado");

        switch (atributo.toLowerCase()) {
            case "cliente":
                Usuario cliente = usuarioManager.buscarPorId(pedido.getClienteId());
                return cliente != null ? cliente.getNome() : "";

            case "empresa":
                Empresa empresa = empresaManager.buscarPorId(pedido.getEmpresaId());
                return empresa != null ? empresa.getNome() : "";

            case "estado":
                return pedido.getEstado();

            case "produtos":
                List<String> nomes = pedido.getProdutosIds().stream()
                        .map(id -> {
                            Produto p = produtoManager.buscarPorId(id);
                            return p != null ? p.getNome() : "";
                        })
                        .collect(Collectors.toList());
                return "{[" + String.join(", ", nomes) + "]}";

            case "valor":
                float total = 0f;
                for (int id : pedido.getProdutosIds()) {
                    Produto p = produtoManager.buscarPorId(id);
                    if (p != null) total += p.getValor();
                }
                return String.format(java.util.Locale.US, "%.2f", total);

            default:
                throw new Exception("Atributo nao existe");
        }
    }

    public int getNumeroPedido(String clienteId, int empresaId, int indice) throws Exception {
        List<Pedido> filtrados = pedidos.stream()
                .filter(p -> p.getClienteId().equals(clienteId) && p.getEmpresaId() == empresaId)
                .sorted((a, b) -> Integer.compare(a.getNumero(), b.getNumero()))
                .collect(Collectors.toList());

        if (filtrados.isEmpty() || indice >= filtrados.size()) {
            throw new Exception("Pedido nao encontrado");
        }

        return filtrados.get(indice).getNumero();
    }

    public Pedido buscarPorNumero(int numero) {
        return pedidos.stream()
                .filter(p -> p.getNumero() == numero)
                .findFirst()
                .orElse(null);
    }
}
