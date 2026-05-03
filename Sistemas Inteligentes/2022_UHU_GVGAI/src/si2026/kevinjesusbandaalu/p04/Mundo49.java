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
    public ArrayList<Vector2d> agua; // water
    public ArrayList<Vector2d> zonaMarron; // floor/walls
    public ArrayList<Vector2d> arboles; // deadly obstacles (trees/walls)
    public Vector2d salida;

    public HashMap<Vector2d, Integer> direccionesCatapultas;
    public HashMap<Vector2d, Integer> direccionesNenufares;
    public StateObservation stateObsActual;

    public int[][] dirRutaNenufar; // 1: hay ruta de nenúfar
    public double[][] distanciasMeta;
    public HashMap<Vector2d, Integer> tiempoEsperaCatapultas;
    public HashMap<Vector2d, Vector2d> destinoCatapultas;

    private HashMap<String, String> diccionarioLetras = new HashMap<>();

    public void descartarCatapultas() {
        tiempoEsperaCatapultas = new HashMap<>();
        destinoCatapultas = new HashMap<>();
        ArrayList<Vector2d> catapultasAEliminar = new ArrayList<>();

        for (Vector2d loc : catapultas) {

            Integer dir = direccionesCatapultas.get(loc);
            int dx = 0, dy = 0;
            if (dir == 14)
                dy = 1; // ABAJO
            else if (dir == 15)
                dy = -1; // ARRIBA
            else if (dir == 16)
                dx = 1; // DERECHA
            else if (dir == 17)
                dx = -1; // IZQUIERDA

            int cx = (int) loc.x;
            int cy = (int) loc.y;
            int x = cx + dx;
            int y = cy + dy;
            int d = 1;

            boolean aterrizaSeguro = false;
            int mejorEspera = -1;

            while (x >= 0 && x < columnas && y >= 0 && y < filas) {
                Vector2d posTraj = new Vector2d(x, y);

                // La bala choca contra muros (arboles o zonaMarron)
                if (zonaMarron.contains(posTraj) || arboles.contains(posTraj)) {
                    // Al chocar, el avatar se transforma en onground y retrocede un paso (stepBack)
                    Vector2d prevPos = new Vector2d(x - dx, y - dy);
                    boolean prevEsVacia = false;
                    if (prevPos.x >= 0 && prevPos.x < columnas && prevPos.y >= 0 && prevPos.y < filas) {
                        prevEsVacia = stateObsActual.getObservationGrid()[(int) prevPos.x][(int) prevPos.y].isEmpty();
                    }

                    // Si el paso anterior es suelo vacío, zonaMarron, ruta de nenúfar o LA SALIDA,
                    // aterrizamos seguros
                    if (prevEsVacia || zonaMarron.contains(prevPos)
                            || dirRutaNenufar[(int) prevPos.x][(int) prevPos.y] > 0 ||
                            (salida != null && salida.equals(prevPos))) {
                        aterrizaSeguro = true;
                        if (mejorEspera == -1 || 0 < mejorEspera)
                            mejorEspera = 0;
                        x = (int) prevPos.x;
                        y = (int) prevPos.y;
                    }
                    break; // El vuelo no continúa más allá del muro
                }

                // Comprobar si un nenúfar móvil nos puede interceptar aquí
                for (Vector2d n : nenufar) {
                    if (n.y == y) {
                        Integer dirNenufar = direccionesNenufares.get(n);
                        if (dirNenufar != null) {
                            int w = -1;
                            if (dirNenufar == 10 && n.x <= x) { // Mueve a derecha (asumido 10)
                                boolean pathClear = true;
                                for (int nx = (int) n.x; nx <= x; nx++) {
                                    Vector2d obst = new Vector2d(nx, y);
                                    if (arboles.contains(obst) || zonaMarron.contains(obst)) {
                                        pathClear = false;
                                        break;
                                    }
                                }
                                if (pathClear)
                                    w = (int) (x - n.x) * 10 - d;
                            } else if (dirNenufar == 11 && n.x >= x) { // Mueve a izquierda (asumido 11)
                                boolean pathClear = true;
                                for (int nx = (int) n.x; nx >= x; nx--) {
                                    Vector2d obst = new Vector2d(nx, y);
                                    if (arboles.contains(obst) || zonaMarron.contains(obst)) {
                                        pathClear = false;
                                        break;
                                    }
                                }
                                if (pathClear)
                                    w = (int) (n.x - x) * 10 - d;
                            }
                            if (w >= 0) {
                                aterrizaSeguro = true;
                                if (mejorEspera == -1 || w < mejorEspera)
                                    mejorEspera = w;
                            }
                        }
                    }
                }

                x += dx;
                y += dy;
                d++;
            }

            if (!aterrizaSeguro) {
                catapultasAEliminar.add(loc);
            } else {
                tiempoEsperaCatapultas.put(loc, mejorEspera);
                destinoCatapultas.put(loc, new Vector2d(x, y));
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
        agua = new ArrayList<>();
        zonaMarron = new ArrayList<>();
        arboles = new ArrayList<>();
        direccionesCatapultas = new HashMap<>();
        direccionesNenufares = new HashMap<>();

        AnalizarEntorno(stateObs);
    }

    @Override
    public void AnalizarEntorno(StateObservation stateObs) {
        this.stateObsActual = stateObs;
        catapultas.clear();
        nenufar.clear();
        agua.clear();
        zonaMarron.clear();
        arboles.clear();
        direccionesCatapultas.clear();
        direccionesNenufares.clear();
        salida = null;

        Vector2d pos = stateObs.getAvatarPosition();
        MiPosicion = new Vector2d(pos.x / Bloque, pos.y / Bloque);
        miOrientacion = stateObs.getAvatarOrientation();

        obtenerObservacionesAnalizadas(stateObs);
        calcularRutasNenufares();
        descartarCatapultas();
        calcularMapaDistancias();
        pintarTerreno(stateObs);
    }

    private void calcularRutasNenufares() {
        if (dirRutaNenufar == null) {
            dirRutaNenufar = new int[columnas][filas];
        } else {
            for (int i = 0; i < columnas; i++) {
                for (int j = 0; j < filas; j++) {
                    dirRutaNenufar[i][j] = 0;
                }
            }
        }
        for (Vector2d n : nenufar) {
            // Trazar ruta horizontal para el tronco
            for (int step = -1; step <= 1; step += 2) {
                int nx = (int) n.x;
                int ny = (int) n.y;
                while (nx >= 0 && nx < columnas) {
                    Vector2d pos = new Vector2d(nx, ny);
                    // Si choca con pared o catapulta, termina la ruta
                    boolean esPared = arboles.contains(pos) || zonaMarron.contains(pos) || catapultas.contains(pos);
                    if (esPared && !pos.equals(n))
                        break;
                    dirRutaNenufar[nx][ny] = 1;
                    nx += step;
                }
            }
        }
    }

    private void calcularMapaDistancias() {
        if (distanciasMeta == null) {
            distanciasMeta = new double[columnas][filas];
        }
        for (int i = 0; i < columnas; i++) {
            for (int j = 0; j < filas; j++) {
                distanciasMeta[i][j] = 1000000;
            }
        }

        if (salida == null)
            return;

        java.util.PriorityQueue<NodeDist> pq = new java.util.PriorityQueue<>();
        distanciasMeta[(int) salida.x][(int) salida.y] = 0;
        pq.add(new NodeDist((int) salida.x, (int) salida.y, 0));

        int[] dx = { 0, 0, 1, -1 };
        int[] dy = { 1, -1, 0, 0 };

        while (!pq.isEmpty()) {
            NodeDist actual = pq.poll();
            if (actual.dist > distanciasMeta[actual.x][actual.y])
                continue;

            Vector2d posActual = new Vector2d(actual.x, actual.y);

            // Transiciones por adyacencia
            for (int i = 0; i < 4; i++) {
                int nx = actual.x + dx[i];
                int ny = actual.y + dy[i];
                if (nx >= 0 && nx < columnas && ny >= 0 && ny < filas) {
                    Vector2d vecina = new Vector2d(nx, ny);
                    boolean esVacia = stateObsActual.getObservationGrid()[nx][ny].isEmpty();

                    // Es muro si está en la lista de árboles o si es agua y no hay ruta de nenúfar
                    boolean esMuro = arboles.contains(vecina) || (agua.contains(vecina) && dirRutaNenufar[nx][ny] == 0);

                    if (!esMuro) {
                        double nuevaDist = actual.dist + 1;
                        if (nuevaDist < distanciasMeta[nx][ny]) {
                            distanciasMeta[nx][ny] = nuevaDist;
                            pq.add(new NodeDist(nx, ny, nuevaDist));
                        }
                    }
                }
            }

            // Transiciones inversas por catapulta (si la catapulta cae aquí, podemos "ir"
            // desde la catapulta hacia aquí)
            for (Vector2d cat : catapultas) {
                if (destinoCatapultas.containsKey(cat)) {
                    Vector2d destino = destinoCatapultas.get(cat);
                    if (destino.x == actual.x && destino.y == actual.y) {
                        // El coste de la catapulta es la distancia de vuelo
                        double distVuelo = Math.abs(cat.x - destino.x) + Math.abs(cat.y - destino.y);
                        double nuevaDist = actual.dist + distVuelo;
                        if (nuevaDist < distanciasMeta[(int) cat.x][(int) cat.y]) {
                            distanciasMeta[(int) cat.x][(int) cat.y] = nuevaDist;
                            pq.add(new NodeDist((int) cat.x, (int) cat.y, nuevaDist));
                        }
                    }
                }
            }
        }
        // Debug: Print distance map to identify disconnection
        /*
         * System.out.println("Distancias Meta:");
         * for (int y = 0; y < filas; y++) {
         * for (int x = 0; x < columnas; x++) {
         * if (distanciasMeta[x][y] == 1000000) System.out.print(" X ");
         * else System.out.print(String.format("%2d ", (int)distanciasMeta[x][y]));
         * }
         * System.out.println();
         * }
         */
    }

    private class NodeDist implements Comparable<NodeDist> {
        int x, y;
        double dist;

        NodeDist(int x, int y, double dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }

        public int compareTo(NodeDist o) {
            return Double.compare(this.dist, o.dist);
        }
    }

    public void obtenerObservacionesAnalizadas(StateObservation stateObs) {
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                ArrayList<Observation> obsEnCelda = grid[x][y];

                if (!obsEnCelda.isEmpty()) {
                    Vector2d celda = new Vector2d(x, y);
                    for (Observation obs : obsEnCelda) {
                        asignarLetraADiccionario(obs);

                        switch (obs.itype) {
                            case 3: // Water
                                agua.add(celda);
                                break;
                            case 0: // Wall
                            case 7: // ForestR
                            case 8: // ForestL
                                arboles.add(celda);
                                break;
                            case 5: // Goal
                                salida = celda;
                                break;
                            case 14:
                            case 15:
                            case 16:
                            case 17: // Catapults
                                catapultas.add(celda);
                                direccionesCatapultas.put(celda, obs.itype);
                                break;
                            case 12: // Landingpad
                                zonaMarron.add(celda);
                                break;
                            case 10:
                            case 11: // Logs
                                nenufar.add(celda);
                                direccionesNenufares.put(celda, obs.itype);
                                break;
                            case 2: // Background
                            case 4: // Floor
                            case 19: // Avatar
                                // Ignore these for mapping, they are traversable or handled elsewhere
                                break;
                        }
                    }
                }
            }
        }
    }

    private void asignarLetraADiccionario(Observation o) {
        String clave = String.valueOf(o.itype);
        if (diccionarioLetras.containsKey(clave))
            return;

        String letra = "?";
        switch (o.itype) {
            case 0:
                letra = "#";
                break; // Wall
            case 2:
                letra = ".";
                break; // Background
            case 3:
                letra = " ";
                break; // Water
            case 4:
                letra = "_";
                break; // Floor
            case 5:
                letra = "S";
                break; // Goal
            case 14:
                letra = "3";
                break; // Catapult Down
            case 15:
                letra = "4";
                break; // Catapult Up
            case 16:
                letra = "1";
                break; // Catapult Right
            case 17:
                letra = "2";
                break; // Catapult Left
            case 7:
                letra = "f";
                break; // forestR
            case 8:
                letra = "l";
                break; // forestL
            case 10:
                letra = "R";
                break; // logR
            case 11:
                letra = "L";
                break; // logL
            case 12:
                letra = "M";
                break; // Landingpad
            case 19:
                letra = "A";
                break; // Avatar
            default:
                System.out.println("Itype desconocido: " + o.itype);
                break;
        }
        diccionarioLetras.put(clave, letra);
    }

    private void pintarTerreno(StateObservation stateObs) {
        boolean[][] alcanzables = new boolean[columnas][filas];
        if (MiPosicion != null) {
            java.util.Queue<Vector2d> q = new java.util.LinkedList<>();
            q.add(MiPosicion);
            alcanzables[(int) MiPosicion.x][(int) MiPosicion.y] = true;

            int[] dx = { 0, 0, 1, -1 };
            int[] dy = { 1, -1, 0, 0 };

            while (!q.isEmpty()) {
                Vector2d actual = q.poll();
                int cx = (int) actual.x;
                int cy = (int) actual.y;

                for (int i = 0; i < 4; i++) {
                    int nx = cx + dx[i];
                    int ny = cy + dy[i];

                    if (nx >= 0 && nx < columnas && ny >= 0 && ny < filas && !alcanzables[nx][ny]) {
                        Vector2d vecina = new Vector2d(nx, ny);
                        boolean esVacia = stateObs.getObservationGrid()[nx][ny].isEmpty();

                        // Es muro si está en la lista de árboles o si es agua y no hay ruta de nenúfar
                        boolean esMuro = arboles.contains(vecina) || (agua.contains(vecina) && dirRutaNenufar[nx][ny] == 0);

                        if (!esMuro) {
                            alcanzables[nx][ny] = true;
                            q.add(vecina);
                        }
                    }
                }

                // Transición por catapultas
                for (Vector2d cat : catapultas) {
                    if ((int) cat.x == cx && (int) cat.y == cy) {
                        if (destinoCatapultas.containsKey(cat)) {
                            Vector2d dest = destinoCatapultas.get(cat);
                            if (dest != null && dest.x >= 0 && dest.x < columnas && dest.y >= 0 && dest.y < filas) {
                                if (!alcanzables[(int) dest.x][(int) dest.y]) {
                                    alcanzables[(int) dest.x][(int) dest.y] = true;
                                    q.add(dest);
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        System.out.println("\n=== MAPA DEL TERRENO ACCESIBLE ===");
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
                if (!alcanzables[x][y]) {
                    System.out.print("  "); // No dibujamos nada para ocultar otras islas/muros
                    continue;
                }

                ArrayList<Observation> obsEnCelda = grid[x][y];
                if (obsEnCelda.isEmpty()) {
                    System.out.print(" ."); // Suelo transitable
                } else {
                    String letraElegida = null;
                    for (Observation o : obsEnCelda) {
                        String clave = String.valueOf(o.itype);
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
            return 10000000;
        if (stateObs.getGameWinner() == ontology.Types.WINNER.PLAYER_WINS)
            return 0;

        tools.Vector2d pos = stateObs.getAvatarPosition();
        int ax = (int) (pos.x / Bloque);
        int ay = (int) (pos.y / Bloque);

        // 1. SI LA META ES ACCESIBLE (vía tierra o ruta actual de troncos)
        if (distanciasMeta != null && ax >= 0 && ax < columnas && ay >= 0 && ay < filas) {
            double dist = distanciasMeta[ax][ay];
            if (dist < 1000000) {
                return dist * 10.0; // Multiplicador bajo para prioridad máxima
            }
        }

        // 2. SI NO ES ACCESIBLE DIRECTAMENTE, BUSCAR CATAPULTAS
        double minDistCatapulta = 1000000;
        if (catapultas != null) {
            for (tools.Vector2d cat : catapultas) {
                double d = Math.abs(cat.x - ax) + Math.abs(cat.y - ay);
                if (d < minDistCatapulta) {
                    minDistCatapulta = d;
                }
            }
        }

        if (minDistCatapulta < 1000000) {
            // Base 2 millones para que sea peor que cualquier camino directo a meta
            return 2000000 + (minDistCatapulta * 10.0);
        }

        // 3. SI NO HAY CATAPULTAS, BUSCAR RUTAS DE NENÚFARES (donde pasan los troncos)
        double minDistRutaLog = 1000000;
        if (dirRutaNenufar != null) {
            for (int x = 0; x < columnas; x++) {
                for (int y = 0; y < filas; y++) {
                    if (dirRutaNenufar[x][y] > 0) {
                        double d = Math.abs(x - ax) + Math.abs(y - ay);
                        if (d < minDistRutaLog) {
                            minDistRutaLog = d;
                        }
                    }
                }
            }
        }

        if (minDistRutaLog < 1000000) {
            // Base 4 millones para que sea la última opción lógica
            return 4000000 + (minDistRutaLog * 10.0);
        }

        // Si no hay absolutamente nada, penalización máxima
        return 6000000;
    }
}