package si2024.kevinjesusbandaalu.p03;

import si2024.kevinjesusbandaalu.common.arbolDecision.ArbolDecision;
import si2024.kevinjesusbandaalu.common.arbolDecision.NodoAccion;
import si2024.kevinjesusbandaalu.common.arbolDecision.NodoDecision;
import si2024.kevinjesusbandaalu.p03.reglas.AtacarMonja_Condicion;
import si2024.kevinjesusbandaalu.p03.reglas.Atacar_Accion;
import si2024.kevinjesusbandaalu.p03.reglas.EstoyInfectado_Condicion;
import si2024.kevinjesusbandaalu.p03.reglas.InfectarA_Accion;
import si2024.kevinjesusbandaalu.p03.reglas.Infectarse_Accion;

public class Motor53 extends ArbolDecision {

	public Motor53() {
		// creamos objetos de reglas - Arbol(condicion, si, no)

		// creamos las acciones
		NodoAccion nodoAtacar = new NodoAccion(new Atacar_Accion());
		NodoAccion nodoInfectarA = new NodoAccion(new InfectarA_Accion());
		NodoAccion nodoInfectarse = new NodoAccion(new Infectarse_Accion());

		// Primera Aproximacion

		// NodoDecision nd = new NodoDecision(new AtacarMonja_Condicion(), nodoAtacar, nodoInfectarA);
		// this.raiz = new NodoDecision(new EstoyInfectado_Condicion(), nd, nodoInfectarse);

		// Segunda Aproximacion, La Elegida

		 NodoDecision nd = new NodoDecision(new EstoyInfectado_Condicion(), nodoInfectarA, nodoInfectarse);
		 this.raiz = new NodoDecision(new AtacarMonja_Condicion(), nodoAtacar, nd);

	}

}
