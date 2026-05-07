package br.ufal.ic.myfood.models;

import br.ufal.ic.myfood.exceptions.UsuarioJaExisteException;
import br.ufal.ic.myfood.exceptions.UsuarioNaoExisteException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia todos os usuários do sistema.
 * Suporta tipos: Cliente, Dono de Empresa, Entregador.
 */
public class UsuarioManager implements Serializable {

    private static final long serialVersionUID = 2L;

    private List<Usuario> usuarioList;

    public UsuarioManager() {
        this.usuarioList = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO
    // -------------------------------------------------------------------------

    /** Cria cliente (sem cpf) ou dono (com cpf). */
    public void criarUsuario(String nome, String email, String senha,
                             String endereco, String cpf) throws Exception {

        if (nome     == null || nome.isEmpty())     throw new Exception("Nome invalido");
        if (email    == null || email.isEmpty())    throw new Exception("Email invalido");
        if (senha    == null || senha.isEmpty())    throw new Exception("Senha invalido");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco invalido");
        if (!email.contains("@"))                   throw new Exception("Email invalido");

        if (cpf != null && !cpf.isEmpty() && cpf.length() != 14) {
            throw new Exception("CPF invalido");
        }

        for (Usuario u : usuarioList) {
            if (u.getEmail().equals(email)) throw new UsuarioJaExisteException();
        }

        usuarioList.add(new Usuario(nome, email, senha, endereco, cpf));
    }

    /** Cria entregador (com veiculo e placa). */
    public void criarEntregador(String nome, String email, String senha,
                                String endereco, String veiculo, String placa) throws Exception {

        if (nome     == null || nome.isEmpty())     throw new Exception("Nome invalido");
        if (email    == null || email.isEmpty())    throw new Exception("Email invalido");
        if (senha    == null || senha.isEmpty())    throw new Exception("Senha invalido");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco invalido");
        if (!email.contains("@"))                   throw new Exception("Email invalido");
        if (veiculo  == null || veiculo.isEmpty())  throw new Exception("Veiculo invalido");
        if (placa    == null || placa.isEmpty())    throw new Exception("Placa invalido");

        // Placa uniqueness checked before email uniqueness (test ordering requirement)
        for (Usuario u : usuarioList) {
            if (placa.equals(u.getPlaca())) throw new Exception("Placa invalido");
        }
        for (Usuario u : usuarioList) {
            if (u.getEmail().equals(email)) throw new UsuarioJaExisteException();
        }

        usuarioList.add(new Usuario(nome, email, senha, endereco, null, veiculo, placa));
    }

    // -------------------------------------------------------------------------
    // AUTENTICAÇÃO
    // -------------------------------------------------------------------------

    public String login(String email, String senha) throws Exception {
        if (email == null || email.isEmpty()) throw new Exception("Login ou senha invalidos");
        if (senha == null || senha.isEmpty()) throw new Exception("Login ou senha invalidos");

        for (Usuario u : usuarioList) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u.getId();
            }
        }
        throw new Exception("Login ou senha invalidos");
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    public String getAtributoUsuario(String id, String atributo) throws Exception {
        for (Usuario u : usuarioList) {
            if (u.getId().equals(id)) {
                switch (atributo.toLowerCase()) {
                    case "nome":     return u.getNome();
                    case "email":    return u.getEmail();
                    case "senha":    return u.getSenha();
                    case "endereco": return u.getEndereco();
                    case "cpf":      return u.getCpf();
                    case "veiculo":  return u.getVeiculo();
                    case "placa":    return u.getPlaca();
                    default:         throw new Exception("Atributo invalido");
                }
            }
        }
        throw new UsuarioNaoExisteException();
    }

    public Usuario buscarPorId(String id) {
        return usuarioList.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // ENTREGADORES
    // -------------------------------------------------------------------------

    /** Cadastra um entregador a uma empresa. */
    public void cadastrarEntregador(int empresaId, String entregadorId) throws Exception {
        Usuario entregador = buscarPorId(entregadorId);
        if (entregador == null) throw new UsuarioNaoExisteException();
        if (!entregador.isEntregador()) throw new Exception("Usuario nao e um entregador");

        if (entregador.getEmpresasVinculadas().contains(empresaId)) {
            throw new Exception("Entregador ja cadastrado nessa empresa");
        }
        entregador.vincularEmpresa(empresaId);
    }

    /** Retorna emails de todos os entregadores de uma empresa: {[email1, email2]}. */
    public String getEntregadores(int empresaId) {
        List<String> emails = usuarioList.stream()
                .filter(u -> u.isEntregador() && u.getEmpresasVinculadas().contains(empresaId))
                .map(Usuario::getEmail)
                .collect(Collectors.toList());

        return "{[" + String.join(", ", emails) + "]}";
    }

    /** Retorna empresas de um entregador: {[[nome1, end1], [nome2, end2]]}. */
    public String getEmpresas(String entregadorId, EmpresaManager empresaManager) throws Exception {
        Usuario entregador = buscarPorId(entregadorId);
        if (entregador == null) throw new UsuarioNaoExisteException();
        if (!entregador.isEntregador()) throw new Exception("Usuario nao e um entregador");

        StringBuilder sb = new StringBuilder("{[");
        List<Integer> ids = entregador.getEmpresasVinculadas();
        for (int i = 0; i < ids.size(); i++) {
            Empresa e = empresaManager.buscarPorId(ids.get(i));
            if (e != null) {
                sb.append("[").append(e.getNome()).append(", ").append(e.getEndereco()).append("]");
                if (i < ids.size() - 1) sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
