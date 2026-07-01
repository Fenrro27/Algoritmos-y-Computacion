/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AMC_Practica1;

import java.util.ArrayList;

/**
 *
 * @author fenrr
 */
public class Algoritmos {

    //  private ArchivosTSP array;
    /**
     * Cargamos un array de puntos para poder pasarle el algoritmo de busqueda
     *
     * @param array
     * @return
     */
    // public Algoritmos(ArchivosTSP array) {
    //     this.array = array; //pasamos el archivo tsp
    // }
    public DistanciaPuntos BusquedaExhaustiva(ArrayList<Puntos> array) {
        Puntos p1, p2;
        double dMin, dActual;
        DistanciaPuntos dP = new DistanciaPuntos();
        ArchivosTSP aTSP = new ArchivosTSP();
        ArrayList<Puntos> punArr = (ArrayList<Puntos>) array.clone();
        dMin = Float.MAX_VALUE;

        for (int i = 0; i < punArr.size(); i++) {
            p1 = punArr.get(i);//obtenemos el punto con el que queremos comparar
            for (int j = i + 1; j < punArr.size(); j++) {

                dP.aumentarCalculadas();

                p2 = punArr.get(j);
                dActual = dP.DistanciaEntrePuntos(p1, p2);
                if (dMin > dActual) {
                    dMin = dActual;
                    dP.insertarDatos(p1, p2, dActual);//nos quedamos con el valor del punto actual
                }
            }

        }
        aTSP.crearTSP(punArr, "Exhaustivo");
        return dP;
    }

    public DistanciaPuntos BusquedaPoda(ArrayList<Puntos> array, boolean fichero) {
        DistanciaPuntos dP = new DistanciaPuntos();

        Puntos p1, p2;
        double dMin, dActual;
        dMin = Double.MAX_VALUE;
        ArrayList<Puntos> punArr = (ArrayList<Puntos>) array.clone();
        ArchivosTSP aTSP = new ArchivosTSP();

        quickSortX(punArr, 0, punArr.size() - 1);

        for (int i = 0; i < punArr.size(); i++) {

            p1 = punArr.get(i);//obtenemos el punto con el que queremos comparar
            int j = i + 1;
            while (j < punArr.size()) {

                p2 = punArr.get(j);//obtenemos el otro punto
                if (dMin < (p2.getX() - p1.getX())) {

                    break;
                }
                dP.aumentarCalculadas();
                dActual = dP.DistanciaEntrePuntos(p1, p2);
                if (dMin > dActual) {
                    dMin = dActual;
                    dP.insertarDatos(p1, p2, dActual);//nos quedamos con el valor del punto actual
                }
                j++;
            }
        }

        if (fichero) {
            aTSP.crearTSP(punArr, "ExhaustivoPoda");
        }
        return dP;
    }

    public DistanciaPuntos BusquedaDYB(ArrayList<Puntos> array) {
        DistanciaPuntos dP, dPI, dPD;
        float dIzq, dDer;
        ArrayList<Puntos> punArr = (ArrayList<Puntos>) array.clone();
        int partido;
        double dMin, dActual = Double.MAX_VALUE;
        Puntos p1, p2;
        ArchivosTSP aTSP = new ArchivosTSP();

        ArrayList<Puntos> pIzq = new ArrayList<>();
        ArrayList<Puntos> pDer = new ArrayList<>();

        quickSortX(punArr, 0, punArr.size() - 1);

        //dividimos el array a la mitad
        partido = punArr.size() / 2;

        //partimos el array en dos
        for (int i = 0; i < punArr.size(); i++) {

            if (i < partido) {
                pIzq.add(punArr.get(i));
            } else {
                pDer.add(punArr.get(i));
            }
        }
        dPI = BusquedaPoda(pIzq, false);
        dPD = BusquedaPoda(pDer, false);

        //comprobamos la distancia menor de ambas
        if (dPI.getDistancia() < dPD.getDistancia()) {
            dP = dPI;
        } else {
            dP = dPD;
        }

        dP.setCalculadas(dPD.getCalculadas() + dPI.getCalculadas());
        dMin = dP.getDistancia();

        int inicio;
        if (partido - dP.getDistancia() > 0) {
            inicio = (int) (partido - dP.getDistancia());
        } else {
            inicio = 0;
        }
        int fin;
        if (punArr.size() <= partido + dP.getDistancia()) {
            fin = (int) (partido + dP.getDistancia());
        } else {
            fin = punArr.size();
        }
        // System.out.println("Partido: " + partido);
        //System.out.println("Distancia: " + dP.getDistancia());
        //vemos si en la franja partida hay un punto con menor separacion

        for (int i = inicio; i < fin; i++) {
            p1 = punArr.get(i);
            for (int j = i + 1; j < fin; j++) {

                p2 = punArr.get(j);//obtenemos el otro punto

                dP.aumentarCalculadas();
                dActual = dP.DistanciaEntrePuntos(p1, p2);
                if (dMin > dActual) {
                    dMin = dActual;
                    dP.insertarDatos(p1, p2, dActual);//nos quedamos con el valor del punto actual
                }

            }
        }

        aTSP.crearTSP(punArr, "DivideVenceras");
        return dP;
    }

    public DistanciaPuntos BusquedaDYBMejor(ArrayList<Puntos> array) {
        DistanciaPuntos dP, dPI, dPD;
        float dIzq, dDer;
        ArrayList<Puntos> punArr = (ArrayList<Puntos>) array.clone();
        int partido;
        double dMin, dActual;
        Puntos p1, p2;
        ArchivosTSP aTSP = new ArchivosTSP();

        ArrayList<Puntos> pIzq = new ArrayList<>();
        ArrayList<Puntos> pDer = new ArrayList<>();

        quickSortX(punArr, 0, punArr.size() - 1);
        //dividimos el array a la mitad
        partido = punArr.size() / 2;

        //partimos el array en dos
        for (int i = 0; i < punArr.size(); i++) {

            if (i < partido) {
                pIzq.add(punArr.get(i));
            } else {
                pDer.add(punArr.get(i));
            }
        }
        dPI = BusquedaPoda((ArrayList<Puntos>) pIzq.clone(), false);
        dPD = BusquedaPoda((ArrayList<Puntos>) pDer.clone(), false);

        //comprobamos la distancia menor de ambas
        if (dPI.getDistancia() < dPD.getDistancia()) {
            dP = dPI;
        } else {
            dP = dPD;
        }

        dP.setCalculadas(dPD.getCalculadas() + dPI.getCalculadas());
        // System.out.println("Calculadas: " + dP.getCalculadas());


        dMin = dP.getDistancia();

        int inicio;
        if (partido - dP.getDistancia() > 0) {
            inicio = (int) (partido - dP.getDistancia());
        } else {
            inicio = 0;
        }
        int fin;
        if (punArr.size() <= partido + dP.getDistancia()) {
            fin = (int) (partido + dP.getDistancia());
        } else {
            fin = punArr.size();
        }
        quickSortY(punArr, inicio, (int) (partido + dMin));
        //vemos si en la franja partida hay un punto con menor separacion
        int k;

        for (int i = inicio; i < fin ; i++) {
            p1 = punArr.get(i);
            k=0;
            for (int j = i + 1; j < fin && k < 12; j++) {

                p2 = punArr.get(j);//obtenemos el otro punto

                dP.aumentarCalculadas();
                dActual = dP.DistanciaEntrePuntos(p1, p2);
                if (dMin > dActual) {
                    dMin = dActual;
                    dP.insertarDatos(p1, p2, dActual);//nos quedamos con el valor del punto actual
                }
                k++;
            }
        }

        aTSP.crearTSP(punArr, "DyVMejorado");
        return dP;
    }

    //quicksortAlgorithm  
    private static void quickSortX(ArrayList<Puntos> puntos, int izquierda, int derecha) {

        int i = izquierda;
        int j = derecha;
        Puntos pivote = puntos.get(izquierda + (derecha - izquierda) / 2);

        while (i <= j) {
            while (puntos.get(i).XcompareTo(pivote) < 0) {
                i++;
            }
            while (puntos.get(j).XcompareTo(pivote) > 0) {
                j--;
            }
            if (i <= j) {
                swap(puntos, i, j);
                i++;
                j--;
            }
        }

        if (izquierda < j) {
            quickSortX(puntos, izquierda, j);
        }
        if (i < derecha) {
            quickSortX(puntos, i, derecha);
        }
    }

    private static void quickSortY(ArrayList<Puntos> puntos, int izquierda, int derecha) {

        int i = izquierda;
        int j = derecha;
        Puntos pivote = puntos.get(izquierda + (derecha - izquierda) / 2);

        while (i <= j) {
            while (puntos.get(i).YcompareTo(pivote) < 0) {
                i++;
            }
            while (puntos.get(j).YcompareTo(pivote) > 0) {
                j--;
            }
            if (i <= j) {
                swap(puntos, i, j);
                i++;
                j--;
            }
        }

        if (izquierda < j) {
            quickSortY(puntos, izquierda, j);
        }
        if (i < derecha) {
            quickSortY(puntos, i, derecha);
        }
    }

    //-----------------------------------
    public static void swap(ArrayList<Puntos> points, int i, int j) {
        Puntos temp = points.get(i);
        points.set(i, points.get(j));
        points.set(j, temp);
    }

}
