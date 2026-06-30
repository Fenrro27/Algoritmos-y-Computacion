package si2026.kevinjesusbandaalu.p02;

import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.arbolDecision.ArbolDecision;
import si2026.kevinjesusbandaalu.common.arbolDecision.NodoAccion;
import si2026.kevinjesusbandaalu.common.arbolDecision.NodoDecision;
import si2026.kevinjesusbandaalu.p02.nodos.*;


public class Motor83 extends ArbolDecision{
	
	public Motor83() {
		NodoAccion esperar = new NodoAccion(new Esperar_Accion());
		NodoAccion subir = new NodoAccion(new IrSuperficie_Accion());
        NodoAccion cazar = new NodoAccion(new CazarBuzo_Accion());
        
        NodoAccion patrullar = new NodoAccion(new Patrullar_Accion());
        // Nuevas acciones de defensa
        NodoAccion defenderUso = new NodoAccion(new DefenderUso_Accion());
        NodoAccion defenderHorizontal = new NodoAccion(new DefenderHorizontal_Accion());
        NodoAccion defenderVertical = new NodoAccion(new DefenderVertical_Accion());
        
        // Estructura de decisiones Disparo Dinamico
        NodoAccion encararYDispararVertical = new NodoAccion(new EncararYDispararVertical_Accion());
        NodoAccion encararYDispararHorizontal = new NodoAccion(new EncararYDispararHorizontal_Accion());
        NodoAccion disparoDefecto = new NodoAccion(new DisparoPorDefecto_Accion());
        
        NodoDecision decEnemigoHorizontal = new NodoDecision(new EnemigoAlineadoHorizontal_Condicion(), encararYDispararHorizontal, disparoDefecto);
        NodoDecision decDisparoDinamico = new NodoDecision(new EnemigoAlineadoVertical_Condicion(), encararYDispararVertical, decEnemigoHorizontal);
        
        // Cadena de decisiones base (Tareas y Recolección)
        NodoDecision decSobreSpawn = new NodoDecision(new SobreSpawn_Condicion(), decDisparoDinamico, patrullar);
        NodoDecision decBuzo = new NodoDecision(new HayBuzo_Condicion(), cazar, decSobreSpawn);
        NodoDecision decBolsa = new NodoDecision(new BolsaLlena_Condicion(), subir, decBuzo);
        
        // Decisiones de Mantenimiento (Oxígeno y Superficie)
        NodoDecision decZonaSegura = new NodoDecision(new EnZonaSegura_Condicion(), decDisparoDinamico, esperar);
        NodoDecision decRecarga = new NodoDecision(new EnSuperficie_Condicion(), decZonaSegura, decBolsa);
        NodoDecision decOxigeno = new NodoDecision(new OxigenoBajo_Condicion(), subir, decRecarga);
        
        // Cadena de decisiones de DEFENSA (MÁXIMA PRIORIDAD)
        // Evita morir ahogado pero, más importante, evita chocar al subir a por aire.
        NodoDecision decPeligroVertical = new NodoDecision(new PeligroCercano_Condicion(), defenderVertical, decOxigeno);
        NodoDecision decPeligroHorizontal = new NodoDecision(new PeligroMismaFila_Condicion(), defenderHorizontal, decPeligroVertical);
        this.raiz = new NodoDecision(new PeligroAlineado_Condicion(), defenderUso, decPeligroHorizontal);
        
        
		/**
		 * ESTRUCTURA DEL ÁRBOL DE DECISIÓN - MUNDO 83 (SEAQUEST)
		 * ======================================================
		 * 
		 * 1. DEFENSA (Máxima Prioridad)
		 *    ├─ ¿Peligro Alineado?  -> DefenderUso_Accion
		 *    ├─ ¿Peligro MismaFila? -> DefenderHorizontal_Accion
		 *    └─ ¿Peligro Cercano?   -> DefenderVertical_Accion
		 * 
		 * 2. SUPERVIVENCIA (Oxígeno)
		 *    ├─ ¿Oxigeno Bajo?      -> IrSuperficie_Accion
		 *    └─ ¿En Superficie?     -> (Gestión de Recarga)
		 *         ├─ ¿En Zona Segura? -> (Subárbol de Disparo Dinámico)
		 *         └─ (No seguro)      -> Esperar_Accion (NIL)
		 * 
		 * 3. GESTIÓN DE RECURSOS
		 *    └─ ¿Bolsa Llena?       -> IrSuperficie_Accion
		 * 
		 * 4. TAREAS PRINCIPALES
		 *    ├─ ¿Hay Buzo Visible?  -> CazarBuzo_Accion
		 *    └─ ¿Sobre Spawn?       -> (Subárbol de Disparo Dinámico)
		 *         └─ (No sobre spawn) -> Patrullar_Accion
		 * 
		 * --- SUBÁRBOL DE DISPARO DINÁMICO ---
		 * ├─ ¿Enemigo Alineado Vertical?   -> EncararYDispararVertical_Accion
		 * ├─ ¿Enemigo Alineado Horizontal? -> EncararYDispararHorizontal_Accion
		 * └─ (Ninguno)                     -> DisparoPorDefecto_Accion (ACTION_USE)
		 * 
		 * ======================================================
		 * DESCRIPCIÓN DE NODOS:
		 * 
		 * [Condiciones]
		 * - PeligroAlineado / MismaFila / Cercano: Detectan amenazas inminentes (peces/proyectiles) en proximidad o trayectoria de colisión.
		 * - OxigenoBajo / EnSuperficie: Verifican si el oxígeno es crítico o si el submarino ya está en la superficie (y=0).
		 * - EnZonaSegura: Comprueba si estamos posicionados sobre una zona segura predefinida.
		 * - BolsaLlena: Retorna verdadero si hemos rescatado el máximo de buzos posibles (6).
		 * - HayBuzo: Verifica si hay algún buzo rescatable visible en la pantalla.
		 * - SobreSpawn: Comprueba si estamos posicionados exactamente en un punto de aparición de buzos.
		 * - EnemigoAlineadoVertical / Horizontal: Detectan si hay un enemigo en nuestra misma columna o fila respectivamente.
		 * 
		 * [Acciones]
		 * - DefenderUso / Horizontal / Vertical: Ejecutan evasión o ataque rápido contra amenazas de máxima prioridad.
		 * - IrSuperficie_Accion: Mueve el submarino hacia arriba (ACTION_UP) para recargar oxígeno o dejar buzos.
		 * - Esperar_Accion: No realiza ninguna acción (ACTION_NIL), para quedarse quieto al recargar oxígeno en superficie.
		 * - CazarBuzo_Accion: Calcula la ruta y navega hacia el buzo rescatable más cercano.
		 * - Patrullar_Accion: Navega usando A* hacia los puntos de spawn de buzos para esperarlos.
		 * - EncararYDispararVertical / Horizontal: Gira el submarino hacia el enemigo detectado y dispara (o se gira si no lo está mirando).
		 * - DisparoPorDefecto_Accion: Acción de precaución que simplemente dispara el cañón.
		 */
		
	}

}
