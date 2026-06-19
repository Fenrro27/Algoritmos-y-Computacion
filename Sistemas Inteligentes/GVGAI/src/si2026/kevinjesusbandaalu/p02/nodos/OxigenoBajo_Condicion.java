package si2026.kevinjesusbandaalu.p02.nodos;

import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class OxigenoBajo_Condicion implements ICondicion {
    @Override
    public boolean seCumple(IMundo m) {
        Mundo83 mundo = (Mundo83) m;
        boolean seCumple = mundo.oxigeno < mundo.maxOxigeno*0.25;
      //  if(seCumple) System.out.println("Oxigeno bajo");
        // El oxígeno suele ser el recurso con itype 0 o 1 en Seaquest
        return seCumple;
        }
    
    @Override
    public String toString() {
        return "Oxigeno Bajo";
    }

}