package planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa un estado del mundo en planificación STRIPS/POP.
 *
 * Un estado es un conjunto de literales que describe completamente
 * las propiedades del mundo en un instante dado.
 * Se asume la <b>Closed World Assumption (CWA)</b>: todo literal
 * no presente en el estado se considera falso.
 *
 * Ejemplos de estado inicial en el mundo de bloques:
 *   { on(A,B), on(B,Table), clear(A), clear(Table), handempty() }
 */
public class State {

    /**
     * Conjunto de literales que conforman el estado.
     * Se usa LinkedHashSet para mantener orden de inserción y
     * garantizar que no haya duplicados.
     */
    private final Set<Literal> literals;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /** Crea un estado vacío. */
    public State() {
        this.literals = new LinkedHashSet<Literal>();
    }

    /**
     * Crea un estado a partir de una colección de literales.
     *
     * @param literals literales iniciales del estado
     */
    public State(Collection<Literal> literals) {
        this.literals = new LinkedHashSet<Literal>(literals);
    }

    /**
     * Crea un estado a partir de un array de literales (varargs).
     *
     * @param literals literales iniciales del estado
     */
    public State(Literal... literals) {
        this.literals = new LinkedHashSet<Literal>();
        for (Literal l : literals) {
            this.literals.add(l);
        }
    }

    // -------------------------------------------------------------------------
    // Consultas sobre el estado
    // -------------------------------------------------------------------------

    /**
     * Comprueba si un literal concreto está presente en el estado.
     *
     * @param literal literal a buscar
     * @return true si el literal pertenece al estado
     */
    public boolean contains(Literal literal) {
        return literals.contains(literal);
    }

    /**
     * Comprueba si todos los literales de la lista están en el estado.
     * Se usa para verificar precondiciones de acciones.
     *
     * @param conditions lista de literales a verificar
     * @return true si todos los literales se satisfacen en este estado
     */
    public boolean satisfies(Collection<Literal> conditions) {
        return literals.containsAll(conditions);
    }

    /**
     * Devuelve los literales de la lista que NO están en el estado.
     * Útil para calcular subgoals pendientes en STRIPS.
     *
     * @param conditions condiciones a verificar
     * @return lista de condiciones no satisfechas (puede estar vacía)
     */
    public List<Literal> unsatisfied(Collection<Literal> conditions) {
        List<Literal> result = new ArrayList<Literal>();
        for (Literal l : conditions) {
            if (!literals.contains(l)) {
                result.add(l);
            }
        }
        return result;
    }

    /**
     * Devuelve una vista no modificable de los literales del estado.
     *
     * @return conjunto de literales (no modificable)
     */
    public Set<Literal> getLiterals() {
        return Collections.unmodifiableSet(literals);
    }

    /**
     * Indica si el estado está vacío.
     *
     * @return true si no contiene ningún literal
     */
    public boolean isEmpty() {
        return literals.isEmpty();
    }

    /**
     * Devuelve el número de literales en el estado.
     *
     * @return cardinalidad del estado
     */
    public int size() {
        return literals.size();
    }

    // -------------------------------------------------------------------------
    // Modificaciones (devuelven nuevo estado — diseño inmutable)
    // -------------------------------------------------------------------------

    /**
     * Aplica los efectos de una acción y devuelve el <b>nuevo estado</b>
     * resultante. El estado original no se modifica.
     *
     * El proceso sigue la semántica STRIPS:
     * <ol>
     *   <li>Se eliminan los literales de la lista de substracción (DEL).</li>
     *   <li>Se añaden los literales de la lista de adición (ADD).</li>
     * </ol>
     *
     * @param action acción cuyos efectos se aplican
     * @return nuevo estado resultante
     * @throws IllegalStateException si las precondiciones no se satisfacen
     */
    public State apply(Action action) {
        if (!satisfies(action.getPreconditions())) {
            throw new IllegalStateException(
                "Las precondiciones de la acción '" + action.getName()
                + "' no se satisfacen en el estado actual.\n"
                + "Estado: " + this + "\n"
                + "Precondiciones: " + action.getPreconditions()
            );
        }
        Set<Literal> next = new LinkedHashSet<Literal>(this.literals);
        next.removeAll(action.getDeleteList()); // DEL
        next.addAll(action.getAddList());        // ADD
        return new State(next);
    }

    /**
     * Añade un literal al estado devolviendo un nuevo estado.
     *
     * @param literal literal a añadir
     * @return nuevo estado con el literal añadido
     */
    public State add(Literal literal) {
        Set<Literal> next = new LinkedHashSet<Literal>(this.literals);
        next.add(literal);
        return new State(next);
    }

    /**
     * Elimina un literal del estado devolviendo un nuevo estado.
     *
     * @param literal literal a eliminar
     * @return nuevo estado sin el literal
     */
    public State remove(Literal literal) {
        Set<Literal> next = new LinkedHashSet<Literal>(this.literals);
        next.remove(literal);
        return new State(next);
    }

    // -------------------------------------------------------------------------
    // Igualdad y hash
    // -------------------------------------------------------------------------

    /**
     * Dos estados son iguales si contienen exactamente los mismos literales.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof State)) return false;
        State other = (State) obj;
        return literals.equals(other.literals);
    }

    @Override
    public int hashCode() {
        return literals.hashCode();
    }

    // -------------------------------------------------------------------------
    // Representación textual
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{ ");
        Iterator<Literal> it = literals.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) sb.append(", ");
        }
        sb.append(" }");
        return sb.toString();
    }
}
