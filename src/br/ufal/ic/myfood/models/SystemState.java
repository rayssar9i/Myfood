package br.ufal.ic.myfood.models;

import java.io.*;

/**
 * Agrupa TODO o estado do sistema em um único objeto serializável.
 *
 * Padrão de persistência:
 *   zerarSistema()    → cria novo estado + apaga arquivo
 *   encerrarSistema() → salva estado em "system_state.dat"
 *   Inicialização     → se arquivo existir, carrega; senão, cria novo
 */
public class SystemState implements Serializable {

    private static final long serialVersionUID = 2L;

    public static final String FILE_PATH = "system_state.dat";

    public UsuarioManager  usuarioManager;
    public EmpresaManager  empresaManager;
    public ProdutoManager  produtoManager;
    public PedidoManager   pedidoManager;
    public EntregaManager  entregaManager;

    public SystemState() {
        this.usuarioManager = new UsuarioManager();
        this.empresaManager = new EmpresaManager();
        this.produtoManager = new ProdutoManager();
        this.pedidoManager  = new PedidoManager();
        this.entregaManager = new EntregaManager();
    }

    public void salvar() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Erro ao salvar estado: " + e.getMessage());
        }
    }

    public static SystemState carregar() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new SystemState();
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (SystemState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar estado: " + e.getMessage());
            return new SystemState();
        }
    }

    public static void apagarArquivo() {
        File file = new File(FILE_PATH);
        if (file.exists()) file.delete();
    }
}
