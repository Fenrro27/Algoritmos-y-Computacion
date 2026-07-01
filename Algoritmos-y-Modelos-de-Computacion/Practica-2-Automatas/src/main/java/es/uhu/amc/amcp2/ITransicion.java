package es.uhu.amc.amcp2;

public interface ITransicion {
    /**
     * Devuelve el código del estado inicial.
     * @return String con el código del estado inicial.
     */
    String getEstadoInicial();

    /**
     * Devuelve el código del estado final.
     * @return String con el código del estado final.
     */
    String getEstadoFinal();

    /**
     * Devuelve el carácter (símbolo) que produce la transición.
     * @return Char con el carácter que produce la transición.
     */
    char getSimbolo();
}
