package es.uhu.amc.amcp2;

public interface IAutomataFinitoNoDeterminista extends IProceso {
    /**
     * Carga el AFND a partir de los datos del fichero de texto indicado.
     * El fichero de texto contendrá la descripción (Estados, estado
     * inicial, estados finales, transiciones y transiciones lambda).
     * @param filePath String con la ruta al fichero de texto con la
     *                 descripción del AFND.
     */
    void load(String filePath) throws Exception;
}
