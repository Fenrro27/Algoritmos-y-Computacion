package si2026.kevinjesusbandaalu.p07;

import java.util.ArrayList;
import core.game.Observation;
import core.game.StateObservation;
import si2026.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo78 implements IMundo {

    public int Bloque;
    public int columnas, filas;
    public Vector2d posAvatar;
    public ArrayList<Vector2d> muros = new ArrayList<>();
    public ArrayList<Vector2d> cajas = new ArrayList<>();
    public ArrayList<Vector2d> objetivos = new ArrayList<>();
    public ArrayList<Vector2d> suelo = new ArrayList<>();

    public Mundo78(StateObservation stateObs) {
        Bloque = stateObs.getBlockSize();
        columnas = stateObs.getWorldDimension().width / Bloque;
        filas = stateObs.getWorldDimension().height / Bloque;
        AnalizarEntorno(stateObs);
    }

    @Override
    public void AnalizarEntorno(StateObservation stateObs) {
        muros.clear();
        cajas.clear();
        objetivos.clear();
        suelo.clear();

        Vector2d pos = stateObs.getAvatarPosition();
        posAvatar = new Vector2d(pos.x / Bloque, pos.y / Bloque);

        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Vector2d celda = new Vector2d(x, y);
                for (Observation obs : grid[x][y]) {
                    switch (obs.itype) {
                        case 0: muros.add(celda); break;
                        case 6: cajas.add(celda); break;
                        case 4: objetivos.add(celda); break;
                        case 3: suelo.add(celda); break;
                    }
                }
            }
        }
    }
}
