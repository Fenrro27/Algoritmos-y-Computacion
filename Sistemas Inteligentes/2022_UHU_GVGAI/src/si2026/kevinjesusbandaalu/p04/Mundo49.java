package si2026.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.HashMap;
import core.game.Observation;
import core.game.StateObservation;
import si2026.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;
import ontology.Types;

public class Mundo49 implements IMundo {

    // ═══ Clase que representa una trayectoria (catapulta o nenúfar) ═════════════
    public static class Trayectoria {
        public static final int TIPO_CATAPULTA = 1;
        public static final int TIPO_NENUFAR = 2;

        /** Tipo de vehículo */
        public int tipo;
        /** Celda de capa 0 desde donde se embarca (catapulta o spawn) */
        public Vector2d origen;
        /**
         * Primera celda de capa 0 donde aterriza/se puede saltar; null si termina en
         * muro
         */
        public Vector2d destino;
        /** Dirección de desplazamiento */
        public int direccion;
        /** Celdas intermedias de la trayectoria (capa 1) */
        public java.util.List<Vector2d> celdas = new ArrayList<>();

        @Override
        public String toString() {
            String tipo_s = (tipo == TIPO_CATAPULTA) ? "CAT" : "NEN";
            String dest_s = (destino != null)
                    ? (int) destino.x + "," + (int) destino.y
                    : "muro";
            return tipo_s + " [" + (int) origen.x + "," + (int) origen.y
                    + "]→[" + dest_s + "] celdas=" + celdas.size();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    public Vector2d MiPosicion;
    public Vector2d miOrientacion;
    public int Bloque, columnas, filas;

    public ArrayList<Vector2d> catapultas;
    public ArrayList<Vector2d> nenufar; // landingpad
    public ArrayList<Vector2d> agua; // water
    public ArrayList<Vector2d> suelo; // floor
    public ArrayList<Vector2d> zonaMarron; // floor/walls
    public ArrayList<Vector2d> arboles; // deadly obstacles (trees/walls)
    public ArrayList<Vector2d> spawnsNenufar;
    public Vector2d salida;

    public HashMap<Vector2d, Integer> direccionesCatapultas;
    public HashMap<Vector2d, Integer> direccionesNenufares;
    public StateObservation stateObsActual;

    public HashMap<Vector2d, Vector2d> destinoCatapultas;

    public HashMap<String, Vector2d> origenCatapulta = new HashMap<>();
    /**
     * Capa 2: lista de trayectorias (catapultas + nenúfares) con origen y destino
     */
    public ArrayList<Trayectoria> trayectorias = new ArrayList<>();
    private HashMap<String, String> diccionarioLetras = new HashMap<>();

    public boolean avatarEnTronco = false;
    public boolean avatarEnVuelo = false;

    // Constantes de dirección para mapaDireccion
    public static final int DIR_LIBRE = 0; // Movimiento libre en todas direcciones
    public static final int DIR_DERECHA = 1; // Forzado solo hacia la derecha
    public static final int DIR_IZQUIERDA = 2; // Forzado solo hacia la izquierda
    public static final int DIR_ARRIBA = 3; // Forzado solo hacia arriba
    public static final int DIR_ABAJO = 4; // Forzado solo hacia abajo

    // Capa 0: suelo firme (libre de moverse en todas direcciones)
    // Incluye: suelo, zonaMarron, salida, catapultas y spawns de nenúfares (puntos
    // de embarque)
    public boolean[][] mapaTransitable;

    // Capa 1: celdas de agua por las que pasan nenúfares/proyectiles de catapulta
    // NUNCA sobreescribe una celda que ya está en mapaTransitable (capa 0 tiene
    // prioridad)
    public boolean[][] mapaTrayectoria;

    // Dirección forzada en cada celda: aplica a ambas capas.
    // Catapultas y spawns (capa 0) también tienen dirección forzada.
    public int[][] mapaDireccion;

    public Mundo49(StateObservation stateObs) {
        Bloque = stateObs.getBlockSize();
        columnas = stateObs.getWorldDimension().width / Bloque;
        filas = stateObs.getWorldDimension().height / Bloque;

        catapultas = new ArrayList<>();
        nenufar = new ArrayList<>();
        agua = new ArrayList<>();
        suelo = new ArrayList<>();
        zonaMarron = new ArrayList<>();
        arboles = new ArrayList<>();
        spawnsNenufar = new ArrayList<>();
        direccionesCatapultas = new HashMap<>();
        direccionesNenufares = new HashMap<>();

        AnalizarEntorno(stateObs);
    }

    @Override
    public void AnalizarEntorno(StateObservation stateObs) {
        this.stateObsActual = stateObs;
        limpiarListas();

        Vector2d pos = stateObs.getAvatarPosition();
        MiPosicion = new Vector2d(pos.x / Bloque, pos.y / Bloque);
        miOrientacion = stateObs.getAvatarOrientation();

        obtenerObservacionesAnalizadas(stateObs);

        mapaTransitable = new boolean[columnas][filas];
        mapaTrayectoria = new boolean[columnas][filas];
        mapaDireccion = new int[columnas][filas]; // 0 = DIR_LIBRE por defecto

        generarMapaAStar();

    }

    private void limpiarListas() {
        catapultas.clear();
        nenufar.clear();
        agua.clear();
        suelo.clear();
        zonaMarron.clear();
        arboles.clear();
        spawnsNenufar.clear();
        direccionesCatapultas.clear();
        direccionesNenufares.clear();
        origenCatapulta.clear();
        trayectorias.clear();
        salida = null;

    }

    private void generarMapaAStar() {
        // Grids booleanos para búsqueda O(1) por coordenada.
        // NOTA: Vector2d de GVGAI NO sobreescribe equals/hashCode,
        // por lo que HashSet<Vector2d> no funciona. Usamos boolean[][].
        boolean[][] esArbol = new boolean[columnas][filas];
        boolean[][] esSuelo = new boolean[columnas][filas];
        boolean[][] esZonaMarron = new boolean[columnas][filas];
        boolean[][] esAgua = new boolean[columnas][filas];

        for (Vector2d v : arboles)
            esArbol[(int) v.x][(int) v.y] = true;
        for (Vector2d v : suelo)
            esSuelo[(int) v.x][(int) v.y] = true;
        for (Vector2d v : zonaMarron)
            esZonaMarron[(int) v.x][(int) v.y] = true;
        for (Vector2d v : agua)
            esAgua[(int) v.x][(int) v.y] = true;

        // ── CAPA 0: suelo firme ─────────────────────────────────────────────────
        // Suelo, zonaMarron y salida → transitables sin restricción de dirección.
        for (Vector2d v : suelo)
            mapaTransitable[(int) v.x][(int) v.y] = true;
        for (Vector2d v : zonaMarron)
            mapaTransitable[(int) v.x][(int) v.y] = true;
        if (salida != null)
            mapaTransitable[(int) salida.x][(int) salida.y] = true;

        // Catapultas → en capa 0 (el avatar puede caminar hasta ellas) +
        // dirección forzada en mapaDireccion (al pararse encima se lanza).
        for (Vector2d cap : catapultas) {
            mapaTransitable[(int) cap.x][(int) cap.y] = true;
            int tipo = direccionesCatapultas.get(cap);
            int dirForzada = tipoCatapultaADir(tipo);
            mapaDireccion[(int) cap.x][(int) cap.y] = dirForzada;
        }

        // Spawns de nenúfares → en capa 0 (punto de embarque) +
        // dirección forzada (el nenúfar pasa moviéndose en esa dirección).
        for (Vector2d sp : spawnsNenufar) {
            int filaSpawn = (int) sp.y;
            int colSpawn = (int) sp.x;

            int dirNenufar = (colSpawn == 0 || colSpawn <= columnas / 2)
                    ? DIR_DERECHA
                    : DIR_IZQUIERDA;

            // El spawn está en capa 0 (el avatar puede llegar aquí caminando).
            mapaTransitable[colSpawn][filaSpawn] = true;
            mapaDireccion[colSpawn][filaSpawn] = dirNenufar;

            // Expandir la trayectoria del nenúfar en CAPA 1 (solo agua).
            for (int x = 0; x < columnas; x++) {
                if (esAgua[x][filaSpawn] && !mapaTransitable[x][filaSpawn]) {
                    mapaTrayectoria[x][filaSpawn] = true;
                    mapaDireccion[x][filaSpawn] = dirNenufar;
                }
            }
        }

        // REFUERZO: Si vemos nenúfares (logs) moviéndose, marcar su trayectoria
        // aunque no hayamos detectado el spawn (útil si el spawn está oculto o es
        // inusual).
        for (Vector2d n : nenufar) {
            int ny = (int) n.y;
            Integer itype = direccionesNenufares.get(n);
            if (itype == null)
                continue;
            int dir = (itype == 10) ? DIR_DERECHA : DIR_IZQUIERDA;
            for (int x = 0; x < columnas; x++) {
                if (esAgua[x][ny] && !mapaTransitable[x][ny]) {
                    mapaTrayectoria[x][ny] = true;
                    mapaDireccion[x][ny] = dir;
                }
            }
        }

        // ── SIEMPRE: los árboles bloquean ambas capas ───────────────────────────
        for (Vector2d arb : arboles) {
            mapaTransitable[(int) arb.x][(int) arb.y] = false;
            mapaTrayectoria[(int) arb.x][(int) arb.y] = false;
            mapaDireccion[(int) arb.x][(int) arb.y] = DIR_LIBRE;
        }

        // ── CAPA 2: construir lista de trayectorias ──────────────────────────────
        construirTrayectorias(esArbol, esZonaMarron);
    }

    /**
     * Construye la lista de objetos Trayectoria.
     * Reglas de parada para catapultas: muro (esArbol) o zonaMarron (landingpad).
     * El suelo normal no detiene al avatar lanzado por catapulta.
     */
    private void construirTrayectorias(boolean[][] esArbol, boolean[][] esZonaMarron) {
        // Grids auxiliares para detección de aterrizajes
        boolean[][] esCapGrid = new boolean[columnas][filas];
        for (Vector2d c : catapultas)
            esCapGrid[(int) c.x][(int) c.y] = true;

        boolean[][] esSalidaGrid = new boolean[columnas][filas];
        if (salida != null)
            esSalidaGrid[(int) salida.x][(int) salida.y] = true;

        // ─ Catapultas ───────────────────────────────────────────────────────
        for (Vector2d cap : catapultas) {
            int tipo = direccionesCatapultas.get(cap);
            int dirForzada = tipoCatapultaADir(tipo);
            int dxC = dx(dirForzada), dyC = dy(dirForzada);

            Trayectoria t = new Trayectoria();
            t.tipo = Trayectoria.TIPO_CATAPULTA;
            t.origen = cap;
            t.direccion = dirForzada;

            int curX = (int) cap.x + dxC, curY = (int) cap.y + dyC;
            Vector2d ultimaCeldaVuelo = cap;

            while (curX >= 0 && curX < columnas && curY >= 0 && curY < filas) {
                if (esArbol[curX][curY]) {
                    t.destino = ultimaCeldaVuelo;
                    break;
                }
                if (esZonaMarron[curX][curY] || esCapGrid[curX][curY] || esSalidaGrid[curX][curY]) {
                    // Aterriza en zona marrón, sobre OTRA catapulta o en la SALIDA
                    t.destino = new Vector2d(curX, curY);
                    break;
                }
                // Sigue volando (no marcamos mapaTrayectoria aquí para evitar confusiones en
                // A*)
                t.celdas.add(new Vector2d(curX, curY));
                ultimaCeldaVuelo = new Vector2d(curX, curY);
                curX += dxC;
                curY += dyC;
            }
            if (t.destino == null)
                t.destino = ultimaCeldaVuelo;
            trayectorias.add(t);
        }

        // ─ Nenúfares ─────────────────────────────────────────────────────────
        for (Vector2d sp : spawnsNenufar) {
            int filaSpawn = (int) sp.y;
            int colSpawn = (int) sp.x;
            int dirNenufar = (colSpawn <= columnas / 2) ? DIR_DERECHA : DIR_IZQUIERDA;
            int dxN = (dirNenufar == DIR_DERECHA) ? 1 : -1;

            Trayectoria t = new Trayectoria();
            t.tipo = Trayectoria.TIPO_NENUFAR;
            t.origen = sp; // el spawn es el punto de embarque principal
            t.direccion = dirNenufar;

            // Recorrer en la dirección de movimiento del nenúfar
            int curX = colSpawn + dxN;
            while (curX >= 0 && curX < columnas) {
                if (esArbol[curX][filaSpawn])
                    break;
                if (mapaTransitable[curX][filaSpawn]) {
                    t.destino = new Vector2d(curX, filaSpawn); // llegada a tierra firme
                    break;
                }
                if (mapaTrayectoria[curX][filaSpawn]) {
                    t.celdas.add(new Vector2d(curX, filaSpawn));
                }
                curX += dxN;
            }
            if (!t.celdas.isEmpty())
                trayectorias.add(t);
        }

        // Debug: imprimir trayectorias (sólo la primera vez o en cambios)
        /*
         * System.out.println("[TRAYECTORIAS] Total=" + trayectorias.size());
         * for (Trayectoria t : trayectorias)
         * System.out.println("  " + t);
         */
    }

    /** Convierte el itype de una catapulta a la constante de dirección. */
    private int tipoCatapultaADir(int tipo) {
        switch (tipo) {
            case 14:
                return DIR_ABAJO;
            case 15:
                return DIR_ARRIBA;
            case 16:
                return DIR_DERECHA;
            case 17:
                return DIR_IZQUIERDA;
            default:
                return DIR_LIBRE;
        }
    }

    private int dx(int dir) {
        if (dir == DIR_DERECHA)
            return 1;
        if (dir == DIR_IZQUIERDA)
            return -1;
        return 0;
    }

    private int dy(int dir) {
        if (dir == DIR_ABAJO)
            return 1;
        if (dir == DIR_ARRIBA)
            return -1;
        return 0;
    }

    public void obtenerObservacionesAnalizadas(StateObservation stateObs) {
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Vector2d celda = new Vector2d(x, y);

                // Si la celda no tiene ninguna observación, suelo transitable
                if (grid[x][y].isEmpty()) {
                    suelo.add(celda);
                    continue;
                }

                for (Observation obs : grid[x][y]) {
                    asignarLetraADiccionario(obs);
                    switch (obs.itype) {
                        case 3:
                            agua.add(celda);
                            break;
                        case 4:
                            suelo.add(celda);
                            break;
                        case 0:
                            arboles.add(celda);
                            break; // Wall
                        case 7:
                            spawnsNenufar.add(celda);
                            break; // Spawn nenúfar izquierda
                        case 8:
                            spawnsNenufar.add(celda);
                            break; // Spawn nenúfar derecha
                        case 5:
                            salida = celda;
                            break;
                        case 14:
                        case 15:
                        case 16:
                        case 17: // Catapultas
                            catapultas.add(celda);
                            direccionesCatapultas.put(celda, obs.itype);
                            break;
                        case 12:
                            zonaMarron.add(celda);
                            break; // Landingpad
                        case 10:
                        case 11: // Logs
                            nenufar.add(celda);
                            direccionesNenufares.put(celda, obs.itype);
                            break;
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
                // System.out.println("Itype desconocido: " + o.itype);
                break;
        }
        diccionarioLetras.put(clave, letra);
    }

}