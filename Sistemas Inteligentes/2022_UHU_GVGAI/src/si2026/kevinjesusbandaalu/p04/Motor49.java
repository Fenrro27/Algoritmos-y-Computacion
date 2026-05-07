package si2026.kevinjesusbandaalu.p04;

import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import core.game.Observation;
import java.util.*;

public class Motor49 {

    private boolean debugActivo = false;

    private final double VEL_NENUFAR = 0.1;
    private final double VEL_MURCIELAGO = 1.0;

    private Vector2d objetivoFijo = null; 
    private boolean objetivoEsCatapulta = false;

    private static class AN {
        Vector2d pos;
        AN padre;
        double g, h;
        AN(Vector2d p, AN padre, double g, double h) {
            this.pos = p; this.padre = padre; this.g = g; this.h = h;
        }
        double f() { return g + h; }
    }

    public Motor49(Mundo49 mundo) {}

    public ACTIONS buscar(Mundo49 mundo, ElapsedCpuTimer timer) {
        if (debugActivo) mostrarMapaAStar(mundo);

        if (mundo.salida == null) return ACTIONS.ACTION_NIL;
        int sx = (int) mundo.salida.x, sy = (int) mundo.salida.y;

        int ax = (int) mundo.MiPosicion.x, ay = (int) mundo.MiPosicion.y;
        if (ax == sx && ay == sy)
            return ACTIONS.ACTION_NIL;

        List<Vector2d> caminoCapa0 = aStar(mundo.MiPosicion, mundo.salida, mundo, false, timer);
        if (caminoCapa0 != null && caminoCapa0.size() >= 2) {
            objetivoFijo = null; 
            ACTIONS acc = traducirDireccion(caminoCapa0.get(0), caminoCapa0.get(1));
            return acc;
        }

        if (objetivoFijo != null) {
            boolean enObjetivo = igualPos(mundo.MiPosicion, objetivoFijo);
            if (debugActivo) {
                System.out.println("[DEBUG] MiPos=" + (int)mundo.MiPosicion.x + "," + (int)mundo.MiPosicion.y 
                    + " Obj=" + (int)objetivoFijo.x + "," + (int)objetivoFijo.y 
                    + " igual=" + enObjetivo);
            }

            if (enObjetivo) {
                if (!objetivoEsCatapulta) {
                    int wx = (int) objetivoFijo.x, wy = (int) objetivoFijo.y;
                    
                    Mundo49.Trayectoria tray = buscarTrayectoriaAdyacente(wx, wy, mundo);
                    if (tray != null) {
                        List<Vector2d> caminoSync = aStar(mundo.MiPosicion, mundo.salida, mundo, true, timer);
                        int[] info = analizarTramoAgua(caminoSync, 0, mundo);
                        int nDirs = info[1];
                        
                        boolean jumpPossible;
                        if (nDirs > 1) {
                            jumpPossible =  verificarSincronizacionFilas(caminoSync, 0, mundo);
                        } else {
                            jumpPossible = trayectoriaAlineadaConPrediccion(tray, mundo);
                        }

                       // String modo = (nDirs > 1) ? "SINCRO" : (info[0] > 1 ? "PUENTE" : "INDIVIDUAL");
                        if (jumpPossible) {
                           // System.out.println("[LOG] Cruce permitido (Modo: " + modo + ") hacia " + (int)tray.origen.x + "," + (int)tray.origen.y);
                            objetivoFijo = null;
                        } else {
                            if (debugActivo || nDirs > 1) {
                              //  System.out.println("[DEBUG] Bloqueado en orilla (Modo: " + modo + ") | Ready=" + jumpPossible);
                            }
                        }
                    }
                } else {
                    objetivoFijo = null; 
                }
            } else {
                ACTIONS acc = navegarA(objetivoFijo, mundo, timer);
                return acc;
            }
        }

        return estrategiaEscape(mundo, timer);
    }

    private List<Vector2d> aStar(Vector2d inicio, Vector2d fin,
            Mundo49 mundo, boolean usarTrayectoria, ElapsedCpuTimer timer) {
        if (fin == null) return null;

        PriorityQueue<AN> open = new PriorityQueue<>(Comparator.comparingDouble(AN::f));
        HashMap<String, Double> gScore = new HashMap<>();

        String ki = k(inicio);
        gScore.put(ki, 0.0);
        open.add(new AN(inicio, null, 0, dist(inicio, fin)));

        while (!open.isEmpty()) {
            if (timer.remainingTimeMillis() < 5) return null;

            AN cur = open.poll();
            String ck = k(cur.pos);

            if (cur.g > gScore.getOrDefault(ck, Double.MAX_VALUE) + 0.001) continue;

            if (igualPos(cur.pos, fin)) {
                return reconstruirCamino(cur);
            }

            for (Vector2d v : vecinos(cur.pos, mundo, usarTrayectoria)) {
                String vk = k(v);
                
                // Cálculo de coste: 
                // 1.0 estándar (suelo)
                // 0.5 si es trayectoria alineada (atractivo)
                // 5.0 si es trayectoria no alineada (penaliza espera)
                double cost = 1.0;
                
                if (esCatapulta((int)cur.pos.x, (int)cur.pos.y, mundo)) {
                    Mundo49.Trayectoria t = buscarTrayectoriaPorOrigen((int)cur.pos.x, (int)cur.pos.y, mundo);
                    if (t != null && t.destino != null && igualPos(v, t.destino)) {
                        cost = dist(cur.pos, v) + 2.0;
                    }
                } else if (!mundo.mapaTransitable[(int)v.x][(int)v.y]) {
                    Mundo49.Trayectoria t = buscarTrayectoria((int)v.x, (int)v.y, mundo);
                    if (t != null && trayectoriaAlineadaConPrediccion(t, mundo)) {
                        cost = 0.5;
                    } else {
                        cost = 4.0;
                    }
                }
                
                double ng = cur.g + cost;
                if (ng < gScore.getOrDefault(vk, Double.MAX_VALUE)) {
                    gScore.put(vk, ng);
                    open.add(new AN(v, cur, ng, dist(v, fin)));
                }
            }
        }
        return null;
    }

    private ACTIONS estrategiaEscape(Mundo49 mundo, ElapsedCpuTimer timer) {
        List<Vector2d> camino = aStar(mundo.MiPosicion, mundo.salida, mundo, true, timer);
        if (camino == null || camino.size() < 2) {
            return ACTIONS.ACTION_NIL;
        }

        Vector2d proximo = camino.get(1);
        if (debugActivo) {
            System.out.println("[DEBUG-ESCAPE] Path from " + (int)mundo.MiPosicion.x + "," + (int)mundo.MiPosicion.y 
                + " to " + (int)proximo.x + "," + (int)proximo.y 
                + " | Transitable=" + mundo.mapaTransitable[(int)proximo.x][(int)proximo.y] 
                + " | Trayectoria=" + mundo.mapaTrayectoria[(int)proximo.x][(int)proximo.y]);
        }

        int ax = (int) mundo.MiPosicion.x, ay = (int) mundo.MiPosicion.y;
        if (!mundo.mapaTransitable[ax][ay]) {
            Vector2d sig = camino.get(1);
            int sx = (int) sig.x, sy = (int) sig.y;
            
            if (mundo.mapaTrayectoria[sx][sy] && !mundo.mapaTransitable[sx][sy]) {
                Mundo49.Trayectoria t = buscarTrayectoria(sx, sy, mundo);
                if (trayectoriaAlineadaConPrediccion(t, mundo) || hayNenufarEn(sx, sy, mundo)) {
                   // System.out.println("[LOG] Cruzando de nenúfar a nenúfar en " + sx + "," + sy + (trayectoriaAlineadaConPrediccion(t, mundo) ? " (ALINEADO)" : ""));
                    return traducirDireccion(mundo.MiPosicion, sig);
                } else {
                    return ACTIONS.ACTION_NIL;
                }
            } else {
                return traducirDireccion(mundo.MiPosicion, sig);
            }
        }

        for (int i = 1; i < camino.size(); i++) {
            Vector2d p = camino.get(i);
            int cx = (int) p.x, cy = (int) p.y;

            if (mundo.mapaTrayectoria[cx][cy] && !mundo.mapaTransitable[cx][cy]) {
                Mundo49.Trayectoria tray = buscarTrayectoria(cx, cy, mundo);
                if (tray == null) {
                    return ACTIONS.ACTION_NIL;
                }

                if (tray.tipo == Mundo49.Trayectoria.TIPO_CATAPULTA) {
                    objetivoFijo = tray.origen;
                    objetivoEsCatapulta = true;
                    return navegarA(tray.origen, mundo, timer);
                } else {
                    Vector2d espera = camino.get(i-1);
                    Vector2d celdaTray = camino.get(i);
                    
                    int[] info = analizarTramoAgua(camino, i, mundo);
                    int nDirs = info[1];

                    boolean jumpPossible;
                    if (nDirs > 1) {
                        // MODO SINCRONIZACIÓN: Direcciones opuestas -> Esperar a spawns activos
                        jumpPossible = verificarSincronizacionFilas(camino, i, mundo);
                    } else {
                        // MODO PUENTE: Misma dirección -> Esperar a trayectoria alineada
                        jumpPossible = trayectoriaAlineadaConPrediccion(tray, mundo);
                    }

                    String modo = (nDirs > 1) ? "SINCRO" : (info[0] > 1 ? "PUENTE" : "INDIVIDUAL");
                    if (igualPos(mundo.MiPosicion, espera)) {
                        if (jumpPossible) {
                           // System.out.println("[LOG] Avance permitido (Modo: " + modo + ") hacia " + (int)celdaTray.x + "," + (int)celdaTray.y);
                            objetivoFijo = null;
                            return traducirDireccion(espera, celdaTray);
                        } else {
                            if (debugActivo || nDirs > 1) {
                               // System.out.println("[DEBUG] Esperando en orilla (Modo: " + modo + ") | Ready=" + jumpPossible);
                            }
                        }
                        objetivoFijo = espera;
                        objetivoEsCatapulta = false;
                        return ACTIONS.ACTION_NIL;
                    }

                    objetivoFijo = espera;
                    objetivoEsCatapulta = false;
                    return navegarA(espera, mundo, timer);
                }
            }
        }
        // Si llegamos aquí y el siguiente paso es agua, BLOQUEAMOS. 
        if (!mundo.mapaTransitable[(int)proximo.x][(int)proximo.y]) {
            return ACTIONS.ACTION_NIL;
        }

        return traducirDireccion(camino.get(0), proximo);
    }

    private Mundo49.Trayectoria buscarTrayectoria(int cx, int cy, Mundo49 mundo) {
        for (Mundo49.Trayectoria t : mundo.trayectorias) {
            if ((int)t.origen.x == cx && (int)t.origen.y == cy) return t;
            if (t.destino != null && (int)t.destino.x == cx && (int)t.destino.y == cy) return t;
            for (Vector2d c : t.celdas)
                if ((int)c.x == cx && (int)c.y == cy) return t;
        }
        return null;
    }

    private ACTIONS navegarA(Vector2d destino, Mundo49 mundo, ElapsedCpuTimer timer) {
        List<Vector2d> cam = aStar(mundo.MiPosicion, destino, mundo, false, timer);
        if (cam != null && cam.size() >= 2) return traducirDireccion(cam.get(0), cam.get(1));
        return ACTIONS.ACTION_NIL;
    }

    private List<Vector2d> vecinos(Vector2d pos, Mundo49 mundo, boolean usarTray) {
        int x = (int) pos.x, y = (int) pos.y;
        int dir = mundo.mapaDireccion[x][y];
        boolean enNenufar = mundo.mapaTrayectoria[x][y] && !mundo.mapaTransitable[x][y];
        List<Vector2d> result = new ArrayList<>();

        if (enNenufar) {
            int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : dirs4) {
                int nx = x + d[0], ny = y + d[1];
                if (nx < 0 || nx >= mundo.columnas || ny < 0 || ny >= mundo.filas) continue;
                
                if (mundo.mapaTransitable[nx][ny]) {
                    if (esCatapulta(nx, ny, mundo)) {
                        if (!usarTray) continue;
                        Mundo49.Trayectoria t = buscarTrayectoriaPorOrigen(nx, ny, mundo);
                        if (t == null || t.destino == null || !mundo.mapaTransitable[(int)t.destino.x][(int)t.destino.y] || !esVueloSeguro(t, mundo)) {
                            continue;
                        }
                    }
                    result.add(new Vector2d(nx, ny));
                } else if (usarTray && mundo.mapaTrayectoria[nx][ny]) {
                    result.add(new Vector2d(nx, ny));
                }
            }
        } else if (dir != Mundo49.DIR_LIBRE) {
            if (esCatapulta(x, y, mundo)) {
                Mundo49.Trayectoria t = buscarTrayectoriaPorOrigen(x, y, mundo);
                if (t != null && t.destino != null && esVueloSeguro(t, mundo)) {
                    int tx = (int)t.destino.x, ty = (int)t.destino.y;
                    if (mundo.mapaTransitable[tx][ty] || (usarTray && mundo.mapaTrayectoria[tx][ty])) {
                        result.add(t.destino);
                    }
                }
            } else {
                int nx = x + dxDir(dir), ny = y + dyDir(dir);
                if (nx >= 0 && nx < mundo.columnas && ny >= 0 && ny < mundo.filas) {
                    if (mundo.mapaTransitable[nx][ny]) {
                        boolean ok = true;
                        if (esCatapulta(nx, ny, mundo)) {
                            if (!usarTray) ok = false;
                            else {
                                Mundo49.Trayectoria t = buscarTrayectoriaPorOrigen(nx, ny, mundo);
                                if (t == null || t.destino == null || !mundo.mapaTransitable[(int)t.destino.x][(int)t.destino.y] || !esVueloSeguro(t, mundo)) {
                                    ok = false;
                                }
                            }
                        }
                        if (ok) result.add(new Vector2d(nx, ny));
                    } else if (usarTray && mundo.mapaTrayectoria[nx][ny]) {
                        result.add(new Vector2d(nx, ny));
                    }
                }
            }
        } else {
            int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : dirs4) {
                int nx = x + d[0], ny = y + d[1];
                if (nx < 0 || nx >= mundo.columnas || ny < 0 || ny >= mundo.filas) continue;
                
                if (mundo.mapaTransitable[nx][ny]) {
                    if (esCatapulta(nx, ny, mundo)) {
                        if (!usarTray) continue; // Muro en modo navegación
                        Mundo49.Trayectoria t = buscarTrayectoriaPorOrigen(nx, ny, mundo);
                        if (t == null || t.destino == null || !mundo.mapaTransitable[(int)t.destino.x][(int)t.destino.y] || !esVueloSeguro(t, mundo)) {
                            continue; // Muro si el salto es inseguro o el vuelo es peligroso
                        }
                    }
                    result.add(new Vector2d(nx, ny));
                } else if (usarTray && mundo.mapaTrayectoria[nx][ny]) {
                    Mundo49.Trayectoria t = buscarTrayectoria(nx, ny, mundo);
                    if (t != null && t.tipo == Mundo49.Trayectoria.TIPO_NENUFAR) {
                        result.add(new Vector2d(nx, ny));
                    }
                }
            }
        }
        return result;
    }

    private Mundo49.Trayectoria buscarTrayectoriaPorOrigen(int x, int y, Mundo49 mundo) {
        for (Mundo49.Trayectoria t : mundo.trayectorias)
            if ((int)t.origen.x == x && (int)t.origen.y == y) return t;
        return null;
    }

    private List<Vector2d> reconstruirCamino(AN nodo) {
        LinkedList<Vector2d> camino = new LinkedList<>();
        AN n = nodo;
        while (n != null) { camino.addFirst(n.pos); n = n.padre; }
        return camino;
    }

    private boolean esCatapulta(int x, int y, Mundo49 mundo) {
        for (Vector2d cap : mundo.catapultas)
            if ((int)cap.x == x && (int)cap.y == y) return true;
        return false;
    }

    private boolean hayNenufarEn(int x, int y, Mundo49 mundo) {
        ArrayList<Observation>[][] grid = mundo.stateObsActual.getObservationGrid();
        int dirFlujo = mundo.mapaDireccion[x][y];
        
        for (int dx = -1; dx <= 1; dx++) {
            int nx = x + dx;
            if (nx < 0 || nx >= grid.length || y < 0 || y >= grid[0].length) continue;
            
            for (Observation obs : grid[nx][y]) {
                if (obs.itype == 10 || obs.itype == 11) {
                    double realX = obs.position.x / mundo.Bloque;
                    double realY = obs.position.y / mundo.Bloque;
                    
                    double diffX = realX - x;
                    double diffY = realY - y;

                    if (Math.abs(diffX) > 0.4 || Math.abs(diffY) > 0.4) continue;
                    
                    if (dirFlujo == Mundo49.DIR_DERECHA) {
                        if (diffX > 0.15) continue; 
                    } else if (dirFlujo == Mundo49.DIR_IZQUIERDA) {
                        if (diffX < -0.15) continue;
                    }

                    return true;
                }
            }
        }
        return false;
    }

    private Mundo49.Trayectoria buscarTrayectoriaAdyacente(int x, int y, Mundo49 mundo) {
        int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : dirs4) {
            int nx = x + d[0], ny = y + d[1];
            if (nx < 0 || nx >= mundo.columnas || ny < 0 || ny >= mundo.filas) continue;
            if (mundo.mapaTrayectoria[nx][ny] && !mundo.mapaTransitable[nx][ny]) {
                Mundo49.Trayectoria t = buscarTrayectoria(nx, ny, mundo);
                if (t != null) return t;
            }
        }
        return null;
    }

    private boolean igualPos(Vector2d a, Vector2d b) {
        return (int)a.x == (int)b.x && (int)a.y == (int)b.y;
    }

    private String k(Vector2d v) { return (int)v.x + "," + (int)v.y; }

    private double dist(Vector2d a, Vector2d b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Manhattan
    }

    private int dxDir(int dir) {
        if (dir == Mundo49.DIR_DERECHA)   return  1;
        if (dir == Mundo49.DIR_IZQUIERDA) return -1;
        return 0;
    }
    private int dyDir(int dir) {
        if (dir == Mundo49.DIR_ABAJO)  return  1;
        if (dir == Mundo49.DIR_ARRIBA) return -1;
        return 0;
    }



    private ACTIONS traducirDireccion(Vector2d actual, Vector2d siguiente) {
        if (actual == null || siguiente == null) return ACTIONS.ACTION_NIL;
        int dx = (int)siguiente.x - (int)actual.x;
        int dy = (int)siguiente.y - (int)actual.y;
        ACTIONS acc = ACTIONS.ACTION_NIL;
        if      (dx ==  1) acc = ACTIONS.ACTION_RIGHT;
        else if (dx == -1) acc = ACTIONS.ACTION_LEFT;
        else if (dy ==  1) acc = ACTIONS.ACTION_DOWN;
        else if (dy == -1) acc = ACTIONS.ACTION_UP;
     //   System.out.println("Accion: " + acc);
        return acc;
    }

    private void mostrarMapaAStar(Mundo49 mundo) {
        System.out.println("=== DEBUG MAPA A* (Y:Avatar S:Salida | Capa0: T D I A B | Capa1: d i a b | .:Bloqueado) ===");
        for (int y = 0; y < mundo.filas; y++) {
            StringBuilder fila = new StringBuilder();
            for (int x = 0; x < mundo.columnas; x++) {
                if ((int)mundo.MiPosicion.x == x && (int)mundo.MiPosicion.y == y) {
                    fila.append("Y ");
                } else if (mundo.salida != null
                        && (int)mundo.salida.x == x && (int)mundo.salida.y == y) {
                    fila.append("S ");
                } else if (mundo.mapaTransitable[x][y]) {
                    int dir = mundo.mapaDireccion[x][y];
                    switch (dir) {
                        case Mundo49.DIR_DERECHA:   fila.append("D "); break;
                        case Mundo49.DIR_IZQUIERDA: fila.append("I "); break;
                        case Mundo49.DIR_ARRIBA:    fila.append("A "); break;
                        case Mundo49.DIR_ABAJO:     fila.append("B "); break;
                        default:                    fila.append("T "); break;
                    }
                } else if (mundo.mapaTrayectoria[x][y]) {
                    int dir = mundo.mapaDireccion[x][y];
                    switch (dir) {
                        case Mundo49.DIR_DERECHA:   fila.append("d "); break;
                        case Mundo49.DIR_IZQUIERDA: fila.append("i "); break;
                        case Mundo49.DIR_ARRIBA:    fila.append("a "); break;
                        case Mundo49.DIR_ABAJO:     fila.append("b "); break;
                        default:                    fila.append("? "); break;
                    }
                } else {
                    fila.append(". ");
                }
            }
            System.out.println(fila.toString());
        }
        System.out.println("=====================================================================");
    }



private boolean esVueloSeguro(Mundo49.Trayectoria tray, Mundo49 mundo) {
    if (tray == null || tray.destino == null) return false;

    for (int i = 0; i < tray.celdas.size(); i++) {
        Vector2d celdaVuelo = tray.celdas.get(i);
        double ticksHastaCelda = (i + 1) / VEL_MURCIELAGO;

        if (predecirObstaculoEn(celdaVuelo, ticksHastaCelda, mundo)) {
            return false;
        }
    }
    return true;
}


private int[] analizarTramoAgua(List<Vector2d> camino, int startIndex, Mundo49 mundo) {
    if (camino == null || startIndex < 0 || startIndex >= camino.size()) {
        return new int[]{0, 0};
    }

    Set<Integer> filas = new LinkedHashSet<>();
    Set<Integer> dirs = new HashSet<>();

    boolean enTramo = false;

    for (int i = startIndex; i < camino.size(); i++) {
        Vector2d p = camino.get(i);
        int x = (int) p.x;
        int y = (int) p.y;

        // Buscar si hay spawner en esta fila
        Vector2d spawn = null;
        int dirSpawner = 0;

        for (Vector2d sp : mundo.spawnsNenufar) {
            if ((int) sp.y == y) {
                spawn = sp;
                Integer type = mundo.tiposSpawners.get(sp);
                dirSpawner = (type != null && type == 7)
                        ? Mundo49.DIR_DERECHA
                        : Mundo49.DIR_IZQUIERDA;
                break;
            }
        }

        boolean esAguaReal = (!mundo.mapaTransitable[x][y] && mundo.mapaTrayectoria[x][y]);

        if (spawn != null && esAguaReal) {
            enTramo = true;
            filas.add(y);
            dirs.add(dirSpawner);
        } 
        else if (enTramo) {
            // Solo salimos cuando ya hemos entrado y dejamos de ver agua real
            break;
        }
    }

    if (filas.size() > 1) {
       // System.out.println("[DEBUG] Tramo agua real: filas=" + filas + " dirs=" + dirs.size());
    }

    return new int[]{filas.size(), dirs.size()};
}

private boolean predecirObstaculoEn(Vector2d pos, double ticks, Mundo49 mundo) {
    ArrayList<Observation>[][] grid = mundo.stateObsActual.getObservationGrid();
    int x = (int) pos.x;
    int y = (int) pos.y;

    for (int nx = 0; nx < mundo.columnas; nx++) {
        for (Observation obs : grid[nx][y]) {
            if (obs.itype == 10 || obs.itype == 11) {
                double realX = obs.position.x / mundo.Bloque;
                int dir = mundo.mapaDireccion[(int)realX][y];
                
                double movX = (dir == Mundo49.DIR_DERECHA ? 1 : (dir == Mundo49.DIR_IZQUIERDA ? -1 : 0));
                double futuraX = realX + (movX * VEL_NENUFAR * ticks);

                if (Math.abs(futuraX - x) < 0.4) return true;
            }
        }
    }
    return false;
}
private boolean trayectoriaAlineadaConPrediccion(Mundo49.Trayectoria tray, Mundo49 mundo) {
    if (tray == null || tray.celdas.isEmpty()) return false;

    for (int i = 0; i < tray.celdas.size(); i++) {
        Vector2d celda = tray.celdas.get(i);
        if (!mundo.mapaTransitable[(int)celda.x][(int)celda.y]) {
            // Asumimos que el avatar tarda 1 tick por salto
            double ticksHastaSalto = (i + 1); 
            
            if (!predecirObstaculoEn(celda, ticksHastaSalto, mundo)) {
                return false; 
            }
        }
    }
    return true;
}

/**
 * Verifica si un tramo de agua (nenúfares) tiene varias filas con direcciones opuestas
 * y, en ese caso, si todas las filas involucradas tienen al menos un nenúfar activo.
 */
private boolean verificarSincronizacionFilas(List<Vector2d> camino, int startIndex, Mundo49 mundo) {
    if (camino == null || startIndex < 0 || startIndex >= camino.size()) return true;

    Set<Integer> filasAgua = new HashSet<>();
    Set<Integer> direcciones = new HashSet<>();

    for (int i = startIndex; i < camino.size(); i++) {
        Vector2d p = camino.get(i);
        int x = (int) p.x, y = (int) p.y;
        
        int dirSpawner = 0;
        for (Vector2d sp : mundo.spawnsNenufar) {
            if ((int)sp.y == y) {
                Integer type = mundo.tiposSpawners.get(sp);
                dirSpawner = (type != null && type == 7) ? Mundo49.DIR_DERECHA : Mundo49.DIR_IZQUIERDA;
                break;
            }
        }

        if (dirSpawner != 0 && !mundo.mapaTransitable[x][y] && mundo.mapaTrayectoria[x][y]) {
            filasAgua.add(y);
            direcciones.add(dirSpawner);
        } else if (filasAgua.isEmpty()) {
            continue;
        } else {
            break;
        }
    }

    if (filasAgua.isEmpty()) return true;

    if (filasAgua.size() > 1) {
      //  System.out.println("[SYNC] Analizando tramo de " + filasAgua.size() + " filas basado en spawners: " + filasAgua + " Dirs: " + direcciones.size());
    }

    // Si hay varias filas y al menos dos tienen direcciones distintas, aplicamos sincronización
    if (filasAgua.size() > 1 && direcciones.size() > 1) {
        for (int y : filasAgua) {
            if (!comprobarSpawnRecienteEnFila(y, mundo)) {
                return false;
            }
        }
        return true;
    }

    return true;
}

private boolean comprobarSpawnRecienteEnFila(int y, Mundo49 mundo) {
    Vector2d spawn = null;
    int dir = 0;

    for (Vector2d sp : mundo.spawnsNenufar) {
        if ((int) sp.y == y) {
            spawn = sp;
            Integer type = mundo.tiposSpawners.get(sp);
            dir = (type != null && type == 7)
                    ? Mundo49.DIR_DERECHA
                    : Mundo49.DIR_IZQUIERDA;
            break;
        }
    }

    if (spawn == null) return false;

    ArrayList<Observation>[][] grid = mundo.stateObsActual.getObservationGrid();
    double spawnX = spawn.x;

    for (int x = 0; x < mundo.columnas; x++) {
        for (Observation obs : grid[x][y]) {
            if (obs.itype == 10 || obs.itype == 11) {
                double realX = obs.position.x / mundo.Bloque;

       double margen = 0.3;

                if (dir == Mundo49.DIR_DERECHA) {
                    if (realX >= spawnX && realX <= spawnX + margen)
                        return true;
                } else {
                    if (realX <= spawnX && realX >= spawnX - margen)
                        return true;
                }
            }
        }
    }

    return false;
}

}