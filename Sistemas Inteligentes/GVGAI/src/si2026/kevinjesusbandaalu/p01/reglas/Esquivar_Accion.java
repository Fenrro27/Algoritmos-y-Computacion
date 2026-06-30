package si2026.kevinjesusbandaalu.p01.reglas;

import java.util.HashMap;
import java.util.Map;
import core.game.Observation;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.p01.Mundo84;
import tools.Vector2d;

public class Esquivar_Accion implements IAccion {

    private final Map<ACTIONS, Vector2d> movimientos;

    public Esquivar_Accion() {
        movimientos = new HashMap<>();
        movimientos.put(ACTIONS.ACTION_UP, new Vector2d(0, -1));
        movimientos.put(ACTIONS.ACTION_DOWN, new Vector2d(0, 1));
        movimientos.put(ACTIONS.ACTION_LEFT, new Vector2d(-1, 0));
        movimientos.put(ACTIONS.ACTION_RIGHT, new Vector2d(1, 0));
        movimientos.put(ACTIONS.ACTION_NIL, new Vector2d(0, 0));
    }

    @Override
    public ACTIONS doAction(IMundo m) {
        Mundo84 mundo = (Mundo84) m;
        if (mundo.lasers == null || mundo.lasers.isEmpty()) return ACTIONS.ACTION_NIL;

        int miX = (int) mundo.MiPosicion.x;
        int miY = (int) mundo.MiPosicion.y;

        ACTIONS mejorAccion = ACTIONS.ACTION_NIL;
        double mejorPuntuacion = -Double.MAX_VALUE;

        // Evaluamos quedarse quieto y las 4 direcciones
        for (Map.Entry<ACTIONS, Vector2d> entrada : movimientos.entrySet()) {
            ACTIONS act = entrada.getKey();
            Vector2d mov = entrada.getValue();
            
            int futX = miX + (int) mov.x;
            int futY = miY + (int) mov.y;

            // 1. Filtros de seguridad básicos
            if (futX < 0 || futX >= mundo.columnas || futY < 0 || futY >= mundo.filas) continue;
            if (esPosicionBloqueada(mundo, futX, futY)) continue;

            // 2. Cálculo de puntuación de seguridad
            double puntuacion = evaluarSeguridadPosicion(mundo, futX, futY);

            // 3. Pequeño bono por movimiento para no quedarse estático si hay peligro igual
            if (act != ACTIONS.ACTION_NIL) puntuacion += 0.1;

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorAccion = act;
            }
        }

        return mejorAccion;
    }

    private double evaluarSeguridadPosicion(Mundo84 mundo, int x, int y) {
        double puntuacion = 0;

        for (Observation laser : mundo.lasers) {
            int lx = (int) (laser.position.x / mundo.Bloque);
            int ly = (int) (laser.position.y / mundo.Bloque);

            // Distancia de Manhattan a la bala
            int distManhattan = Math.abs(x - lx) + Math.abs(y - ly);

            // PENALIZACIÓN CRÍTICA: Estar en la misma fila o columna que un láser
            if (lx == x || ly == y) {
                // Cuanto más cerca esté la bala en nuestro eje, más penaliza
                puntuacion -= (100.0 / (distManhattan + 1));
            } else {
                // Estar en un eje distinto es bueno (Seguro)
                puntuacion += 10.0;
            }
            
            // Bonus por distancia general (cuanto más lejos de cualquier bala, mejor)
            puntuacion += distManhattan * 0.5;
        }

        return puntuacion;
    }

    private boolean esPosicionBloqueada(Mundo84 mundo, int x, int y) {
        // No chocar con muros u objetos estáticos
        for (Observation obj : mundo.objetosIntocables) {
            if ((int)(obj.position.x / mundo.Bloque) == x && (int)(obj.position.y / mundo.Bloque) == y) return true;
        }
        // No chocar con enemigos directamente al esquivar
        for (Observation en : mundo.enemigos) {
            if ((int)(en.position.x / mundo.Bloque) == x && (int)(en.position.y / mundo.Bloque) == y) return true;
        }
        return false;
    }
}