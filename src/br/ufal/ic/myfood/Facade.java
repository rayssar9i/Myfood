package br.ufal.ic.myfood;

import br.ufal.ic.myfood.models.*;

/**
 * Facade do sistema MyFood — ponto de entrada do EasyAccept.
 *
 * Cada método público corresponde a um comando nos scripts de teste (.txt).
 * Nomes e assinaturas devem corresponder exatamente ao que os scripts esperam.
 */
public class Facade {

    private SystemState state;

    private SystemState getState() {
        if (state == null) state = SystemState.carregar();
        return state;
    }

    // CONTROLE DO SISTEMA

    public void zerarSistema() {
        SystemState.apagarArquivo();
        this.state = new SystemState();
    }

    public void encerrarSistema() {
        getState().salvar();
    }

    // US1 — USUÁRIOS
    public String getAtributoUsuario(String id, String atributo) throws Exception {
        return getState().usuarioManager.getAtributoUsuario(id, atributo);
    }

    /** Cria cliente (sem CPF). */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco) throws Exception {
        getState().usuarioManager.criarUsuario(nome, email, senha, endereco, null);
    }

    /** Cria dono de empresa (com CPF). */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String cpf) throws Exception {
        if (cpf == null || cpf.isEmpty()) throw new Exception("CPF invalido");
        getState().usuarioManager.criarUsuario(nome, email, senha, endereco, cpf);
    }

    /** Cria entregador (com veiculo e placa). */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String veiculo, String placa) throws Exception {
        getState().usuarioManager.criarEntregador(nome, email, senha, endereco, veiculo, placa);
    }

    public String login(String email, String senha) throws Exception {
        return getState().usuarioManager.login(email, senha);
    }

    // US2 — EMPRESAS (restaurante)

    /** Cria restaurante. */
    public int criarEmpresa(String tipoEmpresa, String dono, String nome,
                            String endereco, String tipoCozinha) throws Exception {
        Usuario donoObj = getState().usuarioManager.buscarPorId(dono);
        if (donoObj == null) throw new Exception("Usuario nao encontrado");
        return getState().empresaManager.criarEmpresa(
                tipoEmpresa, dono, nome, endereco, tipoCozinha, donoObj);
    }

    public String getEmpresasDoUsuario(String idDono) throws Exception {
        Usuario dono = getState().usuarioManager.buscarPorId(idDono);
        if (dono == null) throw new Exception("Usuario nao encontrado");
        return getState().empresaManager.getEmpresasDoUsuario(idDono, dono);
    }

    public int getIdEmpresa(String idDono, String nome, int indice) throws Exception {
        return getState().empresaManager.getIdEmpresa(idDono, nome, indice);
    }

    public String getAtributoEmpresa(int empresa, String atributo) throws Exception {
        return getState().empresaManager.getAtributoEmpresa(
                empresa, atributo, getState().usuarioManager);
    }

    // US5 — MERCADOS

    /** Cria mercado (7 parâmetros: inclui abre, fecha, tipoMercado). */
    public int criarEmpresa(String tipoEmpresa, String dono, String nome, String endereco,
                            String abre, String fecha, String tipoMercado) throws Exception {
        Usuario donoObj = getState().usuarioManager.buscarPorId(dono);
        if (donoObj == null) throw new Exception("Usuario nao encontrado");
        return getState().empresaManager.criarEmpresaMercado(
                tipoEmpresa, dono, nome, endereco, abre, fecha, tipoMercado, donoObj);
    }

    public void alterarFuncionamento(int mercado, String abre, String fecha) throws Exception {
        getState().empresaManager.alterarFuncionamento(mercado, abre, fecha);
    }

    // US6 — FARMACIAS

    /** Cria farmacia (6 parâmetros: inclui aberto24Horas, numeroFuncionarios). */
    public int criarEmpresa(String tipoEmpresa, String dono, String nome, String endereco,
                            boolean aberto24Horas, int numeroFuncionarios) throws Exception {
        Usuario donoObj = getState().usuarioManager.buscarPorId(dono);
        if (donoObj == null) throw new Exception("Usuario nao encontrado");
        return getState().empresaManager.criarEmpresaFarmacia(
                tipoEmpresa, dono, nome, endereco, aberto24Horas, numeroFuncionarios, donoObj);
    }
    // US3 — PRODUTOS

    public int criarProduto(int empresa, String nome, float valor,
                            String categoria) throws Exception {
        return getState().produtoManager.criarProduto(empresa, nome, valor, categoria);
    }

    public void editarProduto(int produto, String nome, float valor,
                              String categoria) throws Exception {
        getState().produtoManager.editarProduto(produto, nome, valor, categoria);
    }

    public String getProduto(String nome, int empresa, String atributo) throws Exception {
        return getState().produtoManager.getProduto(
                nome, empresa, atributo, getState().empresaManager);
    }

    public String listarProdutos(int empresa) throws Exception {
        return getState().produtoManager.listarProdutos(
                empresa, getState().empresaManager);
    }

    // US4 — PEDIDOS

    public int criarPedido(String cliente, int empresa) throws Exception {
        Usuario clienteObj = getState().usuarioManager.buscarPorId(cliente);
        if (clienteObj == null) throw new Exception("Usuario nao encontrado");
        return getState().pedidoManager.criarPedido(cliente, empresa, clienteObj);
    }

    public void adicionarProduto(int numero, int produto) throws Exception {
        getState().pedidoManager.adicionarProduto(
                numero, produto, getState().produtoManager);
    }

    public String getPedidos(int pedido, String atributo) throws Exception {
        return getState().pedidoManager.getPedidos(
                pedido, atributo,
                getState().usuarioManager,
                getState().empresaManager,
                getState().produtoManager);
    }

    public void fecharPedido(int numero) throws Exception {
        getState().pedidoManager.fecharPedido(numero);
    }

    public void removerProduto(int pedido, String produto) throws Exception {
        getState().pedidoManager.removerProduto(
                pedido, produto, getState().produtoManager);
    }

    public int getNumeroPedido(String cliente, int empresa, int indice) throws Exception {
        return getState().pedidoManager.getNumeroPedido(cliente, empresa, indice);
    }

    // US7 — ENTREGADORES

    public void cadastrarEntregador(int empresa, String entregador) throws Exception {
        getState().usuarioManager.cadastrarEntregador(empresa, entregador);
    }

    public String getEntregadores(int empresa) throws Exception {
        return getState().usuarioManager.getEntregadores(empresa);
    }

    public String getEmpresas(String entregador) throws Exception {
        return getState().usuarioManager.getEmpresas(entregador, getState().empresaManager);
    }

    // US8 — SISTEMA DE ENTREGAS

    public void liberarPedido(int numero) throws Exception {
        getState().pedidoManager.liberarPedido(numero);
    }

    public int obterPedido(String entregador) throws Exception {
        return getState().pedidoManager.obterPedido(
                entregador,
                getState().usuarioManager,
                getState().empresaManager);
    }

    public int criarEntrega(int pedido, String entregador, String destino) throws Exception {
        return getState().entregaManager.criarEntrega(
                pedido, entregador, destino,
                getState().pedidoManager,
                getState().usuarioManager,
                getState().empresaManager);
    }

    public String getEntrega(int id, String atributo) throws Exception {
        return getState().entregaManager.getEntrega(
                id, atributo,
                getState().pedidoManager,
                getState().usuarioManager,
                getState().empresaManager,
                getState().produtoManager);
    }

    public int getIdEntrega(int pedido) throws Exception {
        return getState().entregaManager.getIdEntrega(pedido);
    }

    public void entregar(int entrega) throws Exception {
        getState().entregaManager.entregar(entrega, getState().pedidoManager);
    }

    // MAIN — executa todos os testes EasyAccept

    public static void main(String[] args) {
        // US1
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_2.txt"});
        
        // US2
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_2.txt"});
        
        // US3
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_2.txt"});
        
        // US4
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_2.txt"});
        
        // US5
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us5_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us5_2.txt"});
        
        // US6
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us6_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us6_2.txt"});
        
        // US7
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us7_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us7_2.txt"});
        
        // US8
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us8_1.txt"});
        easyaccept.EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us8_2.txt"});
    }
}
