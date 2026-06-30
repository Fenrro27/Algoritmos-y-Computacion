package si2026.kevinjesusbandaalu.p02.nodos;

import java.util.List;
import core.game.Observation;
import ontology.Types.ACTIONS;
import si2026.kevinjesusbandaalu.common.IMundo;
import si2026.kevinjesusbandaalu.common.MotorReglas.IAccion;
import si2026.kevinjesusbandaalu.common.aStar.AStar;
import si2026.kevinjesusbandaalu.common.aStar.Node;
import si2026.kevinjesusbandaalu.p02.Mundo83;
import tools.Vector2d;

public class Patrullar_Accion implements IAccion {

    @Override
    public ACTIONS doAction(IMundo m) {
        Mundo83 mundo = (Mundo83) m;

        // 1. Determinar punto de patrulla (Priorizar spawn de buzos, si no, el centro)
        Vector2d puntoPatrulla;
        if (!mundo.spawnBuzos.isEmpty()) {
            Observation sp = mundo.spawnBuzos.get(0);
            puntoPatrulla = new Vector2d(sp.position.x / mundo.Bloque, sp.position.y / mundo.Bloque);
        } else {
            puntoPatrulla = new Vector2d(mundo.columnas / 2, mundo.filas / 2);
        }

        // 2. Extraer obstáculos (Enemigos y Proyectiles de la lista 'evitar')
        int[][] obstaculos = extraerObstaculos(mundo);

        // 3. Calcular camino seguro con AStar
        Node nIni = new Node(mundo.MiPosicion);
        Node nFin = new Node(puntoPatrulla);
        AStar aStar = new AStar(mundo.filas, mundo.columnas, nIni, nFin);
        aStar.setBlocks(obstaculos);
        
        List<Node> camino = aStar.findPath();

        // 4. Ejecutar movimiento o disparar preventivamente si hay enemigos cerca
        if (camino != null && camino.size() > 1) {
            Node siguientePaso = camino.get(1);
            return determinarAccion(mundo.MiPosicion, new Vector2d(siguientePaso.getCol(), siguientePaso.getRow()));
        }

        // Si no hay camino claro, disparar suele ser una buena opción por defecto en Seaquest
        return ACTIONS.ACTION_USE;
    }

    private int[][] extraerObstaculos(Mundo83 mundo) {
        int size = mundo.evitar.size();
        int[][] obs = new int[size][2];
        for (int i = 0; i < size; i++) {
            Observation o = mundo.evitar.get(i);
            obs[i][0] = (int) (o.position.y / mundo.Bloque);
            obs[i][1] = (int) (o.position.x / mundo.Bloque);
        }
        return obs;
    }

    private ACTIONS determinarAccion(Vector2d actual, Vector2d siguiente) {
        int dx = (int) siguiente.x - (int) actual.x;
        int dy = (int) siguiente.y - (int) actual.y;

        if (dx == 1) return ACTIONS.ACTION_RIGHT;
        if (dx == -1) return ACTIONS.ACTION_LEFT;
        if (dy == 1) return ACTIONS.ACTION_DOWN;
        if (dy == -1) return ACTIONS.ACTION_UP;
        
        return ACTIONS.ACTION_USE; 
    }
    
}