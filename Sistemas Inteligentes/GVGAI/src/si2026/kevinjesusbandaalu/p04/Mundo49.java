package si2026.kevinjesusbandaalu.p04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import core.game.Observation;
import core.game.StateObservation;
import si2026.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo49 implements IMundo {

    public static class Trayectoria {
        public static final int TIPO_CATAPULTA = 1;
        public static final int TIPO_NENUFAR = 2;

        public int tipo;
        public Vector2d origen;
        public Vector2d destino;
        public int direccion;
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
    public HashMap<Vector2d, Integer> tiposSpawners = new HashMap<>();
    public Vector2d salida;

    public HashMap<Vector2d, Integer> direccionesCatapultas;
    public HashMap<Vector2d, Integer> direccionesNenufares;
    public StateObservation stateObsActual;

    public HashMap<Vector2d, Vector2d> destinoCatapultas;

    public HashMap<String, Vector2d> origenCatapulta = new HashMap<>();

    public ArrayList<Trayectoria> trayectorias = new ArrayList<>();
    private HashMap<String, String> diccionarioLetras = new HashMap<>();

    public boolean avatarEnTronco = false;
    public boolean avatarEnVuelo = false;

    public static final int DIR_LIBRE = 0; 
    public static final int DIR_DERECHA = 1; 
    public static final int DIR_IZQUIERDA = 2; 
    public static final int DIR_ARRIBA = 3; 
    public static final int DIR_ABAJO = 4; 

    // Capa 0: suelo firme
    public boolean[][] mapaTransitable;

    // Capa 1: celdas de agua por las que pasan nenúfares/proyectiles de catapulta
    public boolean[][] mapaTrayectoria;

    // Dirección forzada en cada celda: aplica a ambas capas.
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
        mapaDireccion = new int[columnas][filas]; 

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

        for (Vector2d v : suelo)
            mapaTransitable[(int) v.x][(int) v.y] = true;
        for (Vector2d v : zonaMarron)
            mapaTransitable[(int) v.x][(int) v.y] = true;
        if (salida != null)
            mapaTransitable[(int) salida.x][(int) salida.y] = true;

        for (Vector2d cap : catapultas) {
            mapaTransitable[(int) cap.x][(int) cap.y] = true;
            int tipo = direccionesCatapultas.get(cap);
            int dirForzada = tipoCatapultaADir(tipo);
            mapaDireccion[(int) cap.x][(int) cap.y] = dirForzada;
        }

        for (Vector2d sp : spawnsNenufar) {
            int filaSpawn = (int) sp.y;
            int colSpawn = (int) sp.x;

            Integer type = tiposSpawners.get(sp);
            int dirNenufar = (type != null && type == 7) ? DIR_DERECHA : DIR_IZQUIERDA;

            mapaTransitable[colSpawn][filaSpawn] = false;
            mapaTrayectoria[colSpawn][filaSpawn] = true;
            mapaDireccion[colSpawn][filaSpawn] = dirNenufar;
            for (int x = 0; x < columnas; x++) {
                if (esAgua[x][filaSpawn] && !mapaTransitable[x][filaSpawn]) {
                    mapaTrayectoria[x][filaSpawn] = true;
                    mapaDireccion[x][filaSpawn] = dirNenufar;
                }
            }
        }

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

        for (Vector2d arb : arboles) {
            mapaTransitable[(int) arb.x][(int) arb.y] = false;
            mapaTrayectoria[(int) arb.x][(int) arb.y] = false;
            mapaDireccion[(int) arb.x][(int) arb.y] = DIR_LIBRE;
        }

        construirTrayectorias(esArbol, esZonaMarron);
    }

    private void construirTrayectorias(boolean[][] esArbol, boolean[][] esZonaMarron) {
        boolean[][] esCapGrid = new boolean[columnas][filas];
        for (Vector2d c : catapultas)
            esCapGrid[(int) c.x][(int) c.y] = true;

        boolean[][] esSalidaGrid = new boolean[columnas][filas];
        if (salida != null)
            esSalidaGrid[(int) salida.x][(int) salida.y] = true;

        for (Vector2d cap : catapultas) {
            Trayectoria t = new Trayectoria();
            t.tipo = Trayectoria.TIPO_CATAPULTA;
            t.origen = cap;
            Set<String> visitados = new HashSet<>();
            visitados.add((int) cap.x + "," + (int) cap.y);

            int dirActual = mapaDireccion[(int) cap.x][(int) cap.y];
            t.direccion = dirActual; 

            int curX = (int) cap.x, curY = (int) cap.y;
            int dxC = dx(dirActual), dyC = dy(dirActual);
            Vector2d ultimaCeldaValida = cap;
            t.celdas.add(cap);
            curX += dxC;
            curY += dyC;

            while (curX >= 0 && curX < columnas && curY >= 0 && curY < filas) {
                Vector2d curPos = new Vector2d(curX, curY);
                if (esArbol[curX][curY]) {
                    t.destino = ultimaCeldaValida;
                    break;
                }
                if (esZonaMarron[curX][curY] || esSalidaGrid[curX][curY]) {
                    t.destino = curPos;
                    break;
                }

                if (esCapGrid[curX][curY]) {
                    String key = curX + "," + curY;
                    if (!visitados.contains(key)) {
                        visitados.add(key);
                        dirActual = mapaDireccion[curX][curY];
                        dxC = dx(dirActual);
                        dyC = dy(dirActual);
                        t.celdas.add(curPos);
                        ultimaCeldaValida = curPos;
                        curX += dxC;
                        curY += dyC;
                        continue;
                    }
                }

                t.celdas.add(curPos);
                ultimaCeldaValida = curPos;
                curX += dxC;
                curY += dyC;
            }

            if (t.destino == null)
                t.destino = ultimaCeldaValida;

            trayectorias.add(t);
        }

        for (Vector2d sp : spawnsNenufar) {
            int filaSpawn = (int) sp.y;
            int colSpawn = (int) sp.x;
            int dirNenufar = (colSpawn <= columnas / 2) ? DIR_DERECHA : DIR_IZQUIERDA;
            int dxN = (dirNenufar == DIR_DERECHA) ? 1 : -1;

            Trayectoria t = new Trayectoria();
            t.tipo = Trayectoria.TIPO_NENUFAR;
            t.origen = sp;
            t.direccion = dirNenufar;

            int curX = colSpawn + dxN;
            while (curX >= 0 && curX < columnas) {
                if (esArbol[curX][filaSpawn])
                    break;
                if (mapaTransitable[curX][filaSpawn]) {
                    t.destino = new Vector2d(curX, filaSpawn);
                    break;
                }
                if (mapaTrayectoria[curX][filaSpawn]) {
                    t.celdas.add(new Vector2d(curX, filaSpawn));
                }
                curX += dxN;
            }
            trayectorias.add(t);
        }

    }

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


                if (grid[x][y].isEmpty()) {
                    suelo.add(celda);
                    continue;
                }
                for (Observation obs : grid[x][y]) {
                    asignarLetraADiccionario(obs);
                    switch (obs.itype) {
                        case 2:
                        case 3:
                        case 7:
                        case 8:
                            agua.add(celda);
                            if (obs.itype == 7 || obs.itype == 8) {
                                spawnsNenufar.add(celda);
                                tiposSpawners.put(celda, obs.itype);
                            }
                            break;
                        case 4:
                            suelo.add(celda);
                            break;
                        case 0:
                            arboles.add(celda);
                            break; // Wall
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