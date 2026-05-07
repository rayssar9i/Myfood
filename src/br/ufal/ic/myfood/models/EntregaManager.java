package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gerencia todas as operações relacionadas a Entregas.
 *
 * Responsabilidades:
 *   - Criação de entregas (criarEntrega)
 *   - Consulta de atributos de entrega (getEntrega)
 *   - Busca de entrega por pedido (getIdEntrega)
 *   - Conclusão de entrega (entregar)
 */
public class EntregaManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Entrega> entregas;
    private int proximoId;

    // Entregadores com entrega em andamento (não podem assumir novo pedido)
    private Set<String> entregadoresOcupados;

    public EntregaManager() {
        this.entregas             = new ArrayList<>();
        this.proximoId            = 1;
        this.entregadoresOcupados = new HashSet<>();
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO
    // -------------------------------------------------------------------------

    /**
     * Cria uma entrega para um pedido.
     * Ordem de validação:
     *   1. Pedido está pronto
     *   2. Entregador é válido e trabalha para a empresa do pedido
     *   3. Entregador não está ocupado
     */
    public int criarEntrega(int pedidoNumero, String entregadorId, String destino,
                            PedidoManager pedidoManager,
                            UsuarioManager usuarioManager,
                            EmpresaManager empresaManager) throws Exception {

        Pedido pedido = pedidoManager.buscarPorNumero(pedidoNumero);
        if (pedido == null || !pedido.isPronto()) {
            throw new Exception("Pedido nao esta pronto para entrega");
        }

        Usuario entregador = usuarioManager.buscarPorId(entregadorId);
        if (entregador == null || !entregador.isEntregador()
                || !entregador.getEmpresasVinculadas().contains(pedido.getEmpresaId())) {
            throw new Exception("Nao e um entregador valido");
        }

        if (entregadoresOcupados.contains(entregadorId)) {
            throw new Exception("Entregador ainda em entrega");
        }

        // Se destino não fornecido, usa endereço do cliente
        String enderecoFinal = (destino == null || destino.isEmpty())
                ? usuarioManager.buscarPorId(pedido.getClienteId()).getEndereco()
                : destino;

        entregas.add(new Entrega(proximoId, pedidoNumero, entregadorId, enderecoFinal));
        pedido.iniciarEntrega();
        entregadoresOcupados.add(entregadorId);

        return proximoId++;
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    public String getEntrega(int entregaId, String atributo,
                             PedidoManager pedidoManager,
                             UsuarioManager usuarioManager,
                             EmpresaManager empresaManager,
                             ProdutoManager produtoManager) throws Exception {

        if (atributo == null || atributo.isEmpty()) throw new Exception("Atributo invalido");

        Entrega entrega = buscarPorId(entregaId);
        if (entrega == null) throw new Exception("Entrega nao encontrada");

        Pedido pedido = pedidoManager.buscarPorNumero(entrega.getPedidoNumero());

        switch (atributo.toLowerCase()) {
            case "cliente":
                Usuario cliente = usuarioManager.buscarPorId(pedido.getClienteId());
                return cliente != null ? cliente.getNome() : "";

            case "empresa":
                Empresa empresa = empresaManager.buscarPorId(pedido.getEmpresaId());
                return empresa != null ? empresa.getNome() : "";

            case "pedido":
                return String.valueOf(entrega.getPedidoNumero());

            case "entregador":
                Usuario entregador = usuarioManager.buscarPorId(entrega.getEntregadorId());
                return entregador != null ? entregador.getNome() : "";

            case "destino":
                return entrega.getDestino();

            case "produtos":
                List<String> nomes = pedido.getProdutosIds().stream()
                        .map(id -> {
                            Produto p = produtoManager.buscarPorId(id);
                            return p != null ? p.getNome() : "";
                        })
                        .collect(Collectors.toList());
                return "{[" + String.join(", ", nomes) + "]}";

            default:
                throw new Exception("Atributo nao existe");
        }
    }

    /** Retorna o id da entrega associada ao número de pedido informado. */
    public int getIdEntrega(int pedidoNumero) throws Exception {
        return entregas.stream()
                .filter(e -> e.getPedidoNumero() == pedidoNumero)
                .mapToInt(Entrega::getId)
                .findFirst()
                .orElseThrow(() -> new Exception("Nao existe entrega com esse id"));
    }

    // -------------------------------------------------------------------------
    // CONCLUSÃO
    // -------------------------------------------------------------------------

    /** Marca o pedido como "entregue" e libera o entregador. */
    public void entregar(int entregaId, PedidoManager pedidoManager) throws Exception {
        Entrega entrega = buscarPorId(entregaId);
        if (entrega == null) throw new Exception("Nao existe nada para ser entregue com esse id");

        Pedido pedido = pedidoManager.buscarPorNumero(entrega.getPedidoNumero());
        if (pedido != null) pedido.finalizar();

        entregadoresOcupados.remove(entrega.getEntregadorId());
    }

    // -------------------------------------------------------------------------
    // BUSCA INTERNA
    // -------------------------------------------------------------------------

    private Entrega buscarPorId(int id) {
        return entregas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
