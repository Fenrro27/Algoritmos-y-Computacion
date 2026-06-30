import java.util.*;


public interface iConstraint {

    /**
     * Devuelve la lista de variables (nodos) que se ven afectadas por esta regla.
     * Es vital para que AC3 sepa a qué vecinos vigilar cuando una celda cambia.
     */
    List<Node> getNodes();
    
    /**
     * LA REGLA DE FILTRADO (El núcleo del CSP).
     * Revisa el estado actual de sus nodos y elimina del 'domain' los valores imposibles.
     * @return true si logró borrar al menos un valor de algún dominio; false si no hizo cambios.
     */
    boolean pruneDomains();

    /**
     * Control de daños prematuro (Poda).
     * Mira al futuro: con los valores que aún quedan en los dominios, ¿es viable ganar?
     * @return false si ya se violó la regla o si alguna celda se quedó con 0 opciones.
     */
    boolean canBeSatisfied();
    
    /**
     * Verificación final de victoria.
     * @return true si TODOS los nodos de la restricción ya están decididos (tamaño 1) 
     *         Y además cumplen la regla perfectamente.
     */
    boolean isSatisfied();
}