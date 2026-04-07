package si2026.kevinjesusbandaalu.p01.reglas;

import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.ICondicion;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import tools.Vector2d;

public class Centrar_Condicion implements ICondicion {

    // Radio de tolerancia: si está a más de 3 bloques del centro, se activa
    private static final double RADIO_TOLERANCIA = 2.0;

    @Override
    public boolean seCumple(IMundo m) {
        Mundo84 mundo = (Mundo84) m;

        // 1. Calculamos el centro del mundo (en celdas)
        double centroX = mundo.columnas / 2.0;
        double centroY = mundo.filas / 2.0;
        Vector2d centro = new Vector2d(centroX, centroY);

        // 2. Calculamos la distancia actual al centro
        double distanciaAlCentro = mundo.MiPosicion.dist(centro);

        // 3. Si la distancia es mayor que nuestra tolerancia, devolvemos true para "regresar"
        return distanciaAlCentro > RADIO_TOLERANCIA;
    }
}