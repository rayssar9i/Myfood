package myfood.facade;

import myfood.model.*;
import myfood.persistence.*;

import java.util.*;

public class MyFoodFacade {

    private SistemaData data;

    public MyFoodFacade() {
        this.data = PersistenceManager.load();
    }

    // -------------------------------------------------------------------------
    // zerarSistema
    // -------------------------------------------------------------------------
    public void zerarSistema() {
        data = new SistemaData();
        PersistenceManager.delete();
    }

    // -------------------------------------------------------------------------
    // encerrarSistema
    // -------------------------------------------------------------------------
    public void encerrarSistema() {
        PersistenceManager.save(data);
    }

    // -------------------------------------------------------------------------
    // criarUsuario (Cliente)
    // -------------------------------------------------------------------------
    public void criarUsuario(String nome, String email, String senha, String endereco) throws Exception {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);
        validarEndereco(endereco);
        verificarEmailUnico(email);

        int id = data.allocUserId();
        Cliente c = new Cliente(id, nome, email, senha, endereco);
        data.getUsuarios().put(id, c);
    }

    // -------------------------------------------------------------------------
    // criarUsuario (DonoEmpresa)
    // -------------------------------------------------------------------------
    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) throws Exception {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);
        validarEndereco(endereco);
        validarCpf(cpf);
        verificarEmailUnico(email);

        int id = data.allocUserId();
        DonoEmpresa d = new DonoEmpresa(id, nome, email, senha, endereco, cpf);
        data.getUsuarios().put(id, d);
    }

    // -------------------------------------------------------------------------
    // login
    // -------------------------------------------------------------------------
    public int login(String email, String senha) throws Exception {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new Exception("Login ou senha invalidos");
        }
        for (Usuario u : data.getUsuarios().values()) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u.getId();
            }
        }
        throw new Exception("Login ou senha invalidos");
    }

    // -------------------------------------------------------------------------
    // getAtributoUsuario
    // -------------------------------------------------------------------------
    public String getAtributoUsuario(int id, String atributo) throws Exception {
        Usuario u = data.getUsuarios().get(id);
        if (u == null) throw new Exception("Usuario nao cadastrado.");
        switch (atributo) {
            case "nome":     return u.getNome();
            case "email":    return u.getEmail();
            case "senha":    return u.getSenha();
            case "endereco": return u.getEndereco();
            case "cpf":
                if (u instanceof DonoEmpresa) return ((DonoEmpresa) u).getCpf();
                throw new Exception("Atributo invalido");
            default:
                throw new Exception("Atributo invalido");
        }
    }

    // -------------------------------------------------------------------------
    // criarEmpresa
    // -------------------------------------------------------------------------
    public int criarEmpresa(String tipoEmpresa, int donoId, String nome, String endereco, String tipoCozinha) throws Exception {
        Usuario u = data.getUsuarios().get(donoId);
        if (u == null || !u.isDonoEmpresa()) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }

        // Verifica nome duplicado de outros donos
        for (Empresa e : data.getEmpresas().values()) {
            if (e.getNome().equals(nome)) {
                if (e.getDonoId() != donoId) {
                    throw new Exception("Empresa com esse nome ja existe");
                }
                // Mesmo dono, mesmo nome - verifica endereço
                if (e.getEndereco().equals(endereco)) {
                    throw new Exception("Proibido cadastrar duas empresas com o mesmo nome e local");
                }
            }
        }

        int id = data.allocEmpresaId();
        Empresa emp = new Empresa(id, tipoEmpresa, donoId, nome, endereco, tipoCozinha);
        data.getEmpresas().put(id, emp);
        return id;
    }

    // -------------------------------------------------------------------------
    // getEmpresasDoUsuario
    // -------------------------------------------------------------------------
    public String getEmpresasDoUsuario(int donoId) throws Exception {
        Usuario u = data.getUsuarios().get(donoId);
        if (u == null || !u.isDonoEmpresa()) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }

        // Collect companies in insertion order (sorted by id)
        List<Empresa> lista = new ArrayList<>();
        for (Empresa e : data.getEmpresas().values()) {
            if (e.getDonoId() == donoId) lista.add(e);
        }
        lista.sort(Comparator.comparingInt(Empresa::getId));

        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("[").append(lista.get(i).getNome()).append(", ").append(lista.get(i).getEndereco()).append("]");
        }
        sb.append("]}");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // getAtributoEmpresa
    // -------------------------------------------------------------------------
    public String getAtributoEmpresa(int empresaId, String atributo) throws Exception {
        if (atributo == null || atributo.trim().isEmpty()) {
            // Check if empresa exists first
            Empresa e = data.getEmpresas().get(empresaId);
            if (e == null) throw new Exception("Empresa nao cadastrada");
            throw new Exception("Atributo invalido");
        }
        Empresa e = data.getEmpresas().get(empresaId);
        if (e == null) throw new Exception("Empresa nao cadastrada");

        switch (atributo) {
            case "nome":       return e.getNome();
            case "endereco":   return e.getEndereco();
            case "tipoCozinha": return e.getTipoCozinha();
            case "dono":
                Usuario u = data.getUsuarios().get(e.getDonoId());
                return u != null ? u.getNome() : "";
            default:
                throw new Exception("Atributo invalido");
        }
    }

    // -------------------------------------------------------------------------
    // getIdEmpresa
    // -------------------------------------------------------------------------
    public int getIdEmpresa(int donoId, String nome, int indice) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("Nome invalido");
        }
        if (indice < 0) {
            throw new Exception("Indice invalido");
        }

        List<Empresa> matching = new ArrayList<>();
        for (Empresa e : data.getEmpresas().values()) {
            if (e.getDonoId() == donoId && e.getNome().equals(nome)) {
                matching.add(e);
            }
        }
        matching.sort(Comparator.comparingInt(Empresa::getId));

        if (matching.isEmpty()) {
            throw new Exception("Nao existe empresa com esse nome");
        }
        if (indice >= matching.size()) {
            throw new Exception("Indice maior que o esperado");
        }
        return matching.get(indice).getId();
    }

    // -------------------------------------------------------------------------
    // criarProduto
    // -------------------------------------------------------------------------
    public int criarProduto(int empresaId, String nome, double valor, String categoria) throws Exception {
        if (nome == null || nome.trim().isEmpty()) throw new Exception("Nome invalido");
        if (valor < 0) throw new Exception("Valor invalido");
        if (categoria == null || categoria.trim().isEmpty()) throw new Exception("Categoria invalido");

        Empresa emp = data.getEmpresas().get(empresaId);
        if (emp == null) throw new Exception("Empresa nao encontrada");

        // Verifica nome duplicado na mesma empresa
        for (Produto p : data.getProdutos().values()) {
            if (p.getEmpresaId() == empresaId && p.getNome().equals(nome)) {
                throw new Exception("Ja existe um produto com esse nome para essa empresa");
            }
        }

        int id = data.allocProdutoId();
        Produto prod = new Produto(id, empresaId, nome, valor, categoria);
        data.getProdutos().put(id, prod);
        return id;
    }

    // -------------------------------------------------------------------------
    // editarProduto
    // -------------------------------------------------------------------------
    public void editarProduto(int produtoId, String nome, double valor, String categoria) throws Exception {
        if (nome == null || nome.trim().isEmpty()) throw new Exception("Nome invalido");
        if (valor < 0) throw new Exception("Valor invalido");
        if (categoria == null || categoria.trim().isEmpty()) throw new Exception("Categoria invalido");

        Produto p = data.getProdutos().get(produtoId);
        if (p == null) throw new Exception("Produto nao cadastrado");

        p.setNome(nome);
        p.setValor(valor);
        p.setCategoria(categoria);
    }

    // -------------------------------------------------------------------------
    // getProduto
    // -------------------------------------------------------------------------
    public String getProduto(String nome, int empresaId, String atributo) throws Exception {
        Produto found = null;
        for (Produto p : data.getProdutos().values()) {
            if (p.getEmpresaId() == empresaId && p.getNome().equals(nome)) {
                found = p;
                break;
            }
        }
        if (found == null) throw new Exception("Produto nao encontrado");

        switch (atributo) {
            case "nome":      return found.getNome();
            case "valor":     return formatValor(found.getValor());
            case "categoria": return found.getCategoria();
            case "empresa":
                Empresa e = data.getEmpresas().get(found.getEmpresaId());
                return e != null ? e.getNome() : "";
            default:
                throw new Exception("Atributo nao existe");
        }
    }

    // -------------------------------------------------------------------------
    // listarProdutos
    // -------------------------------------------------------------------------
    public String listarProdutos(int empresaId) throws Exception {
        Empresa emp = data.getEmpresas().get(empresaId);
        if (emp == null) throw new Exception("Empresa nao encontrada");

        List<Produto> lista = new ArrayList<>();
        for (Produto p : data.getProdutos().values()) {
            if (p.getEmpresaId() == empresaId) lista.add(p);
        }
        lista.sort(Comparator.comparingInt(Produto::getId));

        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(lista.get(i).getNome());
        }
        sb.append("]}");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // criarPedido
    // -------------------------------------------------------------------------
    public int criarPedido(int clienteId, int empresaId) throws Exception {
        Usuario u = data.getUsuarios().get(clienteId);
        if (u == null || u.isDonoEmpresa()) {
            throw new Exception("Dono de empresa nao pode fazer um pedido");
        }

        // Verifica se já existe pedido aberto do mesmo cliente na mesma empresa
        for (Pedido p : data.getPedidos().values()) {
            if (p.getClienteId() == clienteId && p.getEmpresaId() == empresaId && p.isAberto()) {
                throw new Exception("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
            }
        }

        int num = data.allocPedidoNum();
        Pedido pedido = new Pedido(num, clienteId, empresaId);
        data.getPedidos().put(num, pedido);
        return num;
    }

    // -------------------------------------------------------------------------
    // adicionarProduto
    // -------------------------------------------------------------------------
    public void adicionarProduto(int numero, int produtoId) throws Exception {
        Pedido pedido = data.getPedidos().get(numero);
        if (pedido == null) throw new Exception("Nao existe pedido em aberto");
        if (!pedido.isAberto()) throw new Exception("Nao e possivel adcionar produtos a um pedido fechado");

        Produto produto = data.getProdutos().get(produtoId);
        if (produto == null) throw new Exception("Produto nao encontrado");

        // Verifica se o produto pertence à empresa do pedido
        if (produto.getEmpresaId() != pedido.getEmpresaId()) {
            throw new Exception("O produto nao pertence a essa empresa");
        }

        pedido.getProdutoIds().add(produtoId);
    }

    // -------------------------------------------------------------------------
    // getPedidos
    // -------------------------------------------------------------------------
    public String getPedidos(int numero, String atributo) throws Exception {
        if (atributo == null || atributo.trim().isEmpty()) {
            throw new Exception("Atributo invalido");
        }
        Pedido pedido = data.getPedidos().get(numero);
        if (pedido == null) throw new Exception("Pedido nao encontrado");

        switch (atributo) {
            case "cliente": {
                Usuario u = data.getUsuarios().get(pedido.getClienteId());
                return u != null ? u.getNome() : "";
            }
            case "empresa": {
                Empresa e = data.getEmpresas().get(pedido.getEmpresaId());
                return e != null ? e.getNome() : "";
            }
            case "estado":
                return pedido.getEstado();
            case "produtos": {
                StringBuilder sb = new StringBuilder("{[");
                List<Integer> ids = pedido.getProdutoIds();
                for (int i = 0; i < ids.size(); i++) {
                    if (i > 0) sb.append(", ");
                    Produto p = data.getProdutos().get(ids.get(i));
                    sb.append(p != null ? p.getNome() : "?");
                }
                sb.append("]}");
                return sb.toString();
            }
            case "valor": {
                double total = 0;
                for (int pid : pedido.getProdutoIds()) {
                    Produto p = data.getProdutos().get(pid);
                    if (p != null) total += p.getValor();
                }
                return formatValor(total);
            }
            default:
                throw new Exception("Atributo nao existe");
        }
    }

    // -------------------------------------------------------------------------
    // fecharPedido
    // -------------------------------------------------------------------------
    public void fecharPedido(int numero) throws Exception {
        Pedido pedido = data.getPedidos().get(numero);
        if (pedido == null) throw new Exception("Pedido nao encontrado");
        pedido.setEstado("preparando");
    }

    // -------------------------------------------------------------------------
    // removerProduto
    // -------------------------------------------------------------------------
    public void removerProduto(int pedidoNum, String nomeProduto) throws Exception {
        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            throw new Exception("Produto invalido");
        }
        Pedido pedido = data.getPedidos().get(pedidoNum);
        if (pedido == null) throw new Exception("Pedido nao encontrado");
        if (!pedido.isAberto()) {
            throw new Exception("Nao e possivel remover produtos de um pedido fechado");
        }

        // Find the first occurrence of the product by name
        List<Integer> ids = pedido.getProdutoIds();
        for (int i = 0; i < ids.size(); i++) {
            Produto p = data.getProdutos().get(ids.get(i));
            if (p != null && p.getNome().equals(nomeProduto)) {
                ids.remove(i);
                return;
            }
        }
        throw new Exception("Produto nao encontrado");
    }

    // -------------------------------------------------------------------------
    // getNumeroPedido
    // -------------------------------------------------------------------------
    public int getNumeroPedido(int clienteId, int empresaId, int indice) throws Exception {
        List<Pedido> lista = new ArrayList<>();
        for (Pedido p : data.getPedidos().values()) {
            if (p.getClienteId() == clienteId && p.getEmpresaId() == empresaId) {
                lista.add(p);
            }
        }
        lista.sort(Comparator.comparingInt(Pedido::getNumero));

        if (indice >= lista.size()) {
            throw new Exception("Indice maior que o esperado");
        }
        return lista.get(indice).getNumero();
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private void validarNome(String nome) throws Exception {
        if (nome == null || nome.trim().isEmpty()) throw new Exception("Nome invalido");
    }

    private void validarEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) throw new Exception("Email invalido");
        // Basic format validation: must contain @
        if (!email.contains("@") || email.indexOf("@") == 0 || email.indexOf("@") == email.length() - 1) {
            throw new Exception("Email invalido");
        }
    }

    private void validarSenha(String senha) throws Exception {
        if (senha == null || senha.trim().isEmpty()) throw new Exception("Senha invalido");
    }

    private void validarEndereco(String endereco) throws Exception {
        if (endereco == null || endereco.trim().isEmpty()) throw new Exception("Endereco invalido");
    }

    private void validarCpf(String cpf) throws Exception {
        if (cpf == null || cpf.trim().isEmpty()) throw new Exception("CPF invalido");
        if (cpf.length() != 14) throw new Exception("CPF invalido");
    }

    private void verificarEmailUnico(String email) throws Exception {
        for (Usuario u : data.getUsuarios().values()) {
            if (u.getEmail().equals(email)) {
                throw new Exception("Conta com esse email ja existe");
            }
        }
    }

    private String formatValor(double valor) {
        return String.format("%.2f", valor);
    }
}
