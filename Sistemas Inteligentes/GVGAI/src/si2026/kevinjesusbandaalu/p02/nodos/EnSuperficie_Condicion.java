package si2026.kevinjesusbandaalu.p02.nodos;

import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class EnSuperficie_Condicion implements ICondicion {

    @Override
    public boolean seCumple(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        
        boolean estaArriba = (mundo.MiPosicion.y <= 0); 
        boolean oxigenoIncompleto = (mundo.oxigeno < mundo.maxOxigeno*0.9); 

        return estaArriba && oxigenoIncompleto;
    }

    @Override
    public String toString() {
        return "Recargando Oxígeno";
    }
}