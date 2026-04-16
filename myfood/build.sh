#!/bin/bash
# =============================================================================
# build.sh - Compila e executa os testes do MyFood com EasyAccept
# =============================================================================
# Pré-requisito: coloque o easyaccept.jar na pasta lib/
# Download: https://sourceforge.net/projects/easyaccept/
# =============================================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
BIN_DIR="$PROJECT_DIR/bin"
LIB_DIR="$PROJECT_DIR/lib"
TESTS_DIR="$PROJECT_DIR/tests"
JAR_EA="$LIB_DIR/easyaccept.jar"

if [ ! -f "$JAR_EA" ]; then
    echo "ERRO: easyaccept.jar não encontrado em $LIB_DIR"
    echo "Baixe em: https://sourceforge.net/projects/easyaccept/"
    exit 1
fi

echo "=== Compilando MyFood ==="
mkdir -p "$BIN_DIR"

find "$SRC_DIR" -name "*.java" > /tmp/sources.txt

javac -cp "$JAR_EA" -d "$BIN_DIR" @/tmp/sources.txt
echo "Compilação concluída."

echo ""
echo "=== Executando testes ==="
cd "$PROJECT_DIR"

run_test() {
    local label="$1"
    local file="$2"
    echo ""
    echo "--- $label ---"
    java -cp "$BIN_DIR:$JAR_EA" easyaccept.EasyAccept myfood.facade.MyFoodFacade "$file"
}

run_test "US1 - Criação de Contas"            "$TESTS_DIR/us1_1.txt"
run_test "US1 - Persistência"                  "$TESTS_DIR/us1_2.txt"
run_test "US2 - Criação de Restaurantes"       "$TESTS_DIR/us2_1.txt"
run_test "US2 - Persistência"                  "$TESTS_DIR/us2_2.txt"
run_test "US3 - Criação de Produtos"           "$TESTS_DIR/us3_1.txt"
run_test "US3 - Persistência"                  "$TESTS_DIR/us3_2.txt"
run_test "US4 - Criação de Pedidos"            "$TESTS_DIR/us4_1.txt"
run_test "US4 - Persistência"                  "$TESTS_DIR/us4_2.txt"

echo ""
echo "=== Todos os testes concluídos ==="
