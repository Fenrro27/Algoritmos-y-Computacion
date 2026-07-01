/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package AMC_Practica1;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fenrr
 */
public class Prueba1_AMC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<Puntos> auxArray;
        ArchivosTSP aTSP = new ArchivosTSP();
        int opc = 0;
        Scanner s = new Scanner(System.in);

        do {
            try {
                menu(aTSP.getCasoPeor());
                opc = s.nextInt();

                switch (opc) {
                    case 0:
                        System.out.println("\33[31mSaliendo");
                         {
                            try {
                                sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Prueba1_AMC.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;

                    case 1://COMPROBAR TODAS LAS ESTRATEGIAS
                        comprobarEstrategias();

                        break;

                    case 2://COMPARAR 2 ESTRATEGIAS
                        compararDos();

                        break;
                    case 3://COMPARAR TODAS LAS ESTRATEGIAS
                        System.out.println("Ejecutandose, espere un segundo.");
                        compararEstrategias();
                        break;
                    case 4://ACTIVAR/DESACTIVAR PEOR CASO (TODOS LOS PUNTOS EN LA MISMA VERTICAL)
                        aTSP.setCasoPeor();

                        break;
                    case 5://CREAR FICHERO TSP ALEATORIO

                        System.out.println("Introduzca la taya del fichero aleatorio");
                        int tama = s.nextInt();
                        auxArray = aTSP.puntosAleatorios(tama);
                        aTSP.verArray(auxArray);

                        break;
                    case 6://COMPROBAR TODAS LAS ESTRATEGIAS DE UN FICHEDRO TSP CONCRETO
                        System.out.println("Introduzca el nombre del fichero TSP");
                        String nombre = s.next();
                        auxArray = aTSP.datosFichero(nombre);

                        comprobarEstrategiasTSP(auxArray);

                        //Falta codifo de comprobar
                        break;
                    default:
                        System.out.println("\33[31mError: INTRODUZCA UNA OPCION CORRECTA");
                         {
                            try {
                                sleep(2000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Prueba1_AMC.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                }
            } catch (IOException ex) {
                Logger.getLogger(Prueba1_AMC.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (opc != 0);
    }

    public static void menu(boolean peorcaso) {
        System.out.println("\n-----------------------------------------");
        System.out.println("AMC. Practica1 \t curso23/24");
        System.out.println("Kevin Jesus Banda Azogil");
        System.out.println("-----------------------------------------");
        System.out.println("Peor Caso: " + peorcaso);
        System.out.println("-----------------------------------------");

        System.out.println("\t1) COMPROBAR TODAS LAS ESTRATEGIAS");
        System.out.println("\t2) COMPARAR 2 ESTRATEGIAS");
        System.out.println("\t3) COMPARAR TODAS LAS ESTRATEGIAS");
        System.out.println("\t4) ACTIVAR/DESACTIVAR PEOR CASO (TODOS LOS PUNTOS EN LA MISMA VERTICAL)");
        System.out.println("\t5) CREAR FICHERO TSP ALEATORIO");
        System.out.println("\t6) COMPROBAR TODAS LAS ESTRATEGIAS DE UN FICHEDRO TSP CONCRETO");
        System.out.println("\t0) SALIR");
        System.out.println("-----------------------------------------");
        System.out.println("ELIGE OPCION: ");

    }

    public static void comprobarEstrategias() {
        ArchivosTSP TSPAleatorio = new ArchivosTSP();
        Algoritmos alg = new Algoritmos();
        ArrayList<Puntos> array = TSPAleatorio.puntosAleatorios(5000);
        double a, tiempo = 0;
        DistanciaPuntos dp;

        System.out.println("Extrategia\tPunto1\t\t\t\t\t\t\tPunto2\t\t\t\t\t\tDistancia\tcalculadas\tTiempo(mseg)");
        a = System.nanoTime() / 1000000.0;
        dp = alg.BusquedaExhaustiva(array);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        System.out.println("Exhaustivo\t" + dp.getPunto1() + "\t\t\t" + dp.getPunto2() + "\t\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t" + tiempo);

        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaPoda(array, true);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        System.out.println("ExhaustivoPoda\t" + dp.getPunto1() + "\t\t\t" + dp.getPunto2() + "\t\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);

        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaDYB(array);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        System.out.println("DivideVenceras\t" + dp.getPunto1() + "\t\t\t" + dp.getPunto2() + "\t\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);

        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaDYBMejor(array);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        System.out.println("DyV Mejorado\t" + dp.getPunto1() + "\t\t\t" + dp.getPunto2() + "\t\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);

    }

    public static void compararEstrategias() {

        ArchivosTSP TSPAleatorio = new ArchivosTSP();
        Algoritmos alg = new Algoritmos();
        double mediaExhaustivo = 0;
        double mediaExhaustivoPoda = 0;
        double mediaDyV = 0;
        double mediaDyVMejor = 0;
        ArrayList<Puntos> aux;
        String[] tama = {"1000", "2000", "3000", "4000", "5000"};
        double tablaInfo[][] = new double[5][4];//dimensiones de la tabla
        double a, b;

        for (int j = 1; j <= 5; j++) {
            for (int i = 0; i < 10; i++) {
                aux = TSPAleatorio.puntosAleatorios(1000 * j);

                a = System.nanoTime() / 1000000;
                alg.BusquedaExhaustiva(aux);
                b = System.nanoTime() / 1000000.0 - a;
                mediaExhaustivo += (b);

                a = System.nanoTime() / 1000000;
                alg.BusquedaDYB(aux);
                b = System.nanoTime() / 1000000.0 - a;
                mediaExhaustivoPoda += (b);

                a = System.nanoTime() / 1000000;
                alg.BusquedaDYBMejor(aux);
                b = System.nanoTime() / 1000000.0 - a;
                mediaDyV += (b);

                a = System.nanoTime() / 1000000;
                alg.BusquedaPoda(aux, true);
                b = System.nanoTime() / 1000000.0 - a;
                mediaDyVMejor += (b);

            }

            mediaExhaustivo /= 10;
            mediaExhaustivoPoda /= 10;
            mediaDyV /= 10;
            mediaDyVMejor /= 10;

            tablaInfo[j - 1][0] = new BigDecimal(mediaExhaustivo).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
            tablaInfo[j - 1][1] = new BigDecimal(mediaExhaustivoPoda).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
            tablaInfo[j - 1][2] = new BigDecimal(mediaDyV).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
            tablaInfo[j - 1][3] = new BigDecimal(mediaDyVMejor).setScale(4, RoundingMode.HALF_EVEN).doubleValue();

            mediaExhaustivo = 0;
            mediaExhaustivoPoda = 0;
            mediaDyV = 0;
            mediaDyVMejor = 0;

        }

        //Mostramos los resultados
        System.out.println("\tExhaustivo\tExhaustivoPoda\tDivideVenceras\tDyV Mejorado");
        System.out.println("Talla\tTiempo(mseg)\tTiempo(mseg)\tTiempo(mseg)\tTiempo(mseg)");
        display(tablaInfo, tama);

    }

    public static void compararDos() {
        Scanner s = new Scanner(System.in);

        ArchivosTSP TSPAleatorio = new ArchivosTSP();
        Algoritmos alg = new Algoritmos();
        double mediaExhaustivo = 0;
        double mediaExhaustivoPoda = 0;
        double mediaDyV = 0;
        double mediaDyVMejor = 0;
        ArrayList<Puntos> aux;
        String[] tama = {"1000", "2000", "3000", "4000", "5000"};
        double tablaInfo[][] = new double[5][4];//dimensiones de la tabla
        double a, b;
        int e1, e2;
        String[] cabecera = new String[4];

        System.out.println("Extrategias:\n1). Busqueda Exhaustiva\n"
                + "2). Busqueda Exhaustiva con Poda\n"
                + "3). Busqueda Divide y Venceras\n"
                + "4). Busqueda Divide y Venceras Mejorado");
        do {
            System.out.println("Extrategia 1:");
            e1 = s.nextInt() - 1;//primera extrategia
            if (e1 > 3 || e1 < 0) {
                System.out.println("Introduzca un valor valido");
            }
        } while (e1 > 3 || e1 < 0);
        do {
            System.out.println("Extrategia 2 (Tiene que ser distinto al anterior):");
            e2 = s.nextInt() - 1;//segunda extrategia
            if ((e2 > 3 || e2 < 0) || e1 == e2) {
                System.out.println("Error: Introduzca un valor valido");
            }
        } while ((e2 > 3 || e2 < 0) || e1 == e2);

        System.out.println("Espere un segundo");

        int[] calculadas = new int[2];

        int indice;

        for (int j = 1; j <= 5; j++) {
            calculadas[0] = 0;
            calculadas[1] = 0;

            for (int i = 0; i < 10; i++) {

                indice = 0;//reiniciamos el indice cada vez q entramos en los bucles

                aux = TSPAleatorio.puntosAleatorios(1000 * j);
                if (e1 == 0 || e2 == 0) {
                    a = System.nanoTime() / 1000000;
                    calculadas[indice] += alg.BusquedaExhaustiva(aux).getCalculadas();
                    b = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                    mediaExhaustivo += (b);
                    indice++;
                }

                if (e1 == 1 || e2 == 1) {
                    a = System.nanoTime() / 1000000;
                    calculadas[indice] += alg.BusquedaDYB(aux).getCalculadas();
                    b = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                    mediaExhaustivoPoda += (b);
                    indice++;
                }
                if (e1 == 2 || e2 == 2) {
                    a = System.nanoTime() / 1000000;
                    calculadas[indice] += alg.BusquedaDYBMejor(aux).getCalculadas();
                    b = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                    mediaDyV += (b);
                    indice++;
                }
                if (e1 == 3 || e2 == 3) {
                    a = System.nanoTime() / 1000000;
                    calculadas[indice] += alg.BusquedaPoda(aux, true).getCalculadas();
                    b = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                    mediaDyVMejor += (b);
                }
            }

            int k = 0;

            if (e1 == 0 || e2 == 0) {
                tablaInfo[j - 1][k] = new BigDecimal(mediaExhaustivo / 10000.0).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                tablaInfo[j - 1][k + 2] = calculadas[k] / 10;

                cabecera[k] = "\tExhaustivo";
                cabecera[k + 2] = "\tExhaustivo";

                k++;
            }
            if (e1 == 1 || e2 == 1) {
                tablaInfo[j - 1][k] = new BigDecimal(mediaExhaustivoPoda / 10000.0).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                tablaInfo[j - 1][k + 2] = calculadas[k] / 10;

                cabecera[k] = "\tExhaustivoPoda";
                cabecera[k + 2] = "\tExhaustivoPoda";

                k++;
            }
            if (e1 == 2 || e2 == 2) {
                tablaInfo[j - 1][k] = new BigDecimal(mediaDyV / 10000.0).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                tablaInfo[j - 1][k + 2] = calculadas[k] / 10;

                cabecera[k] = "\tDivideVenceras";
                cabecera[k + 2] = "\tDivideVenceras";
                k++;
            }
            if (e1 == 3 || e2 == 3) {
                tablaInfo[j - 1][k] = new BigDecimal(mediaDyVMejor / 10000.0).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                tablaInfo[j - 1][k + 2] = calculadas[k] / 10;

                cabecera[k] = "\tDyV Mejor";
                cabecera[k + 2] = "\tDyV Mejor";
            }

            mediaExhaustivo = 0;
            mediaExhaustivoPoda = 0;
            mediaDyV = 0;
            mediaDyVMejor = 0;

        }
        //Mostramos las calculadas

        //Mostramos los resultados
        System.out.println(cabecera[0] + cabecera[1] + cabecera[2] + cabecera[3]);
        System.out.println("Talla\tTiempo(mseg)\tTiempo(mseg)\tCalculadas\tcalculadas");
        display(tablaInfo, tama);

    }

    /*
    x es el valor de los enteros a representar
     */


    private static void comprobarEstrategiasTSP(ArrayList<Puntos> auxArray) {
        Algoritmos alg = new Algoritmos();
        ArrayList<Puntos> array = (ArrayList<Puntos>) auxArray.clone();
        double a, tiempo = 0;
        DistanciaPuntos dp;

        System.out.println("Extrategia\tPunto1\t\t\t\t\tPunto2\t\t\t\t\tDistancia\tcalculadas\tTiempo(mseg)");
        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaExhaustiva(array);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        System.out.println("Exhaustivo\t" + dp.getPunto1() + "\t" + dp.getPunto2() + "\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);
        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaPoda(array, true);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();

        System.out.println("ExhaustivoPoda\t" + dp.getPunto1() + "\t" + dp.getPunto2() + "\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);
        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaDYB(array);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();

        System.out.println("DivideVenceras\t" + dp.getPunto1() + "\t" + dp.getPunto2() + "\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);
        a = System.nanoTime() / 1000000;
        dp = alg.BusquedaDYBMejor(array);
        tiempo = new BigDecimal(System.nanoTime() / 1000000.0 - a).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
        System.out.println("DyV Mejorado\t" + dp.getPunto1() + "\t" + dp.getPunto2() + "\t" + new BigDecimal(dp.getDistancia()).setScale(8, RoundingMode.HALF_EVEN).doubleValue() + "\t" + dp.getCalculadas() + "\t\t" + tiempo);
    }

    
    
        public static void display(double x[][], String[] PrimeraColumna) {

        for (int fila = 0; fila < x.length; fila++) {
            System.out.print(PrimeraColumna[fila] + "\t");
            for (int columna = 0; columna < x[fila].length; columna++) {
                System.out.print(x[fila][columna] + "\t\t");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------");
    }
}
