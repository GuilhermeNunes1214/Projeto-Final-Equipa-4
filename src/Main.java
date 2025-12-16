public class Main{

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