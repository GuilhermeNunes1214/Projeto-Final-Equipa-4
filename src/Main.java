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
}