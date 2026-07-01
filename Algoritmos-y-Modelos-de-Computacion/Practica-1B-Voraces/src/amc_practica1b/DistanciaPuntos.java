/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc_practica1b;

import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author kjba2
 */
public class DistanciaPuntos {

    public double DistanciaEntrePuntos(Puntos p1, Puntos p2) {
        double longX, longY;

        longX = (double) (p1.getX() - p2.getX());
        longY = (double) (p1.getY() - p2.getY());

        return sqrt(longX * longX + longY * longY);

    }

    public static double DistanciaEntrePuntos(ArrayList<Puntos> array, int punto1, int punto2) {
        double longX, longY;
        Puntos p1 = null;
        Puntos p2 = null;

        for (int i = 0; i < array.size(); i++) {

            if (array.get(i).getnPunto() == punto1) {
                p1 = array.get(i);
            }
            if (array.get(i).getnPunto() == punto2) {
                p2 = array.get(i);
            }
        }

        longX = (double) (p1.getX() - p2.getX());
        longY = (double) (p1.getY() - p2.getY());

        return sqrt(longX * longX + longY * longY);

    }

}
