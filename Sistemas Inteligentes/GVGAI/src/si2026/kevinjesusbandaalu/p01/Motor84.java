package si2026.kevinjesusbandaalu.p01;

import java.util.ArrayList;
import java.util.List;

import si2026.kevinjesusbandaalu.common.*;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.common.MotorReglas.Regla;
import si2026.kevinjesusbandaalu.p01.reglas.*;

public class Motor84 extends Motor {
	
	public Motor84() {
		reglas = new ArrayList<>();
		
		// Regla Equivar
		List<ICondicion> list_esqCond = new ArrayList<ICondicion>();
		list_esqCond.add( new Esquivar_Condicion());
		reglas.add(new Regla(list_esqCond,(IAccion) new Esquivar_Accion()));
	
		// Regla Orientacion
		List<ICondicion> list_orientCond = new ArrayList<ICondicion>();
		list_orientCond.add( new Orientar_Condicion());
		reglas.add(new Regla(list_orientCond,(IAccion) new Orientar_Accion()));
		
		// Regla Default
		List<ICondicion> list_dispCond = new ArrayList<ICondicion>();
		list_dispCond.add( new Disparar_Condicion());
		reglas.add(new Regla(list_dispCond,(IAccion) new Disparar_Accion()));
	
		
		// Regla centrar
		List<ICondicion> list_centCond = new ArrayList<ICondicion>();
		list_centCond.add( new Centrar_Condicion());
		reglas.add(new Regla(list_centCond,(IAccion) new Centrar_Accion()));
		
		
	}

}
