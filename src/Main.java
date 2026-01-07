import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.IntStream;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.SingularMatrixException;

public class Main {

    public static final Scanner scanner = new Scanner(System.in);
    public static final int Zc = 4;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        runInteractiveMode();
    }

    // Opção esccolhida pelo utilizador
    public static void runInteractiveMode() throws FileNotFoundException, IOException {
        int escolha;
        boolean continuar = true;
        while (continuar) {
            menu();

            System.out.print("Digite o número da funcionalidade: ");

            if (scanner.hasNextInt()) {
                escolha = scanner.nextInt();
                scanner.nextLine();

                if (escolha == 1) {
                    executeFunctionality1();
                } else if (escolha == 2) {
                    executeFunctionality2();
                } else if (escolha == 3) {
                    executeFunctionality3();
                } else if (escolha == 4) {
                    executeFunctionality4();
                } else if (escolha == 5) {
                    executeFunctionality5();
                } else if (escolha == 6) {
                    executeFunctionality6();
                } else if (escolha == 7) {
                    executeFunctionality7(scanner);
                } else if (escolha == 8) {
                    executeFunctionality8();
                } else if (escolha == 9) {
                    executeFunctionality9(scanner);
                } else if (escolha == 0) {
                    continuar = false;
                    System.out.println("\nEncerrando a aplicação. Obrigado!");
                } else {
                    System.out.println("ERRO: Funcionalidade não mapeada. Tente novamente.");
                }
            } else {
                System.out.println("Input inválido. Por favor insira um número inteiro.");
                scanner.nextLine();
            }
        }
    }

    // Menu de escolhas
    public static void menu() {
        System.out.println("-----Menu-----");
        System.out.println("1 - Carregar uma matriz e mostrar no ecrã");
        System.out.println("2 - Verificar estabilidade, estabilizar e guardar (CSV)");
        System.out.println("3 - Adicionar tarefas e gerar imagens de relaxamento (JPG)");
        System.out.println("4 - Testar se a matriz estável é recorrente (Burning dhar)");
        System.out.println("5 - Verificar se a matriz E é elemento neutro de R");
        System.out.println("6 - Calcular número de configurações recorrentes - Sem Laplaciana");
        System.out.println("7 - Calcular número de configurações recorrentes - Com Laplaciana Reduzida");
        System.out.println("8 - Retornar matriz inversa para adição estabilizada");
        System.out.println("9 - Calcular a apresentar valores e vetores próprios da Laplaciana Reduzida");
        System.out.println("10 - Sair");
        System.out.println("0 - Sair da aplicação");
    }

    /*
    ---------------------------
    Funcionalidades necessárias
    ---------------------------
    */

    // Método que executa funcionalidade 1
    public static void executeFunctionality1() throws FileNotFoundException {
        System.out.println("--- Funcionalidade 1: Carregar e Mostrar Matriz ---");

        System.out.print("Por favor, insira o nome do ficheiro CSV a carregar: ");
        String nomeFicheiro = scanner.nextLine();

        int[][] matrizCarregada = lerMatriz(nomeFicheiro);

        if (matrizCarregada != null) {
            imprimirMatriz(matrizCarregada);
        }

        System.out.println("--------------------------------------------------\n");
    }

    // Método que executa funcionalidade 2
    public static void executeFunctionality2() throws FileNotFoundException, IOException {
        System.out.println("--- Funcionalidade 2: Verificar Estabilidade, Estabilizar e Guardar (CSV) ---");

        System.out.print("Nome do ficheiro CSV da Matriz a carregar: ");
        String nomeFicheiroEntrada = scanner.nextLine();

        int[][] matriz = lerMatriz(nomeFicheiroEntrada);

        if (matriz == null) {
            return;
        }

        System.out.println("\nMatriz Carregada (Inicial):");
        imprimirMatriz(matriz);
        System.out.println();

        int totalTopples = 0;
        int[][] matrizAtual = copiarMatriz(matriz);
        boolean jaEstavel = !precisaTopping(matrizAtual);

        if (jaEstavel) {
            System.out.println("\nVERIFICAÇÃO: A matriz já está estável (nenhum valor >= Zc=" + Zc + ").");
        } else {
            System.out.println("\nVERIFICAÇÃO: A matriz é instável. Iniciando o processo de estabilização...");

            while (precisaTopping(matrizAtual)) {
                matrizAtual = toppingMatrizes(matrizAtual);
                totalTopples++;
            }

            System.out.println("ESTABILIZAÇÃO CONCLUÍDA. Total de " + totalTopples + " passos de topping síncrono.");
        }

        System.out.println("\nMatriz Estabilizada (Final):");
        imprimirMatriz(matrizAtual);
        System.out.println();

        System.out.print("Nome do ficheiro CSV para guardar a matriz estável (Ex: estavel.csv): ");
        String nomeFicheiroSaida = scanner.nextLine();

        writeMatrizToCSV(matrizAtual, nomeFicheiroSaida);

        System.out.println("----------------------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 3
    public static void executeFunctionality3() throws FileNotFoundException, IOException {
        System.out.println("--- Funcionalidade 3: Adição Estabilizada e Geração de Imagens ---");

        System.out.print("Nome do ficheiro CSV da Matriz A (Configuração Atual): ");
        String nomeFicheiroA = scanner.nextLine();
        System.out.print("Nome do ficheiro CSV da Matriz B (Novas Tarefas): ");
        String nomeFicheiroB = scanner.nextLine();

        int[][] matrizA = lerMatriz(nomeFicheiroA);
        int[][] matrizB = lerMatriz(nomeFicheiroB);

        if (matrizA.length != matrizB.length || matrizA[0].length != matrizB[0].length) {
            System.out.println("ERRO: As matrizes A e B devem ter a mesma dimensão (ser quadradas do mesmo tamanho).");
            return;
        }

        int n = matrizA.length;
        int capacidade = 50;
        int[][][] historico = new int[capacidade][][];
        int totalFrames = 0;

        int[][] matrizInicial = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrizInicial[i][j] = matrizA[i][j] + matrizB[i][j];
            }
        }

        historico[totalFrames] = copiarMatriz(matrizInicial);
        totalFrames++;

        int[][] matrizAtual = copiarMatriz(matrizInicial);

        while (precisaTopping(matrizAtual)) {

            if (totalFrames >= capacidade) {
                capacidade = capacidade * 2;
                int[][][] novoHistorico = new int[capacidade][][];
                for (int i = 0; i < totalFrames; i++) {
                    novoHistorico[i] = historico[i];
                }
                historico = novoHistorico;
            }

            matrizAtual = toppingMatrizes(matrizAtual);

            historico[totalFrames] = copiarMatriz(matrizAtual);
            totalFrames++;
        }

        System.out.println("\nAvalanche terminada. Total de " + totalFrames + " quadros (frames) gerados, incluindo o inicial.");

        int quantidadeDeMatrizes = Math.min(totalFrames, 20);

        System.out.println("--- A gravar " + quantidadeDeMatrizes + " imagens selecionadas ---");

        new File("Output").mkdir();

        for (int i = 0; i < quantidadeDeMatrizes; i++) {
            int indice;

            if (totalFrames <= 20) {
                indice = i;
            } else {
                double tamanhoDoSalto = (double) (totalFrames - 1) / 19.0;
                indice = (int) Math.round(i * tamanhoDoSalto);

                if (indice >= totalFrames) {
                    indice = totalFrames - 1;
                }
            }

            int[][] matrizParaGravar = historico[indice];
            String nomeFicheiro = "Output/iteracao_" + (i + 1) + ".jpg";

            System.out.println("Gerando: " + nomeFicheiro + " (Frame Original: " + indice + ")");

            // CORREÇÃO: Chama o método diretamente, pois está na mesma classe.
            writeArrayAsImage(matrizParaGravar, nomeFicheiro);
        }
        System.out.println("Geração de imagens concluída.");

        System.out.println("------------------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 4
    public static void executeFunctionality4() throws FileNotFoundException {
        System.out.println("--- Funcionalidade 4: Testar Recorrência (Burning Dhar) ---");

        System.out.print("Nome do ficheiro CSV da Matriz estável a testar: ");
        String nomeFicheiro = scanner.nextLine();

        int[][] matriz = lerMatriz(nomeFicheiro);

        if (matriz == null) {
            return;
        }

        // 1. Verificação de Estabilidade (Condição Necessária)
        if (precisaTopping(matriz)) {
            System.out.println("\nRESULTADO: A matriz não é estável (contém valores >= Zc=" + Zc + ").");
            System.out.println("           Uma matriz instável não pode ser recorrente.");
        } else {
            // 2. Aplicação do Algoritmo de Burning de Dhar
            boolean eRecorrente = checadorDeRecorrenciaDeMatriz(matriz);
            if (eRecorrente) {
                System.out.println("RESULTADO: SIM, a matriz é uma configuração recorrente.");
            } else {
                System.out.println("RESULTADO: NÃO, a matriz não é uma configuração recorrente.");
            }
        }

        System.out.println("----------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 5
    public static void executeFunctionality5() throws FileNotFoundException, IOException {
        System.out.println("--- Funcionalidade 5: Verificar se a matriz E é elemento neutro de R ---");

        System.out.print("Nome do ficheiro CSV da Matriz E (Candidato a Elemento Neutro): ");
        String nomeFicheiroE = scanner.nextLine();
        int[][] matrizE = lerMatriz(nomeFicheiroE);

        if (matrizE == null) return;

        int n = matrizE.length;

        // Calcula o total de combinações possíveis (4^(n*n))
        // Nota: Utiliza 'long' para suportar números grandes
        long totalCombinacoes = (long) Math.pow(4, n * n);
        long countRecorrentes = 0;
        boolean isNeutro = true;

        System.out.println("A iniciar verificação para dimensão " + n + "x" + n + "...");
        System.out.println("Total de configurações a testar: " + totalCombinacoes);

        // Loop que gera todas as matrizes possíveis (de 0 a totalCombinacoes)
        for (long k = 0; k < totalCombinacoes; k++) {

            // 1. Converter o índice 'k' numa Matriz A (Base 4)
            int[][] matrizA = new int[n][n];
            long tempK = k;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matrizA[i][j] = (int) (tempK % 4);
                    tempK /= 4;
                }
            }

            // 2. Verificar se A pertence ao conjunto Recorrente (R)
            if (checadorDeRecorrenciaDeMatriz(matrizA)) {
                countRecorrentes++;

                // 3. Verificar a propriedade do Elemento Neutro: (A + E) estabilizado == A
                // Usa os métodos existentes: calculateStableAddition e areMatrizesIguais
                int[][] resultado = calculateStableAddition(matrizA, matrizE);

                if (resultado == null) {
                    System.out.println("\nERRO CRÍTICO: Falha no cálculo da estabilização.");
                    return;
                }

                if (!areMatrizesIguais(matrizA, resultado)) {
                    isNeutro = false;
                    System.out.println("\nFALHA ENCONTRADA!");
                    System.out.println("A matriz E falhou para a seguinte configuração recorrente A:");
                    imprimirMatriz(matrizA);
                    System.out.println("Resultado da soma (A ⊕ E):");
                    imprimirMatriz(resultado);
                    break; // Para o loop assim que encontrar uma falha
                }
            }
        }

        System.out.println("\n--- Análise Terminada ---");
        System.out.println("Número de Configurações Recorrentes encontradas e testadas: " + countRecorrentes);

        if (isNeutro) {
            System.out.println("\nRESULTADO: SIM, a Matriz E fornecida funciona como o Elemento Neutro.");
        } else {
            System.out.println("\nRESULTADO: NÃO, a Matriz E fornecida NÃO é o Elemento Neutro.");
        }

        System.out.println("----------------------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 6
    public static void executeFunctionality6() {
        System.out.println("--- Funcionalidade 6: Calcular N.º Configurações Recorrentes (Brute-Force) ---");
        System.out.println("   (AVISO: Esta funcionalidade é computacionalmente intensiva para N > 3)");

        int n = -1;
        boolean inputValido = false;

        while (!inputValido) {
            System.out.print("Por favor, insira a dimensão da matriz (n): ");
            if (scanner.hasNextInt()) {
                int inputN = scanner.nextInt();
                scanner.nextLine();

                if (inputN > 0) {
                    n = inputN;
                    inputValido = true;
                } else {
                    System.out.println("A dimensão deve ser um número inteiro positivo.");
                }
            } else {
                System.out.println("Input inválido. Por favor, insira um número inteiro.");
                scanner.nextLine();
            }
        }

        if (n >= 4) {
            System.out.println("\nALERTA: Para N=" + n + " o número de estados estáveis é " + Zc + "^" + (n*n) + ". O cálculo pode levar minutos ou falhar.");
            System.out.print("Deseja continuar? (S/N): ");
            String resposta = scanner.nextLine().trim().toUpperCase();
            if (!resposta.equals("S")) {
                System.out.println("Cálculo cancelado.");
                System.out.println("----------------------------------------------------------------------\n");
                return;
            }
        }

        long recurrentCount = countRecurrentStatesBruteForce(n);

        System.out.printf("\nRESULTADO: Para a dimensão %d x %d, o número de configurações recorrentes é: %d\n", n, n, recurrentCount);
        System.out.println("----------------------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 7
    public static void executeFunctionality7(Scanner scanner) {
        System.out.println("--- Funcionalidade 7: Calcular Matrizes Recorrentes (Laplaciano Reduzido) ---");

        int n = -1;
        boolean inputValido = false;

        while (!inputValido) {
            System.out.print("Por favor, insira a dimensão da matriz (n): ");
            if (scanner.hasNextInt()) {
                int inputN = scanner.nextInt();
                scanner.nextLine();

                if (inputN > 0) {
                    n = inputN;
                    inputValido = true;
                } else {
                    System.out.println("A dimensão deve ser um número inteiro positivo.");
                }
            } else {
                System.out.println("Input inválido. Por favor, insira um número inteiro.");
                scanner.nextLine();
            }
        }

        RealMatrix L_til = buildReducedLaplacian(n);
        double recurrentCount = calculateRecurrentConfigurations(L_til);

        int m_reduced = n * n - 1;
        System.out.println("\n Laplaciana Reduzida (L̃) para a dimensão " + n + "x" + n + " construída (tamanho " + m_reduced + "x" + m_reduced + ").");
        System.out.printf("   O número de matrizes/configurações recorrentes (det(L̃)) é: %.0f\n", recurrentCount);
        System.out.println("----------------------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 8
    public static void executeFunctionality8() throws FileNotFoundException, IOException {
        System.out.println("--- Funcionalidade 8: Retornar matriz inversa para adição estabilizada ---");
        System.out.println("   (Calculado usando a Laplaciana Reduzida e o seu Inverso)");

        System.out.print("Nome do ficheiro CSV da Matriz A (Configuração Recorrente): ");
        String nomeFicheiroA = scanner.nextLine();
        System.out.print("Nome do ficheiro CSV da Matriz E (Elemento Neutro - A identidade): ");
        String nomeFicheiroE = scanner.nextLine();

        int[][] matrizA = lerMatriz(nomeFicheiroA);
        int[][] matrizE = lerMatriz(nomeFicheiroE);

        if (matrizA == null || matrizE == null) return;

        if (matrizA.length != matrizE.length || matrizA[0].length != matrizE[0].length) {
            System.out.println("ERRO: As matrizes A e E devem ter a mesma dimensão (ser quadradas do mesmo tamanho).");
            return;
        }

        int n = matrizA.length;

        System.out.println("\nMatriz A (Original):");
        imprimirMatriz(matrizA);
        System.out.println("\nMatriz E (Elemento Neutro):");
        imprimirMatriz(matrizE);

        int[][] C_for_Inverse = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C_for_Inverse[i][j] = matrizE[i][j] + (Zc - 1) - matrizA[i][j];
            }
        }

        int[][] A_inverse = calculateStableState(C_for_Inverse);

        if (A_inverse == null) {
            System.out.println("\nERRO: A Laplaciana Reduzida é singular (Determinante zero). O cálculo matricial falhou.");
        } else {
            System.out.println("\nMatriz A' (Configuração Inversa A⁻¹):");
            imprimirMatriz(A_inverse);

            System.out.print("\nNome do ficheiro CSV para guardar a matriz inversa (Ex: inversa.csv): ");
            String nomeFicheiroSaida = scanner.nextLine();
            writeMatrizToCSV(A_inverse, nomeFicheiroSaida);
        }

        System.out.println("----------------------------------------------------------------------\n");
    }

    // Método que executa funcionalidade 9
    public static void executeFunctionality9(Scanner scanner) {
        System.out.println("--- Funcionalidade 9: Valores e Vetores Próprios do Laplaciano Reduzido ---");

        final int n;
        int tempN = -1;
        boolean nSet = false;

        while (!nSet) {
            System.out.print("Por favor, insira a dimensão da matriz (n): ");
            if (scanner.hasNextInt()) {
                int inputN = scanner.nextInt();
                scanner.nextLine();

                if (inputN >= 1) {
                    tempN = inputN;
                    nSet = true;
                } else {
                    System.out.println("A dimensão deve ser um número inteiro positivo.");
                }
            } else {
                System.out.println("Input inválido. Por favor, insira um número inteiro.");
                scanner.nextLine();
            }
        }

        n = tempN;
        if (n*n == 0) {
            System.out.println("Dimensão inválida para o cálculo.");
            return;
        }

        if (n == 1) {
            System.out.println("Para uma matriz 1x1, o Laplaciano reduzido é 0x0. Sem vetores próprios.");
        }

        final RealMatrix L_til = buildReducedLaplacian(n);
        final int m_reduced = n * n - 1;

        final EigenDecomposition eigenDecomposition = new EigenDecomposition(L_til);

        System.out.println("\n Laplaciana Reduzida L̃ (" + m_reduced + "x" + m_reduced + "):");
        System.out.println("   Valores e Vetores Próprios:");

        IntStream.range(0, m_reduced).forEach(i -> {
            double eigenvalue = eigenDecomposition.getRealEigenvalue(i);
            RealVector eigenvector = eigenDecomposition.getEigenvector(i);

            RealVector normalizedEigenvector = normalizeVector(eigenvector);

            System.out.printf("\n--- Par Próprio %d ---\n", i + 1);
            System.out.printf("   Valor Próprio (λ): %.6f\n", eigenvalue);
            System.out.println("   Vetor Próprio Normalizado (escala [-1, 1]):");

            System.out.println("   [Matriz " + n + "x" + n + " correspondente ao Padrão Espacial (Sink = 0.0)]");

            int vectorIndex = 0;
            for (int row = 0; row < n; row++) {
                System.out.print("   [");
                for (int col = 0; col < n; col++) {
                    int gridIndex = row * n + col;

                    if (gridIndex == n * n - 1) {
                        System.out.printf("% 8.4f", 0.0);
                    } else {
                        System.out.printf("% 8.4f", normalizedEigenvector.getEntry(vectorIndex));
                        vectorIndex++;
                    }

                    if (col < n - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("]");
            }
        });
    }

    /*
    --------------------------------------------
    Métodos uteis utilizados nas funcionalidades
    --------------------------------------------
    */

    // Método principal de contagem de força bruta
    public static long countRecurrentStatesBruteForce(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;

        int totalCells = n * n;
        int[] currentConfig = new int[totalCells];
        long[] count = {0};
        generateAndCheck(n, totalCells, currentConfig, 0, count);

        return count[0];
    }

    public static void generateAndCheck(int n, int totalCells, int[] currentConfig, int index, long[] count) {
        if (index == totalCells) {
            int[][] matriz = new int[n][n];
            for (int i = 0; i < totalCells; i++) {
                matriz[i / n][i % n] = currentConfig[i];
            }

            if (checadorDeRecorrenciaDeMatriz(matriz)) {
                count[0]++;
            }
            return;
        }

        for (int value = 0; value < Zc; value++) {
            currentConfig[index] = value;
            generateAndCheck(n, totalCells, currentConfig, index + 1, count);
        }
    }

    // Método para comparar duas matrizes
    public static boolean areMatrizesIguais(int[][] matrizA, int[][] matrizB) {
        if (matrizA.length != matrizB.length || matrizA[0].length != matrizB[0].length) {
            return false;
        }
        for (int i = 0; i < matrizA.length; i++) {
            for (int j = 0; j < matrizA[i].length; j++) {
                if (matrizA[i][j] != matrizB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }


    // Método para escrever matrizes em ficheiros CSV
    public static void writeMatrizToCSV(int[][] matriz, String nomeFicheiro) throws IOException {

        // O PrintWriter e FileWriter podem lançar FileNotFoundException (que é um tipo de IOException)
        PrintWriter pw = new PrintWriter(new FileWriter(nomeFicheiro));

        // Escreve a matriz
        for (int i = 0; i < matriz.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(matriz[i][j]);
                if (j < matriz[i].length - 1) {
                    sb.append(",");
                }
            }
            pw.println(sb.toString());
        }
        pw.close();
        System.out.println("Matriz estável guardada com sucesso em: " + nomeFicheiro);
    }

    // Método que executa o Algoritmo de Burning de Dhar
    public static boolean checadorDeRecorrenciaDeMatriz(int[][] matriz) {

        int n = matriz.length;
        int[][] matrizDasQueimadas = new int[n][n]; // 0: não queimado, 1: queimado
        int celulasQueimadas = 0;
        boolean novasQueimadas = true;

        // 1. Inicializa todas as células como "não queimadas" (0)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrizDasQueimadas[i][j] = 0;
            }
        }

        // 2. Processo iterativo de queima
        while (novasQueimadas) {
            novasQueimadas = false;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (matrizDasQueimadas[i][j] == 0) { // Só processa células não queimadas

                        int U = 0; // número de vizinhos ortogonais não queimados

                        // Cima
                        if (i > 0 && matrizDasQueimadas[i - 1][j] == 0) U++;
                        // Baixo
                        if (i < n - 1 && matrizDasQueimadas[i + 1][j] == 0) U++;
                        // Esquerda
                        if (j > 0 && matrizDasQueimadas[i][j - 1] == 0) U++;
                        // Direita
                        if (j < n - 1 && matrizDasQueimadas[i][j + 1] == 0) U++;

                        // Condição de Queima (G[i][j] >= U)
                        if (matriz[i][j] >= U) {
                            matrizDasQueimadas[i][j] = 1; // Queima
                            novasQueimadas = true;
                            celulasQueimadas++;
                        }
                    }
                }
            }
        }
        return celulasQueimadas == n * n;
    }

    // Método para copiar matrizes
    public static int[][] copiarMatriz(int[][] matriz) {
        int n = matriz.length;
        int[][] copia = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matriz[i], 0, copia[i], 0, n);
        }
        return copia;
    }

    // Método para verificar se uma matriz precisa de Topping
    public static boolean precisaTopping(int[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j] >= Zc) return true;
            }
        }
        return false;
    }

    // Método que realiza o Topping em matrizes
    public static int[][] toppingMatrizes(int[][] matriz) {
        int n = matriz.length;
        int[][] proximoEstado = copiarMatriz(matriz);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (matriz[i][j] >= Zc) {

                    proximoEstado[i][j] -= Zc;

                    // Cima (i-1, j)
                    if (i > 0) {
                        proximoEstado[i - 1][j]++;
                    }

                    // Baixo (i+1, j)
                    if (i < n - 1) {
                        proximoEstado[i + 1][j]++;
                    }

                    // Esquerda (i, j-1)
                    if (j > 0) {
                        proximoEstado[i][j - 1]++;
                    }

                    // Direita (i, j+1)
                    if (j < n - 1) {
                        proximoEstado[i][j + 1]++;
                    }
                }
            }
        }
        return proximoEstado;
    }

    // Método para transformar matrizes em imagens
    public static void writeArrayAsImage(int[][] array, String outputFilePath) throws IOException {
        int height = array.length;
        int width = array[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = array[y][x];
                Color color;

                switch (value) {
                    case 0:
                        color = Color.WHITE;
                        break;
                    case 1:
                        color = Color.YELLOW;
                        break;
                    case 2:
                        color = new Color(150, 75, 0); // Brown
                        break;
                    case 3:
                        color = Color.RED;
                        break;
                    default:
                        color = Color.BLUE;
                        break;
                }

                image.setRGB(x, y, color.getRGB());
            }
        }
        File outputFile = new File(outputFilePath);
        ImageIO.write(image, "jpg", outputFile);
    }

    // Método para ler matrizes
    public static int[][] lerMatriz(String nomeFicheiro) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(nomeFicheiro));

        int[][] matriz = null;
        int linhaAtual = 0;
        int N = 0;
        boolean erro = false;

        while (ler.hasNextLine() && !erro) {
            String linha = ler.nextLine();

            if (!linha.trim().isEmpty()) {
                String[] elementos = linha.split(",");

                if (linhaAtual == 0) {
                    N = elementos.length;
                    if (N == 0) {
                        erro = true;
                    } else {
                        matriz = new int[N][N];
                    }
                }

                if (!erro) {
                    if (elementos.length != N || linhaAtual >= N) {
                        System.out.println(" A matriz não é quadrada ou tem um formato inválido!");
                        erro = true;
                    } else {
                        for (int coluna = 0; coluna < elementos.length; coluna++) {
                            matriz[linhaAtual][coluna] = Integer.parseInt(elementos[coluna].trim());
                        }
                        linhaAtual++;
                    }
                }
            }
        }
        ler.close();

        if (erro || (matriz != null && linhaAtual != N)) {
            System.out.println("Erro irrecuperável na leitura da matriz. Encerrando.");
            return null;
        }
        return matriz;
    }

    // Método que adiciona e estabiliza
    public static int[][] calculateStableAddition(int[][] A, int[][] E) {
        int n = A.length;

        // 1. Calcular a soma simples C = A + E
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + E[i][j];
            }
        }

        // 2. Estabilizar C (C') e retornar
        return calculateStableState(C);
    }

    // Método que estabiliza utilizando Laplaciana Reduzida
    public static int[][] calculateStableState(int[][] C) {
        int n = C.length;
        int m = n * n;
        int m_reduced = m - 1;

        if (n * n <= 1) {
            return new int[n][n];
        }

        RealMatrix L_full = buildFullLaplacian(n);
        RealMatrix L_tilde = buildReducedLaplacian(n);

        LUDecomposition lu = new LUDecomposition(L_tilde);
        if (Math.abs(lu.getDeterminant()) < 1e-9) {
            return null;
        }

        double[] R_data_reduced = new double[m_reduced];
        double[] C_data_full = new double[m];

        int k = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double C_k = C[i][j];
                C_data_full[k] = C_k;

                if (k < m_reduced) {
                    R_data_reduced[k] = C_k - (Zc - 1);
                }
                k++;
            }
        }

        RealVector R_reduced = new ArrayRealVector(R_data_reduced);
        RealVector C_full = new ArrayRealVector(C_data_full);

        DecompositionSolver solver = lu.getSolver();
        RealVector T_vector_reduced = solver.solve(R_reduced);

        RealVector T_vector_reduced_int = new ArrayRealVector(m_reduced);
        for (int i = 0; i < m_reduced; i++) {
            double roundedT = Math.round(T_vector_reduced.getEntry(i));
            T_vector_reduced_int.setEntry(i, roundedT);
        }

        double[] T_data_full = new double[m];
        for (int i = 0; i < m_reduced; i++) {
            T_data_full[i] = T_vector_reduced_int.getEntry(i);
        }
        T_data_full[m_reduced] = 0.0;
        RealVector T_full = new ArrayRealVector(T_data_full);

        RealVector Dissipation = L_full.operate(T_full);

        int[][] C_stable_matrix = new int[n][n];
        for (int i = 0; i < m; i++) {
            double value = C_full.getEntry(i) - Dissipation.getEntry(i);

            int C_stable_ij = (int) Math.round(value);

            int row = i / n;
            int col = i % n;

            C_stable_matrix[row][col] = C_stable_ij;
        }

        return C_stable_matrix;
    }

    // Método para imprimir matrizes
    public static void imprimirMatriz(int[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            System.out.print("|");
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf(" %d |", matriz[i][j]);
            }
            System.out.println();
        }
    }

    // Método para escalar os vetores próprios
    public static RealVector normalizeVector(RealVector vector) {
        double maxAbs = 0.0;
        for (double value : vector.toArray()) {
            double absValue = Math.abs(value);
            if (absValue > maxAbs) {
                maxAbs = absValue;
            }
        }

        if (maxAbs < 1e-9) {
            return vector.mapMultiply(0.0);
        }

        return vector.mapMultiply(1.0 / maxAbs);
    }

    // Método que constrói o Laplaciano COMPLETO
    public static RealMatrix buildFullLaplacian(int n) {
        int m = n * n;
        double[][] laplacianData = new double[m][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int k = i * n + j;

                int degree = 0; // Graus do nó

                int[][] neighbors = {
                        {i - 1, j}, {i + 1, j}, {i, j - 1}, {i, j + 1}
                };

                for (int[] neighbor : neighbors) {
                    int ni = neighbor[0];
                    int nj = neighbor[1];

                    if (ni >= 0 && ni < n && nj >= 0 && nj < n) {
                        int neighbor_k = ni * n + nj;
                        laplacianData[k][neighbor_k] = -1.0;
                        degree++;
                    } else {
                        degree++; // Vizinho externo é uma fronteira aberta/sumidouro.
                    }
                }
                laplacianData[k][k] = degree;
            }
        }
        return new Array2DRowRealMatrix(laplacianData);
    }

    // Método de construção de Laplaciana Reduzida
    public static RealMatrix buildReducedLaplacian(int n) {
        if (n * n <= 1) {
            return new Array2DRowRealMatrix(0, 0);
        }

        RealMatrix L_full = buildFullLaplacian(n);
        int m = n * n;
        int sizeReduced = m - 1;

        return L_full.getSubMatrix(0, sizeReduced - 1, 0, sizeReduced - 1);
    }

    // Método para calcular o número de configurações recorrentes
    public static double calculateRecurrentConfigurations(RealMatrix laplacianMatrix) {
        if (laplacianMatrix.getRowDimension() == 0) {
            return 1.0;
        }
        LUDecomposition lu = new LUDecomposition(laplacianMatrix);
        return lu.getDeterminant();
    }
}