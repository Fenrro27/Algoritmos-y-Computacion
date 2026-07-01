package es.uhu.amc.amcp2;

import java.io.IOException;

/**
 * Implementación Dummy de un AFND
 */
public class AFNDDummy implements IAutomataFinitoNoDeterminista {
    @Override
    public void load(String filePath) throws Exception {
        // Aquí debería comprobar el fichero (existencia, formato, transiciones, etc.)
        throw new IOException("Dummy load");
    }



    @Override
    public boolean esFinal(String estado) {
        return false;
    }

    @Override
    public boolean reconocer(String cadena) {
        return false;
    }

    @Override
    public String toString() {
        return "AFDDummy{}";
    }
}
