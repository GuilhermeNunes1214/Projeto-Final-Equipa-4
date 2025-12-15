import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Main{
    public static int[][] somaMatrizes(int[][] matriz1,int[][] matriz2) {
        int linhas= matriz1.length;
        int colunas= matriz1[0].length;
        int[][] matrizresultante=new int[linhas][colunas];
        for (int i=0;i < linhas;i++) {
            for (int j = 0; j < colunas; j++) {
                matrizresultante[i][j]=matriz1[i][j] + matriz2[i][j];
            }
        }
        return matrizresultante;
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