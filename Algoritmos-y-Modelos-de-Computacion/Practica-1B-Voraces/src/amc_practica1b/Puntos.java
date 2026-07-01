/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc_practica1b;

/**
 *
 * @author fenrr
 */
public class Puntos { //posibilidad de usar reccord en vez de class

    private double x;
    private double y;
    private int nPunto;

    public Puntos(int nPunto, double x, double y) {
        this.nPunto = nPunto;
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getnPunto() {
        return nPunto;
    }

    public void verPunto() {
        System.out.println("Punto: " + nPunto + ", X: " + x + ", Y: " + y);
    }

    @Override
    public String toString() {
        return nPunto + "(" + x + "," + y + ")";
    }

    //Metodos aux quicksort
    public double XcompareTo(Puntos p) {
        if (x != p.getX()) {
            return x - p.getX();
        } else {
            return y - p.getY();
        }
    }

    public double YcompareTo(Puntos p) {
        if (y != p.getY()) {
            return y - p.getY();
        } else {
            return x - p.getX();
        }
    }
}
