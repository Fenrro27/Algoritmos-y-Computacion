/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc_practica1b;

import java.util.ArrayList;
import java.util.LinkedList;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author kjba2
 */
class costeArista {

    double coste;
    int nodoEntrante;
    int nodoSaliente;

    public costeArista(double coste, int nodoEntrante, int nodoSaliente) {
        this.coste = coste;
        this.nodoEntrante = nodoEntrante;
        this.nodoSaliente = nodoSaliente;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public int getAristaEntrante() {
        return nodoEntrante;
    }

    public void setAristaEntrante(int nodoEntrante) {
        this.nodoEntrante = nodoEntrante;
    }

    public int getAristaSaliente() {
        return nodoSaliente;
    }

    public void setAristaSaliente(int nodoSaliente) {
        this.nodoSaliente = nodoSaliente;
    }

    @Override
    public String toString() {
        return coste + " - " + nodoEntrante + "," + nodoSaliente;
    }

}

public class Resultados {

    private int dimension = 0;
    private double costeTotal=0;
    private ArrayList<Integer> caminoLista= new ArrayList<>();
    private ArrayList<costeArista> costArist = new ArrayList<>();
    private DefaultEdge aristaAux=new DefaultEdge();
    private int proximoNodo;



    
    public int getProximoNodo() {
        return proximoNodo;
    }

    public void setProximoNodo(int proximoNodo) {
        this.proximoNodo = proximoNodo;
    }

    public ArrayList<Integer> getArrayCamino(){
        return this.caminoLista;
    }

    public DefaultEdge getAristaAux() {
        return aristaAux;
    }

    public void setAristaAux(DefaultEdge aristaAux) {
        this.aristaAux = aristaAux;
    }
    
    
    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getStringCamino() {
        String s="";
        for(int i: caminoLista){
            s+=i+", ";
        }
        return s;
    }

    public void setStringCamino(int s) {
        this.caminoLista.add(s);
    }
    public void setCaminoPorLinkedList(LinkedList<Integer> camino){
        for(int i=0; i<camino.size();i++){
            this.caminoLista.add(camino.get(i));
        }
       
    }

    public String getCostArist() {
        String str = "";
        for (int i = 0; i < costArist.size(); i++) {

            str += costArist.get(i) + "\n";

        }
        return str;
    }

    public double CalculoCosteTotal() {
        double m = 0;
        for (int i = 0; i < costArist.size(); i++) {
            m += costArist.get(i).getCoste();
        }
        costeTotal=m;
        return m;
    }
    public double getCosteTotal(){
        return costeTotal;
    }
    
    public void setCostArist(double coste, int nodoEntrante, int nodoSaliente) {
        this.costArist.add(new costeArista(coste, nodoEntrante, nodoSaliente));
    }

}
