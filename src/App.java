import java.util.Random;
import java.util.Scanner;

public class App {
    /**
     * Scanner para la entrada de datos por teclado.
     */
    private static Scanner sc = new Scanner(System.in);

    /**
     * Tamaño del tablero de juego.
     */
    private static int TAM = 10;

    /**
     * Tablero del jugador 1. 
     * Cada posición contiene:
     * 0 = agua, 1-5 = barco sin tocar, 6 = barco tocado, 7 = disparo a agua.
     */
    private static int barcosJ1[][] = new int[TAM][TAM];

    /**
     * Tablero del jugador 2.
     * Igual que {@link #barcosJ1}.
     */
    private static int barcosJ2[][] = new int[TAM][TAM];

    /**
     * Número de casillas de barco que quedan por hundir del jugador 1.
     */
    private static int nBarcos1;

    /**
     * Número de casillas de barco que quedan por hundir del jugador 2.
     */
    private static int nBarcos2;

    /**
     * Matriz auxiliar para colocar barcos temporalmente al generar el tablero.
     */
    private static int matrizAux[][] = new int[TAM][TAM];

    /**
     * Cantidad de barcos por tipo (índice 0 = tamaño 1, índice 4 = tamaño 5).
     */
    private static final int cantidad[] = { 5, 4, 3, 2, 1 };

    /**
     * Tamaño de los barcos (1 a 5 casillas).
     */
    private static final int tamanios[] = { 1, 2, 3, 4, 5 };

    /**
     * Nombres de los barcos según su tamaño.
     */
    private static final String[] nombres = { "Lancha", "Crucero", "Submarino", "Buque", "Portaaviones" };

    /**
     * Direcciones posibles para colocar los barcos: arriba, derecha, abajo, izquierda.
     */
    private static final int direcciones[][] = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

    // Colores ANSI para imprimir el tablero en consola
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREY = "\u001B[90m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String[] colores = { ANSI_BLACK, ANSI_CYAN, ANSI_BLUE, ANSI_YELLOW, ANSI_GREEN, ANSI_PURPLE, ANSI_RED, ANSI_GREY };

    /**
     * Método principal que inicia el juego.
     *
     * @param args Argumentos de la línea de comandos (no se usan).
     * @throws Exception Si ocurre algún error de ejecución inesperado.
     */
    public static void main(String[] args) throws Exception {
        prepararJuego();

        sc.close();
    }

    /**
     * Muestra un menú de selección de modo de juego y devuelve la opción elegida.
     * 
     * Muestra un menú en consola con opciones: 1 = PVP, 2 = PVE, 0 = salir.
     * Valida la entrada del usuario y repite la solicitud hasta que sea correcta.
     *
     * @return Opción elegida por el usuario: 0 = Salir, 1 = PVP, 2 = PVE.
     */
    public static int menuJuego() {

        int opcion;

        System.out.println();
        System.out.println(ANSI_BLUE + "==============================");
        System.out.println(ANSI_BLUE + "      HUNDIR LA FLOTA");
        System.out.println(ANSI_BLUE + "==============================" + ANSI_WHITE);

        System.out.println(ANSI_YELLOW + " 1 " + ANSI_WHITE + "- PvP");
        System.out.println(ANSI_CYAN   + " 2 " + ANSI_WHITE + "- PvE");
        System.out.println(ANSI_GREEN  + " 0 " + ANSI_WHITE + "- Salir");
        System.out.println();

        System.out.print("Selecciona una opcion: ");
        opcion = sc.nextInt();

        while (opcion < 0 || opcion > 2) {
            System.out.println(ANSI_RED + "Opcion no valida." + ANSI_WHITE);
            System.out.print("Vuelve a introducir una opcion: ");
            opcion = sc.nextInt();
        }

        return opcion;
    }

    /**
     * Inicializa los tableros de ambos jugadores, calcula los barcos y solicita el modo de juego.
     * Dependiendo de la opción elegida, inicia PVP o PVE.
     * 
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Llama a {@link #generarTablero()} para crear los tableros de ambos jugadores.</li>
     * <li>Calcula el número total de casillas de barco con {@link #calcularNBarcos(int[], int[])}.</li>
     * <li>Muestra ambos tableros completos.</li>
     * <li>Muestra el menú de selección de juego y ejecuta el modo seleccionado.</li>
     * </ol>
     *
     * @postcondición Los tableros de los jugadores están generados y el juego inicia en el modo seleccionado.
     */
    public static void prepararJuego() {

        barcosJ1 = generarTablero();
        barcosJ2 = generarTablero();

        nBarcos1 = calcularNBarcos(cantidad, tamanios);
        nBarcos2 = calcularNBarcos(cantidad, tamanios);

        System.out.println(ANSI_GREEN + "Tablero del Jugador 1 cargado correctamente." + ANSI_WHITE);
        System.out.println(ANSI_GREEN + "Tablero del Jugador 2 cargado correctamente." + ANSI_WHITE);

        int modoJuego = menuJuego();

        switch (modoJuego) {
            case 1:
                System.out.println(ANSI_YELLOW + "Modo PvP seleccionado" + ANSI_WHITE);
                jugarPVP();
                break;

            case 2:
                System.out.println(ANSI_YELLOW + "Modo PvE seleccionado" + ANSI_WHITE);
                jugarPVE();
                break;

            default:
                System.out.println(ANSI_RED + "Saliendo..." + ANSI_WHITE);
        }
    }


    /**
     * Calcula el número total de casillas de barco dadas las cantidades y tamaños de barcos.
     *
     * @param cantidades Array con la cantidad de barcos por tipo.
     * @param tamanios Array con los tamaños de los barcos correspondientes.
     * @return Total de casillas de barco.
     * @precondición {@code cantidades.length == tamanios.length}.
     */
    public static int calcularNBarcos(int[] cantidades, int[] tamanios){
        int total=0;
        for (int i = 0; i < tamanios.length; i++) {
            total+=cantidades[i] * tamanios[i];
        }

        return total;
    }

    /**
     * Ejecuta el modo Jugador vs Jugador.
     * Permite que ambos jugadores disparen alternativamente hasta que uno gane.
     *
     * @precondición Los tableros de ambos jugadores deben estar inicializados.
     * @postcondición El juego termina cuando {@link #nBarcos1} o {@link #nBarcos2} llega a 0.
     */
    public static void jugarPVP() {
        int x, y;
        boolean turnoJugador1 = true;

        while (nBarcos1 > 0 && nBarcos2 > 0) {

            if (turnoJugador1) {
                System.out.println(ANSI_YELLOW + "Turno del Jugador 1" + ANSI_WHITE);
                mostrarJugador1();

                System.out.print("Ingresa fila: ");
                x = sc.nextInt();
                System.out.print("Ingresa columna: ");
                y = sc.nextInt();

                if (disparar(barcosJ2, x, y)) {
                    nBarcos2--;
                    System.out.println(ANSI_GREEN + "Tocado!" + ANSI_WHITE);
                } else {
                    System.out.println(ANSI_CYAN + "Agua..." + ANSI_WHITE);
                }

            } else {
                System.out.println(ANSI_YELLOW + "Turno del Jugador 2" + ANSI_WHITE);
                mostrarJugador2();

                System.out.print("Ingresa fila: ");
                x = sc.nextInt();
                System.out.print("Ingresa columna: ");
                y = sc.nextInt();

                if (disparar(barcosJ1, x, y)) {
                    nBarcos1--;
                    System.out.println(ANSI_GREEN + "Tocado!" + ANSI_WHITE);
                } else {
                    System.out.println(ANSI_CYAN + "Agua..." + ANSI_WHITE);
                }
            }

            turnoJugador1 = !turnoJugador1;
        }

        System.out.println();
        if (nBarcos1 == 0) {
            System.out.println(ANSI_RED + "Gana el Jugador 2!!" + ANSI_WHITE);
        } else {
            System.out.println(ANSI_RED + "Gana el Jugador 1!!" + ANSI_WHITE);
        }
    }



    /**
     * Ejecuta el modo Jugador vs Máquina.
     * La máquina dispara aleatoriamente.
     *
     * @precondición Los tableros de ambos jugadores deben estar inicializados.
     * @postcondición El juego termina cuando {@link #nBarcos1} o {@link #nBarcos2} llega a 0.
     */
    public static void jugarPVE() {

        Random aleatorio = new Random();
        int filaJugador, colJugador;
        int filaCPU, colCPU;

        while (nBarcos1 > 0 && nBarcos2 > 0) {

            System.out.println(ANSI_CYAN + "Tu turno:" + ANSI_WHITE);
            mostrarJugador1();

            System.out.print("Introduce la fila: ");
            filaJugador = sc.nextInt();
            System.out.print("Introduce la columna: ");
            colJugador = sc.nextInt();

            if (disparar(barcosJ2, filaJugador, colJugador)) {
                nBarcos2--;
                System.out.println(ANSI_GREEN + "Tocado!" + ANSI_WHITE);
            } else {
                System.out.println(ANSI_GREY + "Agua..." + ANSI_WHITE);
            }


            filaCPU = aleatorio.nextInt(TAM);
            colCPU = aleatorio.nextInt(TAM);

            System.out.println(ANSI_PURPLE + "La Máquina dispara a: (" + filaCPU + "," + colCPU + ")" + ANSI_WHITE);

            if (disparar(barcosJ1, filaCPU, colCPU)) {
                nBarcos1--;
                System.out.println(ANSI_RED + "Han tocado tu barco!" + ANSI_WHITE);
            } else {
                System.out.println(ANSI_GREY + "Han fallado." + ANSI_WHITE);
            }
        }

    
        if (nBarcos1 == 0) {
            System.out.println(ANSI_RED + "Has perdido..." + ANSI_WHITE);
        } else {
            System.out.println(ANSI_GREEN + "Enhorabuena! ¡Has ganado!" + ANSI_WHITE);
        }
    }


    /**
     * Realiza un disparo sobre el tablero especificado.
     *
     * @param matriz Tablero donde se dispara.
     * @param x Coordenada X (fila) del disparo.
     * @param y Coordenada Y (columna) del disparo.
     * @return {@code true} si se tocó un barco, {@code false} si fue agua o disparo repetido.
     * @precondición {@code 0 <= x < TAM && 0 <= y < TAM}.
     * @postcondición La matriz queda actualizada con el resultado del disparo (6 = tocado, 7 = agua).
     */
    public static boolean disparar(int[][] matriz, int x, int y) {
        if (matriz[x][y] >= 1 && matriz[x][y] <= 5) {
            matriz[x][y] = 6;
            return true;
        } else if (matriz[x][y] == 0) {
            matriz[x][y] = 7;
        }
        return false;
    }


    /**
     * Determina si un barco ha sido tocado o hundido a partir de la casilla disparada.
     *
     * @param matriz Tablero donde se encuentra el barco.
     * @param x Coordenada X (fila) del disparo.
     * @param y Coordenada Y (columna) del disparo.
     * @return {@code true} si el barco está completamente hundido, {@code false} si solo ha sido tocado.
     */
    public static boolean cantarDisparo(int[][] matriz, int x, int y) {
        // TODO OPCIONAL función cantarDisparo
        return false;
    }

    // #region Preparación del tablero

    /**
     * Genera un tablero aleatorio con los barcos colocados.
     * 
     * Para cada tipo de barco:
     * <ul>
     * <li>Se intenta colocar la cantidad correspondiente de barcos de ese tamaño.</li>
     * <li>Se elige una posición aleatoria y una dirección válida usando {@link #comprobarDirecciones(int, int, int)}.</li>
     * <li>Se coloca el barco con {@link #copiarBarcoEn(int, int, int, int)}.</li>
     * </ul>
     *
     * @return Matriz {@link int[][]} de tamaño {@link #TAM} x {@link #TAM} con los barcos colocados.
     *         0 = agua, 1-5 = barco sin tocar.
     * @precondición {@link #TAM} debe ser mayor que 0.
     * @postcondición {@link #matrizAux} queda reiniciada a cero y se devuelve un tablero completo.
     */
    public static int[][] generarTablero() {
        Random r = new Random();
        int x, y, direccion;

        for (int i = cantidad.length - 1; i >= 0; i--) {
            for (int j = 0; j < cantidad[i]; j++) {
                do {
                    x = r.nextInt(TAM);
                    y = r.nextInt(TAM);
                } while ((direccion = comprobarDirecciones(x, y, tamanios[i])) == -1);

                copiarBarcoEn(x, y, direccion, tamanios[i]);
            }
        }

        int[][] matrizJuego = new int[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            matrizJuego[i] = matrizAux[i].clone();
        }
        matrizAux = new int[TAM][TAM];
        return matrizJuego;
    }

    /**
     * Comprueba si una posición (x,y) está libre para colocar un barco. Comprueba que todas las casillas del barco tengan espacio
     * para lo cual debe haber al menos una casilla vacía entre barco y barco.
     * 
     * La comprobación se realiza en la matriz matrizAux
     *
     * @param x Fila de la posición a comprobar.
     * @param y Columna de la posición a comprobar.
     * @return {@code true} si la posición y sus adyacentes están libres, {@code false} en caso contrario.
     */
    public static boolean comprobarPosicion(int fila, int col) {

        if (fila < 0 || fila >= TAM || col < 0 || col >= TAM) {
            return false;
        }

        if (matrizAux[fila][col] != 0) {
            return false;
        }

        for (int i = 0; i < direcciones.length; i++) {
            int nuevaFila = fila + direcciones[i][0];
            int nuevaCol = col + direcciones[i][1];

            if (nuevaFila >= 0 && nuevaFila < TAM && nuevaCol >= 0 && nuevaCol < TAM) {
                if (matrizAux[nuevaFila][nuevaCol] != 0) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Determina una dirección viable para colocar un barco de tamaño dado desde (x,y).
     *
     * @param x Fila de inicio.
     * @param y Columna de inicio.
     * @param tamBarco Tamaño del barco.
     * @return Índice de la dirección válida (0=arriba,1=derecha,2=abajo,3=izquierda), -1 si no hay direcciones válidas.
     * @precondición {@code 1 <= tamBarco <= 5} y {@code 0 <= x < TAM && 0 <= y < TAM}.
     */
    public static int comprobarDirecciones(int x, int y, int tamBarco) {
        Random r = new Random();
        int[] direccionesViables = new int[4];
        int nDireccionesViables = 0;
        boolean viable = true;
        if (!comprobarPosicion(x, y))
            return -1;

        if (tamBarco == 1)
            return 1;

        for (int i = 0; i < direcciones.length; i++) {
            viable = true;
            for (int j = 0; j < tamBarco; j++) {
                if (!comprobarPosicion(x + direcciones[i][0] * j, y + direcciones[i][1] * j)) {
                    viable = false;
                }
            }

            if (viable) {
                direccionesViables[nDireccionesViables] = i;
                nDireccionesViables++;
            }
        }

        if (nDireccionesViables == 0)
            return -1;
        else
            return direccionesViables[r.nextInt(nDireccionesViables)];
    }

    
    /**
     * Copia un barco en la posición (x,y) siguiendo la dirección indicada.
     *
     * @param x Fila inicial.
     * @param y Columna inicial.
     * @param direccion Dirección del barco (0=arriba,1=derecha,2=abajo,3=izquierda).
     * @param tamanio Tamaño del barco.
     * @precondición {@code 0 <= x,y < TAM}, {@code direccion ∈ [0,3]}, {@code tamanio > 0}.
     * @postcondición {@link #matrizAux} queda modificada con el barco colocado.
     */
    public static void copiarBarcoEn(int x, int y, int direccion, int tamanio) {
        for (int i = 0; i < tamanio; i++) {
            matrizAux[x + direcciones[direccion][0] * i][y + direcciones[direccion][1] * i] = tamanio;
        }
    }

    // #endregion

    
    /**
     * Muestra por consola el tablero completo, incluyendo barcos y disparos.
     *
     * @param matriz Tablero a mostrar.
     */
    public static void mostrarTablero(int[][] matriz) {
        // TODO función mostrarTablero
    }

    
    /**
     * Muestra el tablero del jugador 1 y el tablero rival, ocultando los barcos enemigos.
     */
    public static void mostrarJugador1() {
        // TODO función mostrarJugador1
    }

    /**
     * Muestra el tablero del jugador 2 y el tablero rival, ocultando los barcos enemigos.
     */
    public static void mostrarJugador2() {
        // TODO función mostrarJugador2
    }
}
