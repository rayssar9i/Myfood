package br.ufal.ic.myfood;

import easyaccept.EasyAccept;

/**
 * Ponto de entrada alternativo — delega para Facade.main().
 * O EasyAccept também pode ser chamado diretamente por aqui.
 */
public class Main {
    public static void main(String[] args) {
        // US1 - Usuários
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us1_2.txt"});

        // US2 - Empresas
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us2_2.txt"});

        // US3 - Produtos
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us3_2.txt"});

        // US4 - Pedidos
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us4_2.txt"});

        // US5 - Gerenciar entregas
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us5_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us5_2.txt"});

        // US6 - Consultar histórico
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us6_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us6_2.txt"});

        // US7 - Relatórios
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us7_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us7_2.txt"});

        // US8 - Outras funcionalidades
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us8_1.txt"});
        EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", "tests/us8_2.txt"});
    }
}
/*package br.ufal.ic.myfood;
import easyaccept.EasyAccept;

public class Main {
    public static void main(String[] args) {
        // Agora o Java SÓ roda o que o PowerShell mandar via args[0]
        if (args.length > 0) {
            EasyAccept.main(new String[]{"br.ufal.ic.myfood.Facade", args[0]});
        }
    }
} */