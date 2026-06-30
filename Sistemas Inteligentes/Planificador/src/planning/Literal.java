package planning;

/**
 * Representa un literal proposicional en planificación STRIPS/POP.
 *
 * Un literal es un átomo proposicional o su negación. Los literales no
 * llevan argumentos: si necesitas algo con parámetros eso es un predicado
 * (no soportado aquí). Si tu dominio tiene predicados con argumentos,
 * trabaja con su versión <em>ground</em> (ya instanciada) usando un nombre
 * compuesto, por ejemplo {@code on_A_B}, {@code clear_C}.
 *
 * Ejemplos:
 *   p1         → literal positivo de nombre "p1"
 *   ¬p1        → literal negado  de nombre "p1"
 *   handempty  → literal positivo de nombre "handempty"
 */
public class Literal {

    /** Nombre del literal (ej. "p1", "clear_C", "handempty"). */
    private final String name;

    /** Indica si el literal es positivo (true) o negado (false). */
    private final boolean positive;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Crea un literal positivo con el nombre indicado.
     *
     * @param name nombre del literal
     */
    public Literal(String name) {
        this(name, true);
    }

    /**
     * Crea un literal con signo explícito.
     *
     * @param name     nombre del literal
     * @param positive true si es positivo, false si es negado
     */
    public Literal(String name, boolean positive) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del literal no puede ser nulo o vacío.");
        }
        this.name     = name;
        this.positive = positive;
    }

    // -------------------------------------------------------------------------
    // Métodos de fábrica
    // -------------------------------------------------------------------------

    /**
     * Devuelve el complementario de este literal (cambia el signo).
     * Útil en POP para detectar amenazas y conflictos.
     *
     * @return nuevo literal con signo opuesto
     */
    public Literal negate() {
        return new Literal(name, !positive);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public boolean isPositive() {
        return positive;
    }

    public boolean isNegated() {
        return !positive;
    }

    // -------------------------------------------------------------------------
    // Igualdad y hash
    // -------------------------------------------------------------------------

    /**
     * Dos literales son iguales si tienen el mismo nombre y el mismo signo.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Literal)) return false;
        Literal other = (Literal) obj;
        return positive == other.positive
            && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (positive ? 1 : 0);
        return result;
    }

    // -------------------------------------------------------------------------
    // Representación textual
    // -------------------------------------------------------------------------

    /**
     * Devuelve la representación textual del literal.
     * Ejemplos: "p1", "¬p1", "handempty"
     */
    @Override
    public String toString() {
        return positive ? name : ("¬" + name);
    }
}
