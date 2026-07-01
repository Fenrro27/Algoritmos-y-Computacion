package si2026.kevinjesusbandaalu.p02.nodos;

import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p02.Mundo83;

public class BolsaLlena_Condicion implements ICondicion {
    
    @Override
    public boolean seCumple(IMundo m) {
        // Casteamos a Mundo83 para acceder a sus atributos específicos
        Mundo83 mundo = (Mundo83) m;
        return mundo.bolsaLlena; 
    }

    @Override
    public String toString() {
        return "Bolsa Llena";
    }
}