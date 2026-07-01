package es.uhu.amc.amcp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Clase creada para registrar las transiciones de AFND
 *
 */
class TransicionAFND {
    private String origen;
    private char simbolo;
    private String[] destino;

    public TransicionAFND(String origen, char simbolo, String[] destino) {
        this.origen = origen;
        this.simbolo = simbolo;
        this.destino = destino.clone();
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(char simbolo) {
        this.simbolo = simbolo;
    }

    public String[] getDestino() {
        return destino;
    }

    public void setDestino(String[] destino) {
        this.destino = destino;
    }
}

/**
 * Clase creada para registrar las transiciones Lambda, es decir, las q son imediatas
 *
 */
class TransicionLambda {
    private String origen;
    private String[] destino;

    public TransicionLambda(String origen, String[] destino) {
        this.origen = origen;
        this.destino = destino.clone();
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String[] getDestino() {
        return destino;
    }

    public void setDestino(String[] destino) {
        this.destino = destino;
    }
}


public class AutomataFinitoNoDeterminista implements IAutomataFinitoNoDeterminista {

    private String estadoInicial;//estado en el que empieza el automata
    private String[] estadosFinales;//estados en los que acaba el automata
    private List<TransicionAFND> transiciones;//transiciones que necesitas de un simbolo para ejecutarse
    private List<TransicionLambda> transicionLambda;//transiciones imediatas

    /**
     * Constructor por defecto del AFND
     */
    public AutomataFinitoNoDeterminista() {
            transiciones = new ArrayList<>();
            transicionLambda = new ArrayList<>();
    }
    /**
     * Constructor del AFND al que se le pasa el estado inicial y los estados Finales
     */
    public AutomataFinitoNoDeterminista(String estadoInicial, String[] estadosFinales) {
        this.estadoInicial = estadoInicial;
        this.estadosFinales = estadosFinales.clone();
        transiciones = new ArrayList<>();
        transicionLambda = new ArrayList<>();
    }

    /**
     * @return Devuelve el estado inicial
     */
    public String getEstadoInicial() {
        return estadoInicial;
    }

    /**
     * Asigna un estado inicial
     * @param estadoInicial Estado que se asigna
     */
    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    /**
     *
     * @return Array de Strings con los estados Finales
     */
    public String[] getEstadosFinales() {
        return estadosFinales;
    }

    /**
     * Inserta los posibles estados finales en el automata
     * @param estadosFinales Array de los estados finales del automata
     */
    public void setEstadosFinales(String[] estadosFinales) {
        this.estadosFinales = estadosFinales;
    }

    /**
     *
     * @return Devuelve una Lista con las transiciones AFND
     */
    public List<TransicionAFND> getTransiciones() {
        return transiciones;
    }

    /**
     * Añade transiciones al automata
     * @param transiciones Transiciones a añadir en el automata
     */
    public void setTransiciones(List<TransicionAFND> transiciones) {
        this.transiciones = transiciones;
    }

    /**
     *
     * @return Devuelve una Lista con las transiciones Lambda
     */
    public List<TransicionLambda> getTransicionLambda() {
        return transicionLambda;
    }

    /**
     * Añade una lista de transiciones al automata
     * @param transicionLambda transiciones que se añaden al automata
     */
    public void setTransicionLambda(List<TransicionLambda> transicionLambda) {
        this.transicionLambda = transicionLambda;
    }

    /**
     * Carga un AFND desde Fichero
     * @param filePath String con la ruta al fichero de texto con la
     *                 descripción del AFND.
     * @throws Exception Lanza excepciones de fallos
     */
    @Override
    public void load(String filePath) throws Exception {
        try {
            int j;
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
            j = 0;
            estadosFinales = new String[st.countTokens()];

            for (int i = st.countTokens(); i > 0; i--) {
                estadosFinales[j] = st.nextToken();
                j++;
            }
            br.readLine();
            linea = br.readLine();

            while (!linea.equals("TRANSICIONES LAMBDA:")) {
                st = new StringTokenizer(linea);
                String a = st.nextToken();
                char b = st.nextToken().toCharArray()[1];
                String[] c = new String[st.countTokens()];

                j = 0;
                for (int i = st.countTokens(); i > 0; i--) {
                    c[j] = st.nextToken();
                    j++;
                }

                agregarTransicion(a, b, c);
                linea = br.readLine();
            }

            //Lectura de lambda
            linea = br.readLine();
            while (!linea.equals("FIN")) {
                st = new StringTokenizer(linea);
                String a = st.nextToken();
                String[] c = new String[st.countTokens()];

                j = 0;
                for (int i = st.countTokens(); i > 0; i--) {
                    c[j] = st.nextToken();
                    j++;
                }

                agregarTransicionLambda(a, c);
                linea = br.readLine();
            }


        } catch (
                Exception ex) {
            System.out.println("\033[31mError en lectura: " + ex + "\u001B[0m");
        }
    }

    /**
     * Añade una transicion Lambda
     * @param a Nodo origen
     * @param c Nodos destino
     */
    private void agregarTransicionLambda(String a, String[] c) {
        transicionLambda.add(new TransicionLambda(a, c));
    }

    /**
     * Añade una Transicion
     * @param a Nodo Origen
     * @param b Simbolo de salto
     * @param c Nodo Destino
     */
    private void agregarTransicion(String a, char b, String[] c) {
        transiciones.add(new TransicionAFND(a, b, c));
    }

    /**
     * Comprueba si una cadena es un estado final
     * @param estado código del estado que se quiere comprobar.
     * @return devuelve true si la cadena es un estado final
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
     * Comprueba si alguno de los macroestados es un estado final
     * @param macroestado Lista de macroestados que se van a comprobar
     * @return devuelve true si algun macroestado es un estado final
     */
    public boolean esFinal(String[] macroestado) {
        int i = 0;
        while (i < estadosFinales.length) {
            int j = 0;
            while (j < macroestado.length) {

                if (macroestado[j].equals(estadosFinales[i])) {
                    return true;
                }

                j++;
            }


            i++;
        }
        return false;
    }

    /**
     *
     * @param cadena String con la cadena de símbolos a
     *               comprobar.
     * @return Devuelve True si La secuencia traza un camino hasta un estado final
     */
    @Override
    public boolean reconocer(String cadena) {
        char[] simbolo = cadena.toCharArray();
        String[] estado = new String[1];
        estado[0] = estadoInicial;
        String[] macroestado = lambda_clausura(estado);
        int i = 0;
        for (;i < simbolo.length; i++) {
            //System.out.println("Simbolo: "+simbolo[i]);
            System.out.println("\033[36mInstante " + (i+1) + ": \u001B[0m");
            for(int j =0; j< estado.length;j++){
                System.out.println("\033[36m\tEstados Iniciales: " + estado[j] +", Simbolo: " + simbolo[i] +"\u001B[0m");
            }
            System.out.println("\033[36m\t---------------------------\u001B[0m");


            estado=macroestado;
            macroestado = transicion(macroestado, simbolo[i]);
            if(macroestado.length == 0){
                return false;
            }
            for (int j = 0; j< macroestado.length; j++){
                // System.out.println("\033[36mEstado " + (j+1) + ": " + macroestado[j] +"\u001B[0m");
                System.out.println("\033[36m\tEstado Final:"+ macroestado[j]+" \u001B[0m");

            }

        }

        return esFinal(macroestado);
    }

    /**
     *
     * @param cadena String con la cadena de símbolos a comprobar.
     * @return Devuelve True si La secuencia traza un camino hasta un estado final
     */
    public boolean reconocerPaso(String cadena) {
        char[] simbolo = cadena.toCharArray();
        String[] estado = new String[1];
        estado[0] = estadoInicial;
        String[] macroestado = lambda_clausura(estado);
        Scanner sc = new Scanner(System.in);
        int i = 0;
        for (;i < simbolo.length; i++) {

            System.out.print("Pulsa intro para continuar");
            sc.nextLine();

            System.out.println("\033[36mInstante " + (i+1) + ": \u001B[0m");
            for(int j =0; j< estado.length;j++){
                System.out.println("\033[36m\tEstados Iniciales: " + estado[j] +", Simbolo: " + simbolo[i] +"\u001B[0m");
            }
            System.out.println("\033[36m\t---------------------------\u001B[0m");

            estado=macroestado;
            macroestado = transicion(macroestado, simbolo[i]);
            if(macroestado.length == 0){
                return false;
            }
            for (int j = 0; j< macroestado.length; j++){
                // System.out.println("\033[36mEstado " + (j+1) + ": " + macroestado[j] +"\u001B[0m");
                System.out.println("\033[36m\tEstado Final:"+ macroestado[j]+" \u001B[0m");

            }
        }

        return esFinal(macroestado);
    }

    /**
     *
     * @param estado lista de estados origen que se va a comprobar
     * @return Devuelve una lista original más los estados destino de transiciones lambda
     */
    private String[] lambda_clausura(String[] estado) {

        ArrayList<String> resultado= new ArrayList<>();
        for (int i = 0; i< estado.length; i++){
            resultado.add(estado[i]);
        }

        for (int i = 0; i < estado.length; i++) {
            for (int j = 0; j < transicionLambda.size(); j++) {
                if (transicionLambda.get(j).getOrigen().equals(estado[i])) {
                    for (int k = 0; k < transicionLambda.get(j).getDestino().length; k++) {
                        resultado.add(transicionLambda.get(j).getDestino()[k]);
                    }
                }
            }
        }
        String[] a = new String[resultado.size()];

        return resultado.toArray(a);
    }

    /**
     * Funcion que sirve para devolver la lista de posibles destinos de un determinado macroestado al darle un simbolo en concreto
     *
     * @param macroestado Lista de macroestados que vamos a comprobar
     * @param c Simbolo de la transicion
     * @return estado destino que tienen de origen a los macroestados pasados y que ademas tengan el simbolo pasado
     */
    private String[] transicion(String[] macroestado, char c) {
        ArrayList<String> resultado = new ArrayList<>();

        String[] reslambda =lambda_clausura(macroestado);

        for (int i = 0; i < reslambda.length; i++) {



            for (int j = 0; j < transiciones.size(); j++) {

                if (transiciones.get(j).getOrigen().equals(reslambda[i]) && transiciones.get(j).getSimbolo() == c) {
                    for (int k = 0; k < transiciones.get(j).getDestino().length; k++) {
                        if(!resultado.contains(transiciones.get(j).getDestino()[k])){
                            resultado.add(transiciones.get(j).getDestino()[k]);
                        }
                    }
                }
            }


        }


        String[] a = new String[resultado.size()];
        String[] res = lambda_clausura(resultado.toArray(a));
        return res;
    }

    /**
     * Metodo que pide los datos de un AFND
     * @return Devuelve un AFND con los datos ingresados
     */
    public static AutomataFinitoNoDeterminista pedir() {

        AutomataFinitoNoDeterminista auto;
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
        auto = new AutomataFinitoNoDeterminista(estadoInicial, estadosFinales);

        System.out.print("\nDime el numero de transiciones: ");
        int ntransiciones = sc.nextInt();
        for (int i = 0; i < ntransiciones; i++) {
            System.out.print("\nDime el estado origen: ");
            String e1 = sc.next();
            System.out.print("\nDime el simbolo: ");
            char sim = sc.next().toCharArray()[0];

            System.out.print("\nDime el numero de estados destino: ");
            int nDestino = sc.nextInt();
            String[] e2 = new String[nDestino];
            for (int j = 0; j < nDestino; j++) {
                System.out.print("\nDime el estado destino["+j+"]: ");
                e2[j] = sc.next();
            }

            auto.agregarTransicion(e1, sim, e2);
        }

        //transiciones Lambda
        System.out.print("\nDime el numero de transicionesLambda: ");
        ntransiciones = sc.nextInt();
        for (int i = 0; i < ntransiciones; i++) {
            System.out.print("\nDime el estado origen: ");
            String e1 = sc.next();

            System.out.print("\nDime el numero de estados destino: ");
            int nDestino = sc.nextInt();
            String[] e2 = new String[nDestino];
            for (int j = 0; j < nDestino; j++) {
                System.out.print("\nDime el estado destino["+j+"]: ");
                e2[j] = sc.next();
            }

            auto.agregarTransicionLambda(e1, e2);
        }

        return auto;
    }


}
