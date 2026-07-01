package es.uhu.amc.amcp2;


import com.sun.tools.jdeprscan.scan.Scan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

class TransicionAFD {
    private String origen;
    private char simbolo;
    private String destino;


    /**
     * Constructor en el que se inicializan los atributos de la transición con los pasados por parámetro.
     * @param e1 Estado inicial de la transición.
     * @param simbolo Símbolo que inicia la transición hacia el estado final.
     * @param e2 Estado final de la transición.
     */
    public TransicionAFD(String e1, char simbolo, String e2) {
        origen = e1;
        this.simbolo = simbolo;
        destino = e2;

    }

    /**
     * Método que devuelve el estado inicial de la transición.
     * @return String con el nombre del estado inicial.
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Método que permite actualizar el estado inicial de la transición con el estado pasado por parámetro.
     * @param origen String con el nombre del nuevo estado inicial.
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * Método que devuelve el símbolo que inicia la transición.
     * @return Char que contiene el símbolo correspondiente.
     */
    public char getSimbolo() {
        return simbolo;
    }

    /**
     * Método que permite actualizar el símbolo que inicia la transición con el pasado por parámetro.
     * @param simbolo Char con el nuevo símbolo de la transición.
     */
    public void setSimbolo(char simbolo) {
        this.simbolo = simbolo;
    }

    /**
     * Método que devuelve el estado final de la transición.
     * @return String que contiene el nombre del estado final.
     */
    public String getDestino() {
        return destino;
    }

    /**
     * Método que permite actualizar el estado final de la transición.
     * @param destino String que contiene el nombre del nuevo estado final.
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }


}

public class AutomataFinitoDeterminista implements IAutomataFinitoDeterminista {

    private String estadoInicial;//indica el estado inicial
    private String[] estadosFinales; //indica cuales son los estados Finales
    private List<TransicionAFD> transiciones; //indica la lista de transiciones del AFD

    /**
     * Constructor que genera un autómata vacío, inicializando la lista de transiciones para ser
     * utilizada en otros métodos.
     */
    public AutomataFinitoDeterminista() {
        transiciones = new ArrayList<>();
    }
    /**
     * Constructor al que le pasamos el estado inicial y los estados finales
     *
     * @param estadoInicial
     * @param estadosFinales
     */
    public AutomataFinitoDeterminista(String estadoInicial, String[] estadosFinales) {
        this.estadoInicial = estadoInicial;
        this.estadosFinales = estadosFinales;
        transiciones = new ArrayList<>();
    }


    /**
     * Método que devuelve el estado inicial del autómata.
     * @return Un string que contiene el nombre del estado inicial.
     */
    public String getEstadoInicial() {
        return estadoInicial;
    }

    /**
     * Método con el que se actualiza el estado inicial del autómata al estado pasado por parámetro.
     * @param estadoInicial Estado (string) que será el nuevo estado inicial del autómata.
     */
    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    /**
     * Método que devuelve el conjunto de estados finales del autómata.
     * @return Un array de string que contiene todos los estados finales.
     */
    public String[] getEstadosFinales() {
        return estadosFinales;
    }

    /**
     * Método que permite reemplazar la lista de estados finales del autómata por la lista pasada por parámetro.
     * @param estadosFinales Array de string conteniendo los nuevos estados finales del autómata.
     */
    public void setEstadosFinales(String[] estadosFinales) {
        this.estadosFinales = estadosFinales;
    }

    /**
     * Método que devuelve la lista de transiciones del autómata.
     * @return Lista de TransicionAFD que contiene todas las transiciones posibles del autómata.
     */
    public List<TransicionAFD> getTransiciones() {
        return transiciones;
    }

    /**
     * Método que permite reemplazar la lista de transiciones del autómata por la lista pasada por parámetro.
     * @param transiciones Lista de TransicionAFD que será la nueva lista de transiciones del autómata.
     */
    public void setTransiciones(List<TransicionAFD> transiciones) {
        this.transiciones = transiciones;
    }

    /**
     * Método que añade la transición pasada por parámetro a la lista de transiciones del autómata.
     *
     * @param e1 Estado inicial de la transición.
     * @param simbolo Símbolo que inicia la transición hacia el estado final.
     * @param e2 Estado final de la transición.
     */
    public void agregarTransicion(String e1, char simbolo, String e2) {
        transiciones.add(new TransicionAFD(e1, simbolo, e2));
    }

    /**
     * Método que, dados un estado y un símbolo por parámetro, devuelve el estado destino de la transición,
     * o un espacio en blanco en caso de que no exista dicha transición.
     *
     * @param estado Estado inicial de la transición que se busca.
     * @param simbolo Símbolo que inicia la transición entre el estado inicial indicado y el estado final que se busca.
     * @return Estado final (string) de la transición, o un string con un solo carácter de espacio.
     */
    public String transicion(String estado, char simbolo) {
        String res = " ";
        int i = 0;
        while (i < transiciones.size() && res.equals(" ")) {
            if (transiciones.get(i).getOrigen().equals(estado) && transiciones.get(i).getSimbolo() == simbolo) {
                res = transiciones.get(i).getDestino();
            } else {
                i++;
            }
        }
        return res;
    }

    /**
     * Método que comprueba si el estado pasado por parámetro es un estado final del autómata.
     *
     * @param estado Estado a comprobar.
     * @return True si el estado es un estado final, false si no lo es.
     */
    @Override
    public boolean esFinal(String estado) {

        int i = 0;
        while (i < estadosFinales.length) {
            if (estadosFinales[i].equals(estado))
                return true;
            i++;
        }
        return false;
    }


    /**
     * Método que, dado un string por parámetro, comprueba si la secuencia de transiciones marcadas por
     * esta cadena de caracteres lleva a un estado final del autómata.
     *
     * @param cadena String a comprobar.
     * @return True si la cadena lleva a un estado final, false si la cadena no es válida o lleva a un estado no final.
     */
    public boolean reconocer(String cadena) {
        char[] simbolo = cadena.toCharArray();
        String estado = estadoInicial;
        int i = 0;
        //System.out.println("\033[36mEstado " + estado + "\u001B[0m");
        for (; i < simbolo.length; i++) {
            System.out.println("\033[36mInstante " + (i+1) + ": Estado inicial: " + estado + ", simbolo: " + simbolo[i] +"\u001B[0m");
            estado = transicion(estado, simbolo[i]);
            if(estado.equals(" ")){
                return false;
            }
            System.out.println("\033[36m\tEstado final: " + estado + "\u001B[0m");
        }

        return esFinal(estado);
    }

    /**
     * Método que funciona igual que reconocer, pero permite realizar la comprobación paso por paso,
     * pulsando una tecla para continuar tras cada transición.
     * @param cadena String a comprobar.
     * @return True si la cadena lleva a un estado final, false si la cadena no es válida o lleva a un estado no final.
     */
    public boolean reconocerPaso(String cadena) {
        char[] simbolo = cadena.toCharArray();
        String estado = estadoInicial;
        Scanner sc = new Scanner(System.in);
        int i = 0;
        System.out.println("\033[36mEstado " + estado + "\u001B[0m");
        for (; i < simbolo.length; i++) {
            System.out.print("\033[36mInstante " + (i+1) + ": Estado inicial: " + estado + ", simbolo: " + simbolo[i] +" \u001B[0m");
            System.out.print("Pulsa intro para continuar");
            sc.nextLine();
            estado = transicion(estado, simbolo[i]);
            if(estado.equals(" ")){
                return false;
            }
            System.out.println("\033[36m\tEstado final: " + estado + "\u001B[0m");
        }
        return esFinal(estado);
    }

    /**
     * Método que pide por teclado todos los datos necesarios (estado inicial, estados finales y transiciones)
     * para construir un autómata finito determinista funcional.
     *
     * @return Un AFD con los datos introducidos.
     */
    public static AutomataFinitoDeterminista pedir() {

        AutomataFinitoDeterminista auto;
        String estadoInicial;
        String[] estadosFinales;
        List<TransicionAFD> transiciones;


        Scanner sc = new Scanner(System.in);
        System.out.print("Dime el estado inicial: ");
        estadoInicial = sc.next();
        System.out.print("\nDime el numero de estados finales: ");
        int nfinales = sc.nextInt();
        estadosFinales = new String[nfinales];
        for (int i = 0; i < nfinales; i++) {
            System.out.print("\nDime el estado final " + (i + 1) + ": ");
            estadosFinales[i] = sc.next();
        }
        auto = new AutomataFinitoDeterminista(estadoInicial, estadosFinales);

        System.out.print("\nDime el numero de transiciones: ");
        int ntransiciones = sc.nextInt();
        for (int i = 0; i < ntransiciones; i++) {
            System.out.print("\nDime el estado origen: ");
            String e1 = sc.next();
            System.out.print("\nDime el simbolo: ");
            char sim = sc.next().toCharArray()[0];
            System.out.print("\nDime el estado destino: ");
            String e2 = sc.next();

            auto.agregarTransicion(e1, sim, e2);
        }


        return auto;
    }

    /**
     * Método que permite cargar un autómata finito determinista a través de la información de un fichero de texto.
     * @param filePath String con la ruta al fichero de texto con la descripción del AFD.
     * @throws Exception Excepción que se lanza si ha ocurrido algún error en la lectura del fichero.
     */
    @Override
    public void load(String filePath) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));

            String linea;
            StringTokenizer st = null;
            br.readLine();
            br.readLine();
            linea = br.readLine();
            st = new StringTokenizer(linea);
            st.nextToken();
            estadoInicial = st.nextToken();
            linea = br.readLine();
            st = new StringTokenizer(linea);

            st.nextToken();
            int j = 0;
            estadosFinales = new String[st.countTokens()];

            for (int i = st.countTokens(); i > 0; i--) {
                estadosFinales[j] = st.nextToken();
                j++;
            }
            br.readLine();
            linea = br.readLine();

            while (!linea.equals("FIN")) {
                st = new StringTokenizer(linea);
                agregarTransicion(st.nextToken(), st.nextToken().toCharArray()[1], st.nextToken());
                linea = br.readLine();
            }


        } catch (
                Exception ex) {
            System.out.println("Error en lectura: " + ex);
        }
    }
}