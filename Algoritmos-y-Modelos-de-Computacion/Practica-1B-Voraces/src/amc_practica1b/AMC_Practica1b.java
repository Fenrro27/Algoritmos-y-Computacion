/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package amc_practica1b;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kjba2
 */
public class AMC_Practica1b {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Algoritmos alg = new Algoritmos();
        ArchivosTSP aTSP = new ArchivosTSP();
        ArrayList<Puntos> array = null;
        Resultados r;
        int opc = 0;
        Scanner s = new Scanner(System.in);
        boolean arrayCargado = false;
        //Resultados r = alg.vorazUnidireccional(array);

        do {
            try {
                menu();
                opc = s.nextInt();

                switch (opc) {
                    case 0:
                        System.out.println("\33[31mSaliendo");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AMC_Practica1b.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        break;

                    case 1://Cargar TSP Random
                        System.out.println("Introduzca la talla del fichero aleatorio");
                        int tama = s.nextInt();
                        array = aTSP.puntosAleatorios(tama);
                        System.out.println("Array cargado:");
                        aTSP.verArray(array);
                        System.out.println("-------------------------\n");
                        arrayCargado = true;
                        break;

                    case 2://COMPARAR TSP desde fichero
                        
                        System.out.println("Introduzca el nombre del fichero TSP");
                        String nombre = s.next();
                        array = aTSP.datosFichero(nombre);
                        System.out.println("Array cargado:");
                        aTSP.verArray(array);
                        System.out.println("-------------------------\n");
                        arrayCargado = true;

                        break;

                    case 3://Estrategia Unidireccional
                        if (arrayCargado) {
                            System.out.println("Ejecutandose Unidireccional, espere un segundo.");
                            double a = System.nanoTime() / 1000000.0;
                            r = alg.vorazUnidireccional(array);
                            double tiempoA = System.nanoTime() / 1000000.0 - a;
                            //mostramos la informacion
                            System.out.println("Coste: " + r.getCosteTotal());
                            System.out.println("Camino: "+r.getStringCamino());
                            System.out.println("Tiempo de ejecucion(mseg): "+tiempoA);
                            System.out.println("Costes de arista:\n"+r.getCostArist());
                        } else {
                            System.out.println("\33[31mNecesita cargar un array en memoria\u001B[0m");
                        }

                        break;
                    case 4://Estrategia Bidireccional
                        if (arrayCargado) {
                            System.out.println("Ejecutandose Bidireccional, espere un segundo.");
                            double a = System.nanoTime() / 1000000.0;
                            r = alg.vorazBidireccional(array);
                            double tiempoA = System.nanoTime() / 1000000.0 - a;
                            //mostramos la informacion
                            System.out.println("Coste: " + r.getCosteTotal());
                            System.out.println("Camino: "+r.getStringCamino());
                            System.out.println("Tiempo de ejecucion(mseg): "+tiempoA);
                            System.out.println("Costes de arista:\n"+r.getCostArist());
                        } else {
                            System.out.println("\33[31mNecesita cargar un array en memoria\u001B[0m");
                        }

                        break;
                    case 5://Comparar Estrategias
                        System.out.println("Ejecutandose, espere un poco.");
                        System.out.println("-----------------------------------------");
                        compararEstrategias();

                        break;

                    default:
                        System.out.println("\33[31mError: INTRODUZCA UNA OPCION CORRECTA\u001B[0m");
                         {

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(AMC_Practica1b.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                }
            } catch (IOException ex) {
                Logger.getLogger(AMC_Practica1b.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (opc != 0);

    }

    public static void menu() {
        System.out.println("\n-----------------------------------------");
        System.out.println("\u001B[33mAMC. Practica1 \t curso23/24\nKevin Jesus Banda Azogil\u001B[0m");
        System.out.println("-----------------------------------------");
        System.out.println("\t1) Cargar TSP Aleatorio");
        System.out.println("\t2) Cargar TSP Especifico");
        System.out.println("\t3) Estrategia Unidireccional");
        System.out.println("\t4) Estrategia Bidireccional");
        System.out.println("\t5) Comparar Estrategias");
        System.out.println("\t0) SALIR");
        System.out.println("-----------------------------------------");
        System.out.println("ELIGE OPCION: ");

    }

    public static void compararEstrategias() {
        Algoritmos alg = new Algoritmos();
        double a, tiempoA = 0, tiempoB = 0;
        ArchivosTSP TSPAleatorio = new ArchivosTSP();
        ArrayList<Puntos> array;
        double mediaUnidireccional = 0;
        double mediaBidireccional = 0;
        int ganaUnidireccional = 0;
        Resultados r1= new Resultados();
        Resultados r2= new Resultados();
        
        int nComparaciones=10;

        for (int j = 1; j <= 10; j++) {
            ganaUnidireccional=0;
            for (int i = 0; i < nComparaciones; i++) {
                array = TSPAleatorio.puntosAleatorios(500 * j);

                a = System.nanoTime() / 1000000.0;
                r1=alg.vorazUnidireccional(array);
                tiempoA = System.nanoTime() / 1000000.0 - a;
                mediaUnidireccional += tiempoA;

                a = System.nanoTime() / 1000000.0;
                r2=alg.vorazBidireccional(array);
                tiempoB = System.nanoTime() / 1000000.0 - a;
                mediaBidireccional += tiempoB;

                if (r1.getCosteTotal() < r2.getCosteTotal()) {
                    ganaUnidireccional++;//gana el q menos distancia total genere
                }

            }
            System.out.println("\u001B[32mResultados de la talla " + 500 * j+"\u001B[0m");
            System.out.println("-----------------------------------------");
            System.out.println("Veces que ha ganado Unidireccional: " + ganaUnidireccional);
            System.out.println("Veces que ha ganado Bidireccional: " + (nComparaciones - ganaUnidireccional));

            System.out.println("Media de tiempo Unidireccional (msge): " + new BigDecimal(mediaUnidireccional / nComparaciones).setScale(4, RoundingMode.HALF_EVEN).doubleValue());
            System.out.println("Media de tiempo Bidireccional (mseg): " + new BigDecimal(mediaBidireccional / nComparaciones).setScale(4, RoundingMode.HALF_EVEN).doubleValue());
            System.out.println("-----------------------------------------");

        }

    }

}
