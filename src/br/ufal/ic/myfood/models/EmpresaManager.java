package br.ufal.ic.myfood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia todas as operações relacionadas a Empresas.
 * Suporta os tipos: restaurante, mercado, farmacia.
 */
public class EmpresaManager implements Serializable {

    private static final long serialVersionUID = 2L;

    private List<Empresa> empresas;
    private int proximoId;

    public EmpresaManager() {
        this.empresas  = new ArrayList<>();
        this.proximoId = 1;
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO — RESTAURANTE
    // -------------------------------------------------------------------------

    public int criarEmpresa(String tipoEmpresa, String donoId, String nome,
                            String endereco, String tipoCozinha,
                            Usuario dono) throws Exception {

        validarPermissaoDono(dono);
        validarTipoEmpresa(tipoEmpresa);
        validarNome(nome);
        validarEndereco(endereco);
        verificarDuplicatas(donoId, nome, endereco);

        empresas.add(new Empresa(proximoId, nome, endereco, tipoCozinha, donoId));
        return proximoId++;
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO — MERCADO
    // -------------------------------------------------------------------------

    public int criarEmpresaMercado(String tipoEmpresa, String donoId, String nome,
                                   String endereco, String abre, String fecha,
                                   String tipoMercado, Usuario dono) throws Exception {

        validarPermissaoDono(dono);
        validarTipoEmpresa(tipoEmpresa);
        validarNome(nome);
        validarEndereco(endereco);
        validarHoraCriar(abre);
        validarHoraCriar(fecha);
        validarHorariosLogica(abre, fecha);
        validarTipoMercado(tipoMercado);
        verificarDuplicatas(donoId, nome, endereco);

        empresas.add(new Empresa(proximoId, nome, endereco, donoId, abre, fecha, tipoMercado));
        return proximoId++;
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO — FARMACIA
    // -------------------------------------------------------------------------

    public int criarEmpresaFarmacia(String tipoEmpresa, String donoId, String nome,
                                    String endereco, boolean aberto24Horas,
                                    int numeroFuncionarios, Usuario dono) throws Exception {

        validarPermissaoDono(dono);
        validarTipoEmpresa(tipoEmpresa);
        validarNome(nome);
        validarEndereco(endereco);
        verificarDuplicatas(donoId, nome, endereco);

        empresas.add(new Empresa(proximoId, nome, endereco, donoId, aberto24Horas, numeroFuncionarios));
        return proximoId++;
    }

    // -------------------------------------------------------------------------
    // ALTERAÇÃO DE FUNCIONAMENTO (Mercado)
    // -------------------------------------------------------------------------

    public void alterarFuncionamento(int mercadoId, String abre, String fecha) throws Exception {
        Empresa empresa = buscarPorId(mercadoId);
        if (empresa == null || !empresa.isMercado()) {
            throw new Exception("Nao e um mercado valido");
        }
        validarHoraAlterar(abre);
        validarHoraAlterar(fecha);
        validarHorariosLogica(abre, fecha);

        empresa.setAbre(abre);
        empresa.setFecha(fecha);
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    public String getEmpresasDoUsuario(String donoId, Usuario dono) throws Exception {
        if (dono.getCpf() == null || dono.getCpf().isEmpty()) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }

        List<Empresa> dosDono = empresas.stream()
                .filter(e -> e.getDonoId().equals(donoId))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < dosDono.size(); i++) {
            sb.append("[").append(dosDono.get(i).getNome())
              .append(", ").append(dosDono.get(i).getEndereco()).append("]");
            if (i < dosDono.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    public int getIdEmpresa(String donoId, String nome, int indice) throws Exception {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (indice < 0)                     throw new Exception("Indice invalido");

        List<Empresa> encontradas = empresas.stream()
                .filter(e -> e.getDonoId().equals(donoId) && e.getNome().equals(nome))
                .collect(Collectors.toList());

        if (encontradas.isEmpty())        throw new Exception("Nao existe empresa com esse nome");
        if (indice >= encontradas.size()) throw new Exception("Indice maior que o esperado");

        return encontradas.get(indice).getId();
    }

    public String getAtributoEmpresa(int empresaId, String atributo,
                                     UsuarioManager usuarioManager) throws Exception {

        Empresa empresa = buscarPorId(empresaId);
        if (empresa == null) throw new Exception("Empresa nao cadastrada");

        if (atributo == null || atributo.isEmpty()) throw new Exception("Atributo invalido");

        switch (atributo.toLowerCase()) {
            case "nome":        return empresa.getNome();
            case "endereco":    return empresa.getEndereco();
            case "tipocozinha": return empresa.getTipoCozinha();
            case "dono":
                Usuario dono = usuarioManager.buscarPorId(empresa.getDonoId());
                return dono != null ? dono.getNome() : "";
            // Mercado
            case "abre":        return empresa.getAbre();
            case "fecha":       return empresa.getFecha();
            case "tipomercado": return empresa.getTipoMercado();
            // Farmacia
            case "aberto24horas":        return String.valueOf(empresa.isAberto24Horas());
            case "numerofuncionarios":   return String.valueOf(empresa.getNumeroFuncionarios());
            default:
                throw new Exception("Atributo invalido");
        }
    }

    public Empresa buscarPorId(int id) {
        return empresas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // VALIDAÇÕES INTERNAS
    // -------------------------------------------------------------------------

    private void validarPermissaoDono(Usuario dono) throws Exception {
        if (dono.getCpf() == null || dono.getCpf().isEmpty()) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }
    }

    private void validarTipoEmpresa(String tipo) throws Exception {
        if (tipo == null || tipo.isEmpty()) throw new Exception("Tipo de empresa invalido");
    }

    private void validarNome(String nome) throws Exception {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
    }

    private void validarEndereco(String end) throws Exception {
        if (end == null || end.isEmpty()) throw new Exception("Endereco da empresa invalido");
    }

    private void validarTipoMercado(String tipo) throws Exception {
        if (tipo == null || tipo.isEmpty()) throw new Exception("Tipo de mercado invalido");
        if (!tipo.equals("supermercado") && !tipo.equals("minimercado") && !tipo.equals("atacadista")) {
            throw new Exception("Tipo de mercado invalido");
        }
    }

    private void verificarDuplicatas(String donoId, String nome, String endereco) throws Exception {
        for (Empresa e : empresas) {
            if (e.getNome().equals(nome) && !e.getDonoId().equals(donoId)) {
                throw new Exception("Empresa com esse nome ja existe");
            }
            if (e.getNome().equals(nome) && e.getDonoId().equals(donoId)
                    && e.getEndereco().equals(endereco)) {
                throw new Exception("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        }
    }

    /**
     * Valida formato de hora para criarEmpresa:
     *   null  → "Horario invalido"
     *   ""    → "Formato de hora invalido"
     *   bad pattern → "Formato de hora invalido"
     *   h>23 or m>59 → "Horario invalido"
     */
    private void validarHoraCriar(String hora) throws Exception {
        if (hora == null) throw new Exception("Horario invalido");
        if (hora.isEmpty()) throw new Exception("Formato de hora invalido");
        if (!hora.matches("\\d{2}:\\d{2}")) throw new Exception("Formato de hora invalido");
        int h = Integer.parseInt(hora.substring(0, 2));
        int m = Integer.parseInt(hora.substring(3, 5));
        if (h > 23 || m > 59) throw new Exception("Horario invalido");
    }

    /**
     * Valida formato de hora para alterarFuncionamento:
     *   null or "" → "Horario invalido"
     *   bad pattern → "Formato de hora invalido"
     *   h>23 or m>59 → "Horario invalido"
     */
    private void validarHoraAlterar(String hora) throws Exception {
        if (hora == null || hora.isEmpty()) throw new Exception("Horario invalido");
        if (!hora.matches("\\d{2}:\\d{2}")) throw new Exception("Formato de hora invalido");
        int h = Integer.parseInt(hora.substring(0, 2));
        int m = Integer.parseInt(hora.substring(3, 5));
        if (h > 23 || m > 59) throw new Exception("Horario invalido");
    }

    private void validarHorariosLogica(String abre, String fecha) throws Exception {
        // abre must be strictly before fecha
        if (abre != null && !abre.isEmpty() && fecha != null && !fecha.isEmpty()
                && abre.matches("\\d{2}:\\d{2}") && fecha.matches("\\d{2}:\\d{2}")) {
            if (abre.compareTo(fecha) >= 0) throw new Exception("Horario invalido");
        }
    }
}
