package es.uhu.amc.amcp2;


import es.uhu.amc.amcp2.ModoGrafico.SeleccionVentana;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class centro{
    public centro() {
        int opc = -1;
        Object automata;
        boolean tipo = false;//variable en la q vamos a guardar el tipo con el q trabajamos. true si es determinista
        boolean ejecutado = false;
        AutomataFinitoNoDeterminista AFND = new AutomataFinitoNoDeterminista();
        AutomataFinitoDeterminista AFD = new AutomataFinitoDeterminista();
        JFileChooser fc=new JFileChooser ();
        Scanner sc = new Scanner(System.in);
        do {
            try {
                opc = menu(ejecutado, tipo);

                switch (opc) {
                    case 0:
                        System.out.println("\033[31mSaliendo de la aplicacion\u001B[0m");
                        Thread.sleep(1000);
                        break;
                    case 1: //carga fichero

                        SeleccionVentana s = new SeleccionVentana();
                        if(s.isElegido()) {
                            System.out.println("\033[32mArchivo Cargado: " + s.getArchivo()+" \u001B[0m");

                            tipo = leerTipoFichero(s.getArchivo());

                            if (tipo) {
                                AFD.load(s.getArchivo());
                            } else {
                                AFND.load(s.getArchivo());
                            }
                            ejecutado = true;

                        }
                        else{
                            System.out.println("\033[31mNo se ha elegido ningun archivo\u001B[0m");
                        }

                        break;
                    case 2://carga de teclado
                        char aut;
                        boolean valido = false;
                        do {
                            System.out.print("¿Que tipo de automata deseas crear? (D/N): ");
                            aut = sc.next().toCharArray()[0];
                            if(aut=='D' || aut=='d'){
                                valido = true;
                                AFD = AutomataFinitoDeterminista.pedir();
                                tipo = true;
                            } else if (aut=='N' || aut=='n'){
                                valido = true;
                                AFND = AutomataFinitoNoDeterminista.pedir();
                                tipo = false;
                            } else {
                                System.out.println("\033[31mOpcion no valida\u001B[0m");
                            }
                        } while (!valido);
                        System.out.println("\033[32mAutomata cargado correctamente\u001B[0m");
                        ejecutado = true;

                        break;
                    case 3://comprobar por paso
                        if(ejecutado) {
                            System.out.print("Introduzca la cadena a comprobar: ");
                            String cadena = sc.nextLine();
                            boolean cadenaValida;
                            if(tipo){
                                cadenaValida = AFD.reconocerPaso(cadena);
                            } else {
                                cadenaValida = AFND.reconocerPaso(cadena);
                            }
                            if(cadenaValida){
                                System.out.println("La cadena es \033[32mvalida\u001B[0m");
                            } else {
                                System.out.println("La cadena es \033[31mno valida\u001B[0m");
                            }
                        } else {
                            System.out.println("\033[31mError: Automata no cargado\u001B[0m");
                        }
                        break;
                    case 4://comprobar de golpe
                        if(ejecutado) {
                            System.out.print("Introduzca la cadena a comprobar: ");
                            String cadena = sc.nextLine();
                            boolean cadenaValida;
                            if(tipo){
                                cadenaValida = AFD.reconocer(cadena);
                            } else {
                                cadenaValida = AFND.reconocer(cadena);
                            }
                            if(cadenaValida){
                                System.out.println("La cadena es \033[32mvalida\u001B[0m");
                            } else {
                                System.out.println("La cadena es \033[31mno valida\u001B[0m");
                            }
                        } else {
                            System.out.println("\033[31mError: Automata no cargado\u001B[0m");
                        }
                        break;
                    default:
                        System.out.println("\033[31mError: Opcion incorrecta\u001B[0m");

                }
            } catch (Exception ex) {
                System.out.println("\033[31mError: " + ex.getMessage() + "\u001B[0m");
            }
        } while (opc != 0);




    }

    private static int menu(boolean cargado, boolean tipo) {
        Scanner sc = new Scanner(System.in);
        System.out.println("------------------------------------------");
        if(cargado){
            if(tipo){
                System.out.println("\033[32mAutomata cargado: AFD\u001B[0m");
            } else {
                System.out.println("\033[32mAutomata cargado: AFND\u001B[0m");
            }
        } else {
            System.out.println("\033[31mAutomata no cargado\u001B[0m");
        }
        System.out.println("------------------------------------------" +
                "\n0). Salir" +
                "\n1). Cargar automata desde el fichero" +
                "\n2). Leer automata desde teclado" +
                "\n3). Simulacion paso por paso" +
                "\n4). Simulacion de golpe" +
                "\n------------------------------------------");

        System.out.print("Dime la opcion: ");
        return sc.nextInt();
    }

    /**
     * Metodo usado para saber si el fichero que estamos leyendo pertenece a un AFD o  a un AFND
     *
     * @param filePath string de la localizacion del fichero
     * @return devuelve el tipo de fichero q estamos comprobando
     */
    private static boolean leerTipoFichero(String filePath) throws IOException {

        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(new File(filePath))));

        boolean encontrado = false;
        String linea;
        StringTokenizer st = null;

        while (!encontrado) {
            linea = br.readLine();
            st = new StringTokenizer(linea);
            if (st.nextToken().equals("TIPO:")) {
                encontrado = true;
            }
        }
        return st.nextToken().equals("AFD"); //el determinista devuelve true

    }
}