package es.uhu.amc.amcp2;

public interface IAutomataFinitoDeterminista extends IProceso {
    /**
     * Carga el AFD a partir de los datos del fichero de texto indicado.
     * El fichero de texto contendrá la descripción (Estados, estado
     * inicial, estados finales y transiciones).
     * @param filePath String con la ruta al fichero de texto con la
     *                 descripción del AFD.
     *
     */
    void load(String filePath) throws Exception;
}
