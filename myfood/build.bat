@echo off
REM =============================================================================
REM build.bat - Compila e executa os testes do MyFood com EasyAccept (Windows)
REM =============================================================================
REM Pre-requisito: coloque o easyaccept.jar na pasta lib\
REM =============================================================================

set PROJECT_DIR=%~dp0
set SRC_DIR=%PROJECT_DIR%src
set BIN_DIR=%PROJECT_DIR%bin
set LIB_DIR=%PROJECT_DIR%lib
set TESTS_DIR=%PROJECT_DIR%tests
set JAR_EA=%LIB_DIR%\easyaccept.jar

if not exist "%JAR_EA%" (
    echo ERRO: easyaccept.jar nao encontrado em %LIB_DIR%
    echo Baixe em: https://sourceforge.net/projects/easyaccept/
    exit /b 1
)

echo === Compilando MyFood ===
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

dir /s /b "%SRC_DIR%\*.java" > %TEMP%\sources.txt

javac -cp "%JAR_EA%" -d "%BIN_DIR%" @%TEMP%\sources.txt
echo Compilacao concluida.

echo.
echo === Executando testes ===
cd /d "%PROJECT_DIR%"

echo --- US1 - Criacao de Contas ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us1_1.txt"

echo --- US1 - Persistencia ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us1_2.txt"

echo --- US2 - Criacao de Restaurantes ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us2_1.txt"

echo --- US2 - Persistencia ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us2_2.txt"

echo --- US3 - Criacao de Produtos ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us3_1.txt"

echo --- US3 - Persistencia ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us3_2.txt"

echo --- US4 - Criacao de Pedidos ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us4_1.txt"

echo --- US4 - Persistencia ---
java -cp "%BIN_DIR%;%JAR_EA%" easyaccept.EasyAccept myfood.facade.MyFoodFacade "%TESTS_DIR%\us4_2.txt"

echo.
echo === Todos os testes concluidos ===
pause
