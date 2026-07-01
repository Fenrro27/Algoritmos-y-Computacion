/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AMC_Practica1;

/**
 *
 * @author fenrr
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class ArchivosTSP {

    //private 
    private static boolean casoPeor = false;

    /**
     * Genera los puntos aleatorios con el tamaño pasado por parametro
     *
     */
    public ArrayList<Puntos> puntosAleatorios(int tama) { //bufferwritter

        //datos cargados
        String nombre;//NAME
        String tipo;//TYPE
        String comentario;//COMMENT
        ArrayList<Puntos> arrayPuntos = new ArrayList<>();
        double xRandom, yRandom, auxX, auxY;

        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());

        tipo = "TSP";

        if (!casoPeor) {
            for (int i = 0; i < tama; i++) {
                //numeros aleatorios
                do {
                    xRandom = new BigDecimal(rnd.nextFloat(1000)).setScale(10,RoundingMode.HALF_EVEN).doubleValue();
                    
                    yRandom = new BigDecimal(rnd.nextFloat(1000)).setScale(10,RoundingMode.HALF_EVEN).doubleValue();
                } while (estaEnArrayXY(arrayPuntos, xRandom, yRandom));

                arrayPuntos.add(new Puntos(i + 1, xRandom, yRandom));

            }
        } else {
            for (int i = 0; i < tama; i++) {
                //numeros aleatorios
                xRandom = 1;
                do {
                    yRandom = new BigDecimal(rnd.nextFloat(1000)).setScale(10,RoundingMode.HALF_EVEN).doubleValue();
                } while (estaEnArrayY(arrayPuntos, yRandom));

                arrayPuntos.add(new Puntos(i + 1, xRandom, yRandom));

            }
        }

        //escribimos el fichero de puntos aleatorios de tama
        FileWriter fichero = null;

        try {
            nombre = "dataset" + tama;
            comentario = "Archivo " + nombre;
            fichero = new FileWriter("src/FicherosTSP/" + nombre + ".tsp");
            // Escribimos linea a linea en el fichero

            fichero.write("NAME: " + nombre + "\n");
            fichero.write("TYPE: " + tipo + "\n");
            fichero.write("COMMENT: " + comentario + "\n");
            fichero.write("DIMENSION: " + tama + "\n");
            fichero.write("NODE_COORD_SECTION\n");

            for (int i = 0; i < tama; i++) {

                fichero.write(arrayPuntos.get(i).getnPunto() + " " + arrayPuntos.get(i).getX() + " " + arrayPuntos.get(i).getY() + "\n");
            }

            fichero.write("EOF\n");
            //cerramos el fichero una vez escrito
            fichero.close();
        } catch (IOException ex) {
            System.out.println("Mensaje de la excepcion: " + ex.getMessage());
        }
        return arrayPuntos;
    }

    public void crearTSP(ArrayList<Puntos> Array, String nombre) {

        String comentario;//COMMENT
        FileWriter fichero = null;

        try {
            comentario = "Archivo " + nombre;
            fichero = new FileWriter("src/FicherosTSP/" + nombre + ".tsp");
            // Escribimos linea a linea en el fichero

            fichero.write("NAME: " + nombre + "\n");
            fichero.write("TYPE: TSP\n");
            fichero.write("COMMENT: " + comentario + "\n");
            fichero.write("DIMENSION: " + Array.size() + "\n");
            fichero.write("NODE_COORD_SECTION\n");

            for (Puntos p : Array) {
                fichero.write(p.getnPunto() + " " + p.getX() + " " + p.getY() + "\n");
            }
            fichero.write("EOF\n");
            //cerramos el fichero una vez escrito
            fichero.close();
        } catch (IOException ex) {
            System.out.println("Mensaje de la excepcion: " + ex.getMessage());
        }
    }

    public ArrayList<Puntos> datosFichero(String nombre) throws IOException {

       ArrayList<Puntos> array = new ArrayList<>();//NODE_COORD_SECTION
        BufferedReader br = new BufferedReader(new FileReader("src/FicherosTSP/" + nombre));
        boolean SeccionNodos = false;
        String linea;
        int dimension = 0;

        int i = 0;

        while ((linea = br.readLine()) != null) {
            if (linea.contains("DIMENSION")) {
                String[] tokens = linea.split(":");
                String dimensionValue = tokens[1].trim();
                dimension = Integer.parseInt(dimensionValue);
                break;
            }
        }
        while ((linea = br.readLine()) != null) {
            if (linea.contains("NODE_COORD_SECTION")) {
                SeccionNodos = true;
                break;
            }
        }
        while ((linea = br.readLine()) != null) {
            if (SeccionNodos && !linea.trim().isEmpty() && !linea.contains("EOF")) {
                String[] tokens = linea.trim().split("\\s+");
                int nPunto = Integer.parseInt(tokens[0]);
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                array.add(new Puntos(nPunto, x, y));
                i++;
            }
        }
        return array;
    }

    /*public int getDimensionArray() {
        return dimensionArray;
    }

    public ArrayList<Puntos> getArray() {
        return arrayPuntos;
    }

    public Puntos getPunto(int pos) {
        return arrayPuntos.get(pos);
    }*/
    public void verArray(ArrayList<Puntos> aux) {
        for (int i = 0; i < aux.size(); i++) {
            aux.get(i).verPunto();
        }

    }

    public boolean getCasoPeor() {
        return casoPeor;
    }

    public void setCasoPeor() {
        casoPeor = !casoPeor;
    }

    //Funciones para ver si una coordenada esta dentro del punto
    private boolean estaEnArrayY(ArrayList<Puntos> arrayPuntos, double auxY) {

        for (int i = 0; i < arrayPuntos.size(); i++) {
            if (arrayPuntos.get(i).getY() == auxY) {
                return true;
            }
        }
        return false;
    }

     private boolean estaEnArrayXY(ArrayList<Puntos> arrayPuntos, double auxX, double auxY) {

        for (int i = 0; i < arrayPuntos.size(); i++) {
            if (arrayPuntos.get(i).getX() == auxX && arrayPuntos.get(i).getY() == auxY) {
                return true;
            }
        }
        return false;
    }
}
