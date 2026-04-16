# MyFood - Projeto de Delivery

Sistema de delivery com cadastro de empresas, clientes, produtos e pedidos.
Desenvolvido em Java puro (sem Swing/Web), com persistência via XML (XMLEncoder/XMLDecoder).

## Estrutura do Projeto

```
myfood/
├── src/
│   └── myfood/
│       ├── Main.java                      <- Ponto de entrada
│       ├── facade/
│       │   └── MyFoodFacade.java          <- Facade principal (EasyAccept aponta aqui)
│       ├── model/
│       │   ├── Usuario.java
│       │   ├── Cliente.java
│       │   ├── DonoEmpresa.java
│       │   ├── Empresa.java
│       │   ├── Produto.java
│       │   └── Pedido.java
│       └── persistence/
│           ├── SistemaData.java           <- Contêiner de dados serializável
│           └── PersistenceManager.java    <- Salva/carrega via XMLEncoder
├── tests/
│   ├── us1_1.txt ... us4_2.txt            <- Scripts EasyAccept
├── lib/
│   └── easyaccept.jar                     <- << VOCÊ COLOCA AQUI >>
├── bin/                                   <- Gerado pelo build
├── build.sh                               <- Build Linux/Mac
├── build.bat                              <- Build Windows
└── pom.xml                                <- Opcional: Maven
```

## Pré-requisitos

- JDK 11 ou superior
- EasyAccept JAR

### Obtendo o EasyAccept

Baixe o `easyaccept.jar` em:
https://sourceforge.net/projects/easyaccept/

Coloque o arquivo em `lib/easyaccept.jar`.

## Como compilar e rodar (Linux/Mac)

```bash
chmod +x build.sh
./build.sh
```

## Como compilar e rodar (Windows)

```bat
build.bat
```

## Como compilar manualmente

```bash
# Compilar
find src -name "*.java" > sources.txt
javac -cp lib/easyaccept.jar -d bin @sources.txt

# Rodar um teste individual
java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept myfood.facade.MyFoodFacade tests/us1_1.txt
```

## User Stories implementadas

| US  | Descrição              | Testes         |
|-----|------------------------|----------------|
| US1 | Criação de Contas      | us1_1, us1_2   |
| US2 | Criação de Empresas    | us2_1, us2_2   |
| US3 | Criação de Produtos    | us3_1, us3_2   |
| US4 | Criação de Pedidos     | us4_1, us4_2   |

## Persistência

Os dados são salvos em `myfood_data.xml` (no diretório de execução) usando
`java.beans.XMLEncoder` e carregados via `java.beans.XMLDecoder`.

O arquivo é gravado ao chamar `encerrarSistema` e apagado ao chamar `zerarSistema`.
