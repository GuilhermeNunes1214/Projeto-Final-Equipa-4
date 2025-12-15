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