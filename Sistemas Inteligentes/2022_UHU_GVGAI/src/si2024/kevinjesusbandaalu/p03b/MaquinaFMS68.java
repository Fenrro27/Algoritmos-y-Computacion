package si2024.kevinjesusbandaalu.p03b;

import java.util.HashMap;

import si2024.kevinjesusbandaalu.common.maquinaFinita.Estado;
import si2024.kevinjesusbandaalu.common.maquinaFinita.MaquinaFSM;
import si2024.kevinjesusbandaalu.p03b.estados.EstadoCazar;
import si2024.kevinjesusbandaalu.p03b.estados.EstadoComer;

public class MaquinaFMS68 extends MaquinaFSM{

	private HashMap<String, Estado> estados;
	
	public MaquinaFMS68(){
		
		estados = new HashMap<String, Estado>();
		
		EstadoComer comer = new EstadoComer();
		EstadoCazar cazar = new EstadoCazar();
		estados.put("Cazar", cazar);
		estados.put("Comer", comer);
		comer.addTransiciones(estados);
		cazar.addTransiciones(estados);
		
		
		this.estadoInicial = comer; //Iniciamos el juego comiendo 
		
		this.estadoActual = this.estadoInicial;
	}
	
}
