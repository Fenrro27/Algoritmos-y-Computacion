package si2026.kevinjesusbandaalu.p01.reglas;

import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;

public class Centrar_Condicion implements ICondicion {

    @Override
    public boolean seCumple(IMundo m) {
       return true;
    }
    
    @Override
	public String toString() {
		return "Centrar";
	}
}