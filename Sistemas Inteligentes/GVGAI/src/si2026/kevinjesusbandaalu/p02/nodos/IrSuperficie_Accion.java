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

public class IrSuperficie_Accion implements IAccion {

    @Override
    public ACTIONS doAction(IMundo m) {
        Mundo83 mundo = (Mundo83) m;

        Vector2d objetivoSuperficie = new Vector2d(mundo.MiPosicion.x, 0);

        int[][] obstaculos = extraerObstaculos(mundo);

        Node nIni = new Node(mundo.MiPosicion); 
        Node nFin = new Node(objetivoSuperficie); 
        
        AStar aStar = new AStar(mundo.filas, mundo.columnas, nIni, nFin); 
        aStar.setBlocks(obstaculos); 
        
        List<Node> camino = aStar.findPath(); 

        // 4. Determinar la acción basada en el primer paso del camino calculado [cite: 151]
        if (camino != null && camino.size() > 1) {
            Node siguientePaso = camino.get(1);
            return determinarAccion(mundo.MiPosicion, new Vector2d(siguientePaso.getCol(), siguientePaso.getRow())); 
        }

        // Si ya estamos en la superficie o no hay camino seguro, nos mantenemos
        return ACTIONS.ACTION_NIL;
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
        
        if (dy == -1) return ACTIONS.ACTION_UP; 
        if (dy == 1) return ACTIONS.ACTION_DOWN; 
        if (dx == 1) return ACTIONS.ACTION_RIGHT; 
        if (dx == -1) return ACTIONS.ACTION_LEFT; 
        
        return ACTIONS.ACTION_NIL;
    }
}