package si2024.kevinjesusbandaalu.common.maquinaFinita;

import java.util.ArrayList;

import si2024.kevinjesusbandaalu.common.IAccion;

public abstract class Estado {

		protected ArrayList<transicion> transiciones;
		
		public abstract IAccion getAccion(); 
		
		public ArrayList<transicion> getTransiciones(){
			return this.transiciones;
		}
		
}
