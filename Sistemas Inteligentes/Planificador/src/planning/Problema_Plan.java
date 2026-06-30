package planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsula un problema de planificación STRIPS/POP.
 *
 * Un problema de planificación queda definido por:
 * <ul>
 *   <li><b>Estado inicial (Ei)</b>: situación de partida del mundo.</li>
 *   <li><b>Estado final / objetivo (Ef)</b>: situación que se desea alcanzar.</li>
 *   <li><b>Acciones</b>: conjunto de operadores disponibles para el planificador.</li>
 * </ul>
 */
public class Problema_Plan {

    // -------------------------------------------------------------------------
    // Atributos
    // -------------------------------------------------------------------------

    /** Estado inicial del problema. */
    private final State estadoInicial;

    /** Estado objetivo (estado final) del problema. */
    private final State estadoFinal;

    /**
     * Lista de acciones disponibles para el planificador.
     * El orden puede influir en la exploración del espacio de búsqueda.
     */
    private final List<Action> acciones;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea un problema de planificación con los tres componentes básicos.
     *
     * @param estadoInicial estado inicial del mundo
     * @param estadoFinal   estado objetivo a alcanzar
     * @param acciones      lista de acciones disponibles
     */
    public Problema_Plan(State estadoInicial, State estadoFinal, List<Action> acciones) {
        if (estadoInicial == null) {
            throw new IllegalArgumentException("El estado inicial no puede ser nulo.");
        }
        if (estadoFinal == null) {
            throw new IllegalArgumentException("El estado final no puede ser nulo.");
        }
        if (acciones == null) {
            throw new IllegalArgumentException("La lista de acciones no puede ser nula.");
        }
        this.estadoInicial = estadoInicial;
        this.estadoFinal   = estadoFinal;
        this.acciones      = Collections.unmodifiableList(new ArrayList<Action>(acciones));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Devuelve el estado inicial del problema.
     *
     * @return estado inicial
     */
    public State getEstadoInicial() {
        return estadoInicial;
    }

    /**
     * Devuelve el estado objetivo (estado final) del problema.
     *
     * @return estado final
     */
    public State getEstadoFinal() {
        return estadoFinal;
    }

    /**
     * Devuelve la lista inmutable de acciones disponibles.
     *
     * @return lista de acciones
     */
    public List<Action> getAcciones() {
        return acciones;
    }

    // -------------------------------------------------------------------------
    // Consultas de utilidad para los algoritmos
    // -------------------------------------------------------------------------

    /**
     * Comprueba si el estado dado satisface el objetivo del problema.
     * Equivale a verificar que todos los literales de Ef están en el estado.
     *
     * @param estado estado a evaluar
     * @return true si el estado cumple el objetivo
     */
    public boolean esObjetivo(State estado) {
        return estado.satisfies(estadoFinal.getLiterals());
    }

    /**
     * Devuelve las acciones aplicables en un estado dado.
     * Una acción es aplicable si sus precondiciones se satisfacen en el estado.
     * Usado por STRIPS para expandir el espacio de búsqueda.
     *
     * @param estado estado actual
     * @return lista (posiblemente vacía) de acciones aplicables
     */
    public List<Action> accionesAplicables(State estado) {
        List<Action> aplicables = new ArrayList<Action>();
        for (Action a : acciones) {
            if (a.isApplicable(estado)) {
                aplicables.add(a);
            }
        }
        return aplicables;
    }

    /**
     * Devuelve las acciones que logran (achieves) un literal dado.
     * Es decir, las acciones cuya lista de adición contiene el literal.
     * Usado por POP para encontrar proveedores de subobjetivos.
     *
     * @param literal subobjetivo a satisfacer
     * @return lista de acciones que producen el literal
     */
    public List<Action> accionesQueLogran(Literal literal) {
        List<Action> resultado = new ArrayList<Action>();
        for (Action a : acciones) {
            if (a.achieves(literal)) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    /**
     * Devuelve las acciones que amenazan (threatens) un literal dado.
     * Es decir, las acciones cuya lista de borrado contiene el literal.
     * Usado por POP para detectar amenazas sobre enlaces causales.
     *
     * @param literal literal a proteger
     * @return lista de acciones que eliminan el literal
     */
    public List<Action> accionesQueAmenazan(Literal literal) {
        List<Action> resultado = new ArrayList<Action>();
        for (Action a : acciones) {
            if (a.threatens(literal)) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    /**
     * Devuelve los literales del objetivo que aún no están satisfechos
     * en el estado inicial. Punto de partida para la pila de STRIPS.
     *
     * @return lista de subobjetivos pendientes desde el estado inicial
     */
    public List<Literal> subobjetivosPendientes() {
        return estadoInicial.unsatisfied(estadoFinal.getLiterals());
    }

    // -------------------------------------------------------------------------
    // Representación textual
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Problema de Planificación ===\n");
        sb.append("Ei (Estado inicial) : ").append(estadoInicial).append("\n");
        sb.append("Ef (Estado final)   : ").append(estadoFinal).append("\n");
        sb.append("Acciones (").append(acciones.size()).append("):\n");
        Iterator<Action> it = acciones.iterator();
        while (it.hasNext()) {
            sb.append("  ").append(it.next().toString().replace("\n", "\n  "));
            if (it.hasNext()) sb.append("\n");
        }
        return sb.toString();
    }
}
