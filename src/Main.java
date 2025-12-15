public class Main{

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
}