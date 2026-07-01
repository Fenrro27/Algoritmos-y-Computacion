/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AMC_Practica1;

import static java.lang.Math.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author kjba2
 */
public class DistanciaPuntos {

    private Puntos Punto1;
    private Puntos Punto2;
    private double distancia;
    private int calculadas=0;//calculos hechos antes de encontrar la distancia minima
    private float tiempo;//tiempo que tarda en encontrar la distancia minima

    
    public double DistanciaEntrePuntos(Puntos p1, Puntos p2) {
        double longX, longY;

        longX = (double) (p1.getX() - p2.getX());
        longY = (double) (p1.getY() - p2.getY());

        
        return sqrt(longX * longX + longY * longY);

    }

    public void insertarDatos(Puntos Punto1, Puntos Punto2, double distancia, int calculadas, float tiempo) {
        this.Punto1 = Punto1;
        this.Punto2 = Punto2;
        this.distancia = distancia;
        this.calculadas = calculadas;
        this.tiempo = tiempo;
    }
    public void insertarDatos(Puntos Punto1, Puntos Punto2, double distancia) {
        this.Punto1 = Punto1;
        this.Punto2 = Punto2;
        this.distancia = distancia;
    }
    public void insertarDatos(Puntos Punto1, Puntos Punto2) {
        this.Punto1 = Punto1;
        this.Punto2 = Punto2;
        this.distancia = DistanciaEntrePuntos(Punto1, Punto2);
    }
    public void aumentarCalculadas(){
        calculadas++;
    }

    public Puntos getPunto1() {
        return Punto1;
    }

    public void setPunto1(Puntos Punto1) {
        this.Punto1 = Punto1;
    }

    public Puntos getPunto2() {
        return Punto2;
    }

    public void setPunto2(Puntos Punto2) {
        this.Punto2 = Punto2;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public int getCalculadas() {
        return calculadas;
    }

    public void setCalculadas(int calculadas) {
        this.calculadas = calculadas;
    }

    public float getTiempo() {
        return tiempo;
    }

    public void setTiempo(float tiempo) {
        this.tiempo = tiempo;
    }
    public void insertarTiempo(float tiempo){
        this.tiempo=tiempo;
    }
    
    
    
}
