package si2026.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import core.game.Observation;
import core.game.StateObservation;
import si2026.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo49 implements IMundo {
    public Vector2d MiPosicion;
    public Vector2d miOrientacion;
    public int Bloque, columnas, filas;

    public ArrayList<Vector2d> catapultas;
    public ArrayList<Vector2d> nenufar; // landingpad
    public ArrayList<Vector2d> troncos; // logs
    public ArrayList<Vector2d> agua; // water
    public ArrayList<Vector2d> zonaMarron; // floor/walls
    public Vector2d salida;

    public HashMap<Vector2d, Integer> direccionesCatapultas;
    public HashMap<Vector2d, Integer> direccionesNenufares;
    public StateObservation stateObsActual;

    private HashMap<String, String> diccionarioLetras = new HashMap<>();

    public void descartarCatapultas() {
        ArrayList<Vector2d> catapultasAEliminar = new  ArrayList<>();
        for (Vector2d loc : catapultas) {
            Integer dir = direccionesCatapultas.get(loc);

            switch (dir) {
                case 14: // down
                    for (int i = (int) loc.y; i <= filas; i++) {
                        if(troncos.contains(new Vector2d(loc.x, i))) {
                            catapultasAEliminar.add(new Vector2d(loc.x, i));
                            continue;}
                    }
                    break;
                case 15: // up
                    for (int i = (int) loc.y; i <= 0; i++) {
                        if(troncos.contains(new Vector2d(loc.x, i))) {
                            catapultasAEliminar.add(new Vector2d(loc.x, i));
                            continue;}

                    }
                    break;
                case 16: // right
                    for (int i = (int) loc.x; i <= columnas; i++) {
                        if(troncos.contains(new Vector2d(i, loc.y))) {
                            catapultasAEliminar.add(new Vector2d(i,loc.y));
                            continue;}
                    }
                    break;
                case 17: // left
                    for (int i = (int) loc.x; i <= 0; i++) {
                        if(troncos.contains(new Vector2d(i, loc.y))) {
                            catapultasAEliminar.add(new Vector2d(i, loc.y));
                            continue;}
                    }
                    break;
            }
        }
        catapultas.removeAll(catapultasAEliminar);
    }

    public Mundo49(StateObservation stateObs) {
        Bloque = stateObs.getBlockSize();
        columnas = stateObs.getWorldDimension().width / Bloque;
        filas = stateObs.getWorldDimension().height / Bloque;

        catapultas = new ArrayList<>();
        nenufar = new ArrayList<>();
        troncos = new ArrayList<>();
        agua = new ArrayList<>();
        zonaMarron = new ArrayList<>();
        direccionesCatapultas = new HashMap<>();
        direccionesNenufares = new HashMap<>();

        AnalizarEntorno(stateObs);
    }

    @Override
    public void AnalizarEntorno(StateObservation stateObs) {
        this.stateObsActual = stateObs;
        catapultas.clear();
        nenufar.clear();
        troncos.clear();
        agua.clear();
        zonaMarron.clear();
        direccionesCatapultas.clear();
        direccionesNenufares.clear();
        salida = null;

        Vector2d pos = stateObs.getAvatarPosition();
        MiPosicion = new Vector2d(pos.x / Bloque, pos.y / Bloque);
        miOrientacion = stateObs.getAvatarOrientation();

        obtenerObservacionesAnalizadas(stateObs);
    }

    public void obtenerObservacionesAnalizadas(StateObservation stateObs) {
        HashSet<String> tiposVistos = new HashSet<>();
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Vector2d celda = new Vector2d(x, y);
                ArrayList<Observation> obsEnCelda = grid[x][y];

                if (!obsEnCelda.isEmpty()) {
                    for (Observation obs : obsEnCelda) {
                        String clave = obs.category + "-" + obs.itype;
                        if (!tiposVistos.contains(clave)) {
                            tiposVistos.add(clave);
                            asignarLetraADiccionario(obs);
                        }

                        switch (obs.category) {
                            case 2: // Salida
                                salida = celda;
                                break;
                            case 4: // Immovables
                                switch (obs.itype) {
                                    case 3:
                                        agua.add(celda);
                                        break; // Agua
                                    case 7:
                                    case 8:
                                        nenufar.add(celda);
                                        direccionesNenufares.put(celda, obs.itype);
                                        break;
                                    case 14:
                                    case 15:
                                    case 16:
                                    case 17:
                                        catapultas.add(celda);
                                        direccionesCatapultas.put(celda, obs.itype);
                                        break;
                                    case 0:
                                        zonaMarron.add(celda);
                                        break; // Arboles/Muros
                                }
                                break;
                            case 6:
                                if (obs.itype == 12)
                                    zonaMarron.add(celda); // Marron
                                break;
                            case 5: // Missiles
                                troncos.add(celda);
                                break;
                        }
                    }
                }
            }
        }
        pintarTerreno(stateObs);
    }

    private void asignarLetraADiccionario(Observation o) {
        String clave = o.category + "-" + o.itype;
        if (diccionarioLetras.containsKey(clave))
            return;

        String letra = "?";
        switch (o.category) {
            case 0:
                letra = "A";
                break; // personaje principal
            case 2:
                letra = "S";
                break; // Salida
            case 4:
                switch (o.itype) {
                    case 0:
                        letra = "#";
                        break; // Arboles
                    case 16:
                        letra = "1";
                        break; // Catapulta a la derecha
                    case 17:
                        letra = "2";
                        break; // Catapulta a la izquierda
                    case 3:
                        letra = " ";
                        break; // Agua
                    case 14:
                        letra = "3";
                        break; // catapulta para abajo
                    case 15:
                        letra = "4";
                        break; // catapulta para arriba
                    case 7:
                        letra = "5";
                        break; // Nenufar a la izquierda
                    case 8:
                        letra = "6";
                        break; // nenufar a la derecha

                    default:
                        System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
                        break;
                }
                break;
            case 6:
                switch (o.itype) {
                    case 10:
                        letra = " ";
                        break; // ?, no se q es
                    case 11:
                        letra = " ";
                        break; // ?, no se q es
                    case 12:
                        letra = "M";
                        break; // Marron
                    default:
                        System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
                        break;
                }
                break;

            default:
                System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
        }
        diccionarioLetras.put(clave, letra);
    }

    private void pintarTerreno(StateObservation stateObs) {
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        System.out.println("\n=== MAPA DEL TERRENO ===");
        System.out.print("    ");
        for (int x = 0; x < grid.length; x++)
            System.out.print(String.format("%2d", x));
        System.out.println();
        System.out.print("\n    ");
        for (int i = 0; i < grid.length; i++)
            System.out.print("--");
        System.out.println();

        for (int y = 0; y < grid[0].length; y++) {
            System.out.print(String.format("%2d |", y));
            for (int x = 0; x < grid.length; x++) {
                ArrayList<Observation> obsEnCelda = grid[x][y];
                if (obsEnCelda.isEmpty()) {
                    System.out.print(" ."); // Suelo transitable
                } else {
                    String letraElegida = null;
                    for (Observation o : obsEnCelda) {
                        String clave = o.category + "-" + o.itype;
                        if (diccionarioLetras.containsKey(clave)) {
                            letraElegida = diccionarioLetras.get(clave);
                            if (!letraElegida.equals("?"))
                                break;
                        }
                    }
                    System.out.print(" " + (letraElegida != null ? letraElegida : " "));
                }
            }
            System.out.println();
        }
        System.out.println("========================\n");
    }

    public double heuristica(StateObservation stateObs) {
        if (stateObs.getGameWinner() == ontology.Types.WINNER.PLAYER_LOSES)
            return 1000000;
        if (stateObs.getGameWinner() == ontology.Types.WINNER.PLAYER_WINS)
            return 0;
        Vector2d pos = stateObs.getAvatarPosition();
        Vector2d avatarPosGrid = new Vector2d(pos.x / Bloque, pos.y / Bloque);
        // Distancia Manhattan a la salida si existe
        if (salida != null) {
            return Math.abs(avatarPosGrid.x - salida.x) + Math.abs(avatarPosGrid.y - salida.y);
        }
        return 0;
    }
}