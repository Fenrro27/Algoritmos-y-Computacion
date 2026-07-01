package es.uhu.amc.amcp2;

public interface IProceso {
    /**
     * Indica si el estado pasado como parámetro es final o no.
     * @param estado código del estado que se quiere comprobar.
     * @return true si es final o false en caso contrario.
     */
    boolean esFinal(String estado);

    /**
     * Comprueba la cadena pasada e indica si el autómata la
     * reconoce o no.
     * @param cadena String con la cadena de símbolos a
     *               comprobar.
     * @return true en caso de reconocer la cadena o false en
     *              caso contrario.
     */
    boolean reconocer(String cadena);

    /**
     * Devuelve una representación en formato string de las
     * transiciones y estados finales del autómata.
     * @return String con los datos del autómata.
     */
    String toString();
}
