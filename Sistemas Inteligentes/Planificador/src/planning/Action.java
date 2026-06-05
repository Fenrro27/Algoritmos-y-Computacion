package planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Representa una acción en el formalismo STRIPS/POP.
 *
 * Una acción se define mediante tres listas de literales:
 * <ul>
 *   <li><b>Precondiciones (PRE)</b>: literales que deben ser verdaderos
 *       en el estado actual para poder aplicar la acción.</li>
 *   <li><b>Lista de Adición (ADD)</b>: literales que pasan a ser
 *       verdaderos tras ejecutar la acción.</li>
 *   <li><b>Lista de Substracción / Borrado (DEL)</b>: literales que
 *       dejan de ser verdaderos tras ejecutar la acción.</li>
 * </ul>
 *
 * <h3>Ejemplo — mundo de bloques: apilar A sobre B</h3>
 * <pre>
 *   Nombre: stack(A, B)
 *   PRE:  { holding(A), clear(B) }
 *   ADD:  { on(A,B), clear(A), handempty() }
 *   DEL:  { holding(A), clear(B) }
 * </pre>
 *
 * <h3>Diseño</h3>
 * La clase es inmutable una vez construida. Para construirla de forma
 * cómoda se proporciona un {@link Builder} interno.
 */
public class Action {

    /** Nombre descriptivo de la acción (ej. "stack(A,B)", "pickup(X)"). */
    private final String name;

    /** Literales que deben cumplirse antes de ejecutar la acción. */
    private final List<Literal> preconditions;

    /** Literales que se añaden al estado tras la ejecución. */
    private final List<Literal> addList;

    /** Literales que se eliminan del estado tras la ejecución. */
    private final List<Literal> deleteList;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una acción directamente a partir de sus tres listas.
     *
     * @param name          nombre de la acción
     * @param preconditions lista de precondiciones
     * @param addList       lista de adición
     * @param deleteList    lista de substracción/borrado
     */
    public Action(String name,
                  Collection<Literal> preconditions,
                  Collection<Literal> addList,
                  Collection<Literal> deleteList) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la acción no puede ser nulo o vacío.");
        }
        this.name          = name;
        this.preconditions = Collections.unmodifiableList(new ArrayList<Literal>(preconditions));
        this.addList       = Collections.unmodifiableList(new ArrayList<Literal>(addList));
        this.deleteList    = Collections.unmodifiableList(new ArrayList<Literal>(deleteList));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return nombre de la acción */
    public String getName() {
        return name;
    }

    /** @return lista inmutable de precondiciones */
    public List<Literal> getPreconditions() {
        return preconditions;
    }

    /** @return lista inmutable de adición (efectos positivos) */
    public List<Literal> getAddList() {
        return addList;
    }

    /** @return lista inmutable de substracción/borrado (efectos negativos) */
    public List<Literal> getDeleteList() {
        return deleteList;
    }

    // -------------------------------------------------------------------------
    // Consultas útiles para POP
    // -------------------------------------------------------------------------

    /**
     * Comprueba si esta acción puede lograr (produce) un literal dado.
     * Un literal es <em>logrado</em> por la acción si aparece en su ADD list.
     * Usado en POP para encontrar proveedores de un subobjetivo.
     *
     * @param goal literal subobjetivo a satisfacer
     * @return true si la acción añade el literal al estado
     */
    public boolean achieves(Literal goal) {
        return addList.contains(goal);
    }

    /**
     * Comprueba si esta acción amenaza un literal dado.
     * Una acción <em>amenaza</em> un literal si lo incluye en su DEL list.
     * Usado en POP para detectar y resolver amenazas.
     *
     * @param literal literal a comprobar
     * @return true si la acción elimina el literal del estado
     */
    public boolean threatens(Literal literal) {
        return deleteList.contains(literal);
    }

    /**
     * Comprueba si las precondiciones de la acción se satisfacen en un estado.
     *
     * @param state estado actual del mundo
     * @return true si el estado satisface todas las precondiciones
     */
    public boolean isApplicable(State state) {
        return state.satisfies(preconditions);
    }

    // -------------------------------------------------------------------------
    // Igualdad y hash
    // -------------------------------------------------------------------------

    /**
     * Dos acciones son iguales si tienen el mismo nombre y las mismas
     * tres listas (mismo contenido y orden).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Action)) return false;
        Action other = (Action) obj;
        return name.equals(other.name)
            && preconditions.equals(other.preconditions)
            && addList.equals(other.addList)
            && deleteList.equals(other.deleteList);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + preconditions.hashCode();
        result = 31 * result + addList.hashCode();
        result = 31 * result + deleteList.hashCode();
        return result;
    }

    // -------------------------------------------------------------------------
    // Representación textual
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Action[" + name + "]\n"
            + "  PRE: " + preconditions + "\n"
            + "  ADD: " + addList + "\n"
            + "  DEL: " + deleteList;
    }

    // =========================================================================
    // Builder
    // =========================================================================

    /**
     * Facilita la construcción fluida de acciones.
     *
     * <pre>
     * Action stack = new Action.Builder("stack(A,B)")
     *     .pre(new Literal("holding", "A"))
     *     .pre(new Literal("clear",   "B"))
     *     .add(new Literal("on",      new String[]{"A","B"}, true))
     *     .add(new Literal("clear",   "A"))
     *     .add(new Literal("handempty"))
     *     .del(new Literal("holding", "A"))
     *     .del(new Literal("clear",   "B"))
     *     .build();
     * </pre>
     */
    public static class Builder {

        private final String name;
        private final List<Literal> preconditions = new ArrayList<Literal>();
        private final List<Literal> addList       = new ArrayList<Literal>();
        private final List<Literal> deleteList    = new ArrayList<Literal>();

        /**
         * Inicia el builder con el nombre de la acción.
         *
         * @param name nombre de la acción
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Añade un literal a las precondiciones.
         *
         * @param literal literal de precondición
         * @return este builder (encadenamiento fluido)
         */
        public Builder pre(Literal literal) {
            preconditions.add(literal);
            return this;
        }

        /**
         * Añade un literal a la lista de adición.
         *
         * @param literal literal de efecto positivo
         * @return este builder (encadenamiento fluido)
         */
        public Builder add(Literal literal) {
            addList.add(literal);
            return this;
        }

        /**
         * Añade un literal a la lista de substracción/borrado.
         *
         * @param literal literal de efecto negativo
         * @return este builder (encadenamiento fluido)
         */
        public Builder del(Literal literal) {
            deleteList.add(literal);
            return this;
        }

        /**
         * Construye la acción.
         *
         * @return acción inmutable
         */
        public Action build() {
            return new Action(name, preconditions, addList, deleteList);
        }
    }
}
