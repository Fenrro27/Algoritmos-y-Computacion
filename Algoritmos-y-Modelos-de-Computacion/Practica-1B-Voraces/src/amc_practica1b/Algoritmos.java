/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc_practica1b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

/**
 *
 * @author kjba2
 */
class resultadoDistanciaCiudades{//clase creada para guardar iformacion del metodociudadMasCercana
    private int vertice;//ciudad resultado de buscar la mas cercana a otra ciudad determinada
    private double distancia;//separacion entre las ciudades

    public int getVertice() {
        return vertice;
    }

    public void setVertice(int vertice) {
        this.vertice = vertice;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
    
    
}
public class Algoritmos {

    public Resultados vorazUnidireccional(ArrayList<Puntos> array) {
        Resultados r = new Resultados();
        DefaultUndirectedWeightedGraph<Integer, DefaultEdge> g;
        r.setDimension(array.size());
        g = arrayToGraph(array);
        HashMap<Integer, Boolean> visitado = new HashMap<Integer, Boolean>();

        int vInicial = g.vertexSet().iterator().next();

        //inicializamos el diccionario con todas las casillas en false
        for (int nodo : g.vertexSet()) {
            visitado.put(nodo, false);

        }
        r.setStringCamino(vInicial);

        r = vorazUnidireccional(g, visitado, r, vInicial);
        r.setStringCamino(vInicial);

        int nodo2 = r.getProximoNodo();

        r.setCostArist(DistanciaPuntos.DistanciaEntrePuntos(array, vInicial, nodo2), nodo2, vInicial);
        //tenemos ya metido en el resultado los datos
        r.CalculoCosteTotal();
        
        
        ArchivosTSP.crearTour(r, "vorazUnidireccional");

        return r;

    }

    private Resultados vorazUnidireccional(DefaultUndirectedWeightedGraph<Integer, DefaultEdge> g, HashMap<Integer, Boolean> visitados, Resultados r, int vInicial) {

        double dMin;
        int nEntrada = -1;
        int nSalida = -1;
        dMin = Double.MAX_VALUE;
        DefaultEdge aux = null;

        DefaultEdge it = g.edgesOf(vInicial).iterator().next();

        for (DefaultEdge j : g.edgesOf(vInicial)) {

            double dAux = g.getEdgeWeight(j);
            if (dAux < dMin && !visitados.get(g.getEdgeSource(j)) && !visitados.get(g.getEdgeTarget(j))) {//añadir si el peso no es 
                dMin = dAux;
                //buscar arista mejor
                if (vInicial == g.getEdgeSource(j)) {
                    nEntrada = g.getEdgeSource(j);
                    nSalida = g.getEdgeTarget(j);
                    aux = j;
                } else {
                    nSalida = g.getEdgeSource(j);
                    nEntrada = g.getEdgeTarget(j);
                    aux = j;
                }
            }
        }

        if (nEntrada == -1) {
            return r;
        }
        r.setProximoNodo(nSalida);
        //comprobamos si el nodo al q vamos a acceder esta visitado previamente
        if (!visitados.get(nSalida)) {

            // System.out.println("Distancia: " + dMin + ", Nodo1: " + nEntrada + ", Nodo2: " + nSalida);
            r.setStringCamino(nSalida);
            r.setAristaAux(aux);
            r.setCostArist(dMin, nEntrada, nSalida);

            visitados.put(vInicial, true);

            //System.out.println("Ha ido por salida");
            return vorazUnidireccional(g, visitados, r, nSalida);
        } else {
            //System.out.println("No visita mas");
            return r;
        }

    }

    public Resultados vorazBidireccional(ArrayList<Puntos> array) {
        Resultados r = new Resultados();
        DefaultUndirectedWeightedGraph<Integer, DefaultEdge> g;
        r.setDimension(array.size());
        g = arrayToGraph(array);
        HashMap<Integer, Boolean> visitado = new HashMap<Integer, Boolean>();
        LinkedList<Integer> recorrido = new LinkedList<>();
        double dMin = Double.MAX_VALUE;

        //inicializamos el diccionario con todas las casillas en false
        for (int nodo : g.vertexSet()) {
            visitado.put(nodo, false);

        }
        int vInicio = g.vertexSet().iterator().next();
        int vFin = -1;
        visitado.put(vInicio, true);
        recorrido.add(vInicio);

        //ciudad mas cercana a vInicio
        for (DefaultEdge j : g.edgesOf(vInicio)) {
            double dAux = g.getEdgeWeight(j);
            if (dAux < dMin) {//añadir si el peso no es 
                dMin = dAux;
                vFin = g.getEdgeTarget(j);
            }
        }
        visitado.put(vFin, true);
        recorrido.add(vFin);

        // System.out.println("Vertices Iniciales: " + recorrido.toString() + "\n");
        r.setCostArist(dMin, vInicio, vFin);

        r = vorazBidireccional(g, visitado, r, recorrido);

        //calculamos el nodo del que se vuelve al nodo inicial   
        dMin = DistanciaPuntos.DistanciaEntrePuntos(array, recorrido.get(0), vInicio);
        int nodo = recorrido.get(0);

        double dMin2 = DistanciaPuntos.DistanciaEntrePuntos(array, recorrido.get(recorrido.size() - 1), vInicio);

        if (dMin2 < dMin) {
            dMin = dMin2;
            nodo = recorrido.get(recorrido.size() - 1);
            recorrido.addLast(vInicio);
        } else {
            recorrido.addFirst(vInicio);
        }

        //System.out.println("\nVertices Finales: " + recorrido.toString() + "\n");
        //completamos los datos de resultados
        r.setCostArist(dMin, nodo, vInicio);
        //Terminamos de añadir datos a r
        r.setCaminoPorLinkedList(recorrido);
        r.CalculoCosteTotal();
        
        ArchivosTSP.crearTour(r, "vorazBidireccional");
        return r;

    }

    private Resultados vorazBidireccional(DefaultUndirectedWeightedGraph<Integer, DefaultEdge> g, HashMap<Integer, Boolean> visitados, Resultados r, LinkedList<Integer> recorrido) {

        //en recorrido vamos añadiendo alante o detras en la cola
        //comprobamos si entramos en otra iteracion o salimos de la recursividad
        double dMinIzq = Double.MAX_VALUE, dMinDrc = Double.MAX_VALUE;
        
        int verticeMinIzq = -1;
        int verticeMinDrc = -1;

        resultadoDistanciaCiudades res1 = ciudadMasCercana(g, visitados, recorrido.get(0));
        resultadoDistanciaCiudades res2 = ciudadMasCercana(g, visitados, recorrido.get(recorrido.size() - 1));
        
        dMinIzq=res1.getDistancia();
        dMinDrc=res2.getDistancia();
        verticeMinIzq=res1.getVertice();
        verticeMinDrc=res2.getVertice();

        if (dMinDrc > dMinIzq) {

            visitados.put(verticeMinIzq, true);
            recorrido.addFirst(verticeMinIzq);
            r.setCostArist(dMinIzq, recorrido.get(1), verticeMinIzq);
        } else {

            visitados.put(verticeMinDrc, true);
            recorrido.addLast(verticeMinDrc);
            r.setCostArist(dMinDrc, recorrido.get(recorrido.size() - 2), verticeMinDrc);

        }

        if (isInMap(visitados)) {
            return vorazBidireccional(g, visitados, r, recorrido);
        } else {
            return r;
        }

    }

    private resultadoDistanciaCiudades ciudadMasCercana(DefaultUndirectedWeightedGraph<Integer, DefaultEdge> g, HashMap<Integer, Boolean> visitados, int ciudadOrigen) {
        int ciudad = -1;
        double dMin = Double.MAX_VALUE;
        resultadoDistanciaCiudades res=new resultadoDistanciaCiudades();

        for (DefaultEdge j : g.edgesOf(ciudadOrigen)) {
            double dAux = g.getEdgeWeight(j);
            if (dAux < dMin && !(visitados.get(g.getEdgeTarget(j)) && visitados.get(g.getEdgeSource(j)))) {//añadir si el peso no es 
                dMin = dAux;
                ciudad = (g.getEdgeTarget(j) == ciudadOrigen) ? g.getEdgeSource(j) : g.getEdgeTarget(j);
            }
        }
        
        res.setDistancia(dMin);
        res.setVertice(ciudad);
        return res;
    }

    private DefaultUndirectedWeightedGraph<Integer, DefaultEdge> arrayToGraph(ArrayList<Puntos> array) {
        DefaultUndirectedWeightedGraph<Integer, DefaultEdge> g = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
        Puntos p1, p2;
        DistanciaPuntos dP = new DistanciaPuntos();

        //añadimos los vertices
        for (int i = 0; i < array.size(); i++) {
            g.addVertex(array.get(i).getnPunto());
        }

        //Creamos las aristas y los pesos
        double distancia = 0;
        for (int i = 0; i < array.size(); i++) {
            p1 = array.get(i);
            for (int j = i + 1; j < array.size(); j++) {
                p2 = array.get(j);
                distancia = dP.DistanciaEntrePuntos(p1, p2);

                g.setEdgeWeight(g.addEdge(p1.getnPunto(), p2.getnPunto()), distancia);
            }
        }
        return g;
    }

    private boolean isInMap(HashMap<Integer, Boolean> visitados) {
        //devolvemos true si hay algun nodo por visitar, solo sirve si el hashmap es de enteros
        boolean esta = false;

        for (int i = 0; i < visitados.size() - 1; i++) {
            esta = esta || !visitados.get(i + 1);
        }

        return esta;
    }

}
