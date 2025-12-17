import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Main{
    public static void imprimirMatriz(int[][] matriz) {
        System.out.println("Matriz Inicial:");
        for (int i = 0; i < matriz.length; i++) {
            System.out.print("|");
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf(" %d |", matriz[i][j]);
            }
            System.out.println();
        }
    }

    public static int Zc = 4;                          //Constante do número crítico

    //||||||||||||||||||||||||||||||||||||||||||||\\
   //                                              \\
  //           Modulo Operação Toppin               \\
 //                                                  \\
    public static int[][] toppingMatrizes(int[][] matriz) {

        int numeroIndiceMaximo = matriz.length - 1;   //É a váriavel que armazena o maior Índice da coluna e da linha
        int nDaMatriz = matriz[0].length;             //É a váriavel que armazena a quantidade de termos em cada coluna e linha

        //Ciclo dos termos da Matriz
        for (int i = 0; i < nDaMatriz; i++) {
            for (int j = 0; j < nDaMatriz; j++) {
                //O topping é apenas aplicado se o termo selecionado for maior ou igual ao Zc (nnúmero crítico da rede)
                if (matriz[i][j] >= Zc) {
                    //O termo é anulado
                    matriz[i][j] -= Zc;

                    //Certas áreas da matriz possuem propriedades genéricas quando sofrem toppin dependendo da sua posição em relação a bordas
                    //(Operações abaixo:)
                    if ( i > 0 ) {

                        matriz[i - 1][j] ++;
                    }
                    if ( j > 0 ) {

                        matriz[i][j - 1] ++;
                    }
                    if ( i < numeroIndiceMaximo) {

                        matriz[i + 1][j] ++;
                    }
                    if ( j < numeroIndiceMaximo) {

                        matriz[i][j + 1] ++;
                    }
                }
            }
        }
        //A matriz é retornada depois de sofrer toppin
        return matriz;
    }
    public static int[][] somaMatrizes(int[][] matriz1,int[][] matriz2) {

        int linhas = matriz1.length;
        int colunas = matriz1[0].length;
        int [][] matrizresultante = new int[linhas][colunas];

        for (int i = 0 ;i < linhas; i++ ) {
            for (int j = 0 ; j < colunas; j++ ) {
                matrizresultante[i][j] = matriz1[i][j] + matriz2[i][j];
            }
        }
        return matrizresultante;
    }


       //-----------------------------------------------------------------------------\\
      //                                                                               \\
     //    Verificar se Matriz estbilizada é recorrente (ALgaritimo de Burning Dhar)    \\
    //                                                                                   \\
    public static boolean checadorDeRecorrenciaDeMatriz ( int [][] matriz){

    int n = matriz.length;                            //numero de termos por linha ou coluna
    int [][] matrizDasQueimadas = new int[n][n];      //matriz (termo=0 quando não queimado)(termo=1 quando queimado)
    int celulasQueimadas = 0;                         //numero de celulas queimadas
    boolean novasQueimadas = true;                    //Boolean para ou continua o while

    for ( int i = 0 ; i < n ; i++ ) {
        for ( int j = 0 ; j < n ; j++ ) {

            matrizDasQueimadas[i][j] = 0;
        }
    }
    while (novasQueimadas) {
        novasQueimadas = false;

        for ( int i = 0; i < n; i++) {
            for ( int j = 0; j < n; j++) {

                if ( matrizDasQueimadas [i][j] == 0 ){

                    int U = 0;                       //número de vizinhos ortogonais não queimados do termo selecionado

                    if ( i > 0 ) {
                        if ( matrizDasQueimadas[i-1][j] == 0 ) { U ++; }
                    }
                    if ( j > 0 ) {
                        if ( matrizDasQueimadas[i][j-1] == 0 ) { U ++; }
                    }
                    if ( i < n - 1) {
                        if ( matrizDasQueimadas[i+1][j] == 0 ) { U ++; }
                    }
                    if ( j < n - 1 ) {
                        if ( matrizDasQueimadas[i][j+1] == 0 ) { U ++; }
                    }
                    if ( matriz[i][j] >= U ) {
                        matrizDasQueimadas[i][j] = 1;   //O termo selecionado queima
                        novasQueimadas = true;          //While mais uma vez
                        celulasQueimadas++;
                    }
                }
            }
        }
    }
    if (celulasQueimadas == n*n) {return true ;}  //matriz é recorrente
    else                         {return false;}  //matriz não é recorrente
    }
}
  public static int[][] lerMatriz(String nomeFicheiro) throws FileNotFoundException {
        Scanner ler = new Scanner(new File(nomeFicheiro));
        int[][] matriz = null;
        int linhaAtual = 0;
        int N = 0;
        boolean erro = false;
        while (ler.hasNextLine() && !erro) {
            String linha = ler.nextLine();
            if (linha.trim().isEmpty()) continue;
            String[] elementos = linha.split(",");
            if (linhaAtual == 0) {
                N = elementos.length;
                matriz = new int[N][N];
            }
            if (elementos.length != N || linhaAtual >= N) {
                System.out.println(" A matriz não é quadrada!");
                erro = true;
            } else {
                for (int coluna = 0; coluna < elementos.length; coluna++) {
                    matriz[linhaAtual][coluna] = Integer.parseInt(elementos[coluna].trim());
                }
                linhaAtual++;
            }
        }
        ler.close();
        if (erro || (matriz != null && linhaAtual != N)) {
            System.exit(0);
        }
        return matriz;
  }
    public static void escreverMatrizParaCSV(int[][] matriz, String nomeFicheiroSaida) throws FileNotFoundException {
        File ficheiro = new File("Output/" + nomeFicheiroSaida);
        PrintWriter escrever = new PrintWriter(ficheiro);
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                escrever.print(matriz[i][j]);
                if (j < matriz[i].length - 1) {
                    escrever.print(",");
                }
            }
            escrever.println();
        }
        escrever.close();
        System.out.println("Ficheiro guardado: " + ficheiro.getPath());
    }
}

