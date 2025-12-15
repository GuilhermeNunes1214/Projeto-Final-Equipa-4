import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Main{

    public static int Zc = 4;

       //||||||||||||||||||||||||||||||||||||||||||||||\\
      //                                                \\
     //      Modulo Checar Estabilidade de matrizes      \\
    //                                                    \\
    public boolean ChecadorDeEstabilidadeDeMatrizes ( int [][] matriz){

        //Ciclos "enchented for" percorrem todos os termos da matriz
        for ( int i = 0; i < matriz.length - 1; i++ ){
            for ( int j = 0; j < matriz[i].length - 1; j++ ){
                //Termo selecionado
                if ( matriz[i][j] >= Zc){
                    //Se um termo selecionado for maior ou igual a Zc, a Matriz não é estável
                    return false;
                }
            }
        }
        //Se depois dos ciclos "for" nenum termo for maior ou igual a Zc, a matriz é estável
        return true;
    }
}