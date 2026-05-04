package si2026.kevinjesusbandaalu.p04;

import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import core.game.Observation;
import java.util.*;

public class Motor49 {

    private boolean debugActivo = false;

    // Objetivo fijo: se guarda entre ticks para evitar oscilaciones
    private Vector2d objetivoFijo = null; // posición destino (catapulta o espera nenúfar)
    private boolean objetivoEsCatapulta = false;

    // Nodo interno para A* con referencias de padre (evita conflicto con Node.java)
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

    // ═══════════════════════════════════════════════════════════════
    //  PUNTO DE ENTRADA PRINCIPAL
    // ═══════════════════════════════════════════════════════════════
    public ACTIONS buscar(Mundo49 mundo, ElapsedCpuTimer timer) {
        if (debugActivo) mostrarMapaAStar(mundo);

        if (mundo.salida == null) return ACTIONS.ACTION_NIL;
        int sx = (int) mundo.salida.x, sy = (int) mundo.salida.y;
      //  System.out.println("[A*] Salida en " + sx + "," + sy + " | Transitable=" + mundo.mapaTransitable[sx][sy]);

        int ax = (int) mundo.MiPosicion.x, ay = (int) mundo.MiPosicion.y;
        if (ax == sx && ay == sy)
            return ACTIONS.ACTION_NIL;

        // ── FASE 1: A* solo en capa 0 ───────────────────────────────────────
       // System.out.println("[A*] Verificando camino capa 0...");
        List<Vector2d> caminoCapa0 = aStar(mundo.MiPosicion, mundo.salida, mundo, false, timer);
        if (caminoCapa0 != null && caminoCapa0.size() >= 2) {
       //     System.out.println("[A*] ¡Camino capa 0 encontrado!");
            objetivoFijo = null; 
            ACTIONS acc = traducirDireccion(caminoCapa0.get(0), caminoCapa0.get(1));
            return acc;
        }
       // System.out.println("[A*] No hay camino directo en capa 0.");

        // ── FASE 2: Escapar usando trayectorias ────────────────────────────────
        if (objetivoFijo != null) {
            boolean enObjetivo = igualPos(mundo.MiPosicion, objetivoFijo);
            // Debug para entender por qué igualPos podría fallar
            if (debugActivo) {
                System.out.println("[DEBUG] MiPos=" + (int)mundo.MiPosicion.x + "," + (int)mundo.MiPosicion.y 
                    + " Obj=" + (int)objetivoFijo.x + "," + (int)objetivoFijo.y 
                    + " igual=" + enObjetivo);
            }

            if (enObjetivo) {
                if (!objetivoEsCatapulta) {
                    // Espera de nenúfar
                    int wx = (int) objetivoFijo.x, wy = (int) objetivoFijo.y;
                    int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
                    for (int[] d : dirs4) {
                        int nx = wx + d[0], ny = wy + d[1];
                        if (nx < 0 || nx >= mundo.columnas || ny < 0 || ny >= mundo.filas) continue;
                        if (mundo.mapaTrayectoria[nx][ny] && hayNenufarEn(nx, ny, mundo)) {
                    //        System.out.println("[NEN] ¡Subiendo al nenúfar!");
                            objetivoFijo = null;
                            return traducirDireccion(new Vector2d(wx, wy), new Vector2d(nx, ny));
                        }
                    }
                    return ACTIONS.ACTION_NIL;
                } else {
                    // Catapulta: debería activarse sola, pero forzamos movimiento por si acaso
                 //   System.out.println("[CAT] En catapulta. Re-evaluando trayectoria.");
                    objetivoFijo = null; 
                    // No retornamos NIL aquí, dejamos que estrategiaEscape calcule el siguiente paso
                }
            } else {
                ACTIONS acc = navegarA(objetivoFijo, mundo, timer);
                return acc;
            }
        }

     //   System.out.println("[A*] Verificando camino con trayectorias (Capa 0 + Capa 1)...");
        return estrategiaEscape(mundo, timer);
    }

    // ═══════════════════════════════════════════════════════════════
    //  A* GENÉRICO (usarTrayectoria = incluir capa 1)
    //  Devuelve lista desde inicio (incl.) hasta fin (incl.), o null
    // ═══════════════════════════════════════════════════════════════
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

            // Si ya procesamos esta celda con menor g, descartamos
            if (cur.g > gScore.getOrDefault(ck, Double.MAX_VALUE) + 0.001) continue;

            if (igualPos(cur.pos, fin)) {
                return reconstruirCamino(cur);
            }

            for (Vector2d v : vecinos(cur.pos, mundo, usarTrayectoria)) {
                String vk = k(v);
                double ng = cur.g + 1;
                if (ng < gScore.getOrDefault(vk, Double.MAX_VALUE)) {
                    gScore.put(vk, ng);
                    open.add(new AN(v, cur, ng, dist(v, fin)));
                }
            }
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    //  ESTRATEGIA DE ESCAPE (Fase 2) – fija un objetivo y navega hacia él
    // ═══════════════════════════════════════════════════════════════
    private ACTIONS estrategiaEscape(Mundo49 mundo, ElapsedCpuTimer timer) {
        List<Vector2d> camino = aStar(mundo.MiPosicion, mundo.salida, mundo, true, timer);
        if (camino == null || camino.size() < 2) {
           // System.out.println("[A*] ERROR: No hay camino ni con trayectorias. (Celdas Capa 0 + Capa 1)");
            return ACTIONS.ACTION_NIL;
        }
      //  System.out.println("[A*] ¡Camino con trayectorias encontrado! Longitud=" + camino.size());

        // Si ya estamos sobre un nenúfar (en el agua), seguir el camino directamente
        int ax = (int) mundo.MiPosicion.x, ay = (int) mundo.MiPosicion.y;
        if (!mundo.mapaTransitable[ax][ay]) {
            Vector2d sig = camino.get(1);
            int sx = (int) sig.x, sy = (int) sig.y;
            
            // Si el siguiente paso es agua, esperar a que haya un nenúfar allí
            if (mundo.mapaTrayectoria[sx][sy] && !mundo.mapaTransitable[sx][sy]) {
                if (hayNenufarEn(sx, sy, mundo)) {
             //       System.out.println("[ESCAPE] Saltando de nenúfar a nenúfar: " + sx + "," + sy);
                    return traducirDireccion(mundo.MiPosicion, sig);
                } else {
                    return ACTIONS.ACTION_NIL; // Esperar en el actual
                }
            } else {
                // Si es tierra firme, saltar ya
                return traducirDireccion(mundo.MiPosicion, sig);
            }
        }

        for (int i = 1; i < camino.size(); i++) {
            int cx = (int) camino.get(i).x, cy = (int) camino.get(i).y;

            if (mundo.mapaTrayectoria[cx][cy] && !mundo.mapaTransitable[cx][cy]) {
                Mundo49.Trayectoria tray = buscarTrayectoria(cx, cy, mundo);
                if (tray == null) {
              //      System.out.println("[ESCAPE] Sin trayectoria para " + cx + "," + cy);
                    return ACTIONS.ACTION_NIL;
                }
            //    System.out.println("[ESCAPE] Usando " + tray);

                if (tray.tipo == Mundo49.Trayectoria.TIPO_CATAPULTA) {
                    objetivoFijo = tray.origen;
                    objetivoEsCatapulta = true;
              //      System.out.println("[ESCAPE] Objetivo CAT " + (int)tray.origen.x + "," + (int)tray.origen.y);
                    return navegarA(tray.origen, mundo, timer);
                } else {
                    // Es un nenúfar: el mejor sitio para esperar es la celda de tierra desde la que saltaremos
                    Vector2d espera = camino.get(i-1); 
                    
                    // Si ya estamos en la celda de espera, no llamamos a navegarA (evita bucles)
                    if (igualPos(mundo.MiPosicion, espera)) {
                        objetivoFijo = espera;
                        objetivoEsCatapulta = false;
                        return ACTIONS.ACTION_NIL;
                    }

                    objetivoFijo = espera;
                    objetivoEsCatapulta = false;
                //    System.out.println("[ESCAPE] Objetivo NEN (espera en salto) " + (int)espera.x + "," + (int)espera.y);
                    return navegarA(espera, mundo, timer);
                }
            }
        }
        return traducirDireccion(camino.get(0), camino.get(1));
    }

    /** Busca la Trayectoria que contiene la celda (cx, cy) en sus celdas de agua o es su destino. */
    private Mundo49.Trayectoria buscarTrayectoria(int cx, int cy, Mundo49 mundo) {
        for (Mundo49.Trayectoria t : mundo.trayectorias) {
            // 1. Es el destino final
            if (t.destino != null && (int)t.destino.x == cx && (int)t.destino.y == cy) return t;
            // 2. Es una de las celdas intermedias (vuelo/agua)
            for (Vector2d c : t.celdas)
                if ((int)c.x == cx && (int)c.y == cy) return t;
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    //  ESTRATEGIA NENÚFAR
    //  filaT / colT = coordenadas de la primera celda de trayectoria encontrada
    // ═══════════════════════════════════════════════════════════════
    private ACTIONS estrategiaNenufar(int filaT, int colT, Mundo49 mundo, ElapsedCpuTimer timer) {
        int ax = (int) mundo.MiPosicion.x, ay = (int) mundo.MiPosicion.y;
        int dir = mundo.mapaDireccion[colT][filaT];
        int dxN = dxDir(dir), dyN = dyDir(dir);

        // 1. Si ya estamos en capa 0 adyacente a la trayectoria, comprobar si el nenúfar está ahí
        if (mundo.mapaTransitable[ax][ay]) {
            int nx = ax + dxN, ny = ay + dyN;
            if (nx >= 0 && nx < mundo.columnas && ny >= 0 && ny < mundo.filas
                    && mundo.mapaTrayectoria[nx][ny] && !mundo.mapaTransitable[nx][ny]) {
                // La celda adyacente es trayectoria: ¿hay un nenúfar ahí ahora?
                if (hayNenufarEn(nx, ny, mundo)) {
               //     System.out.println("[NENUFAR] Nenúfar detectado en " + nx + "," + ny + " → subiendo");
                    return dirAAccion(dir);
                }
            }
        }

        // 2. Encontrar la mejor celda de espera (capa 0 más cerca del spawn en esa fila)
        Vector2d espera = mejorEspera(filaT, mundo);
        if (espera == null) {
            System.out.println("[NENUFAR] Sin celda de espera válida → ACTION_NIL");
            return ACTIONS.ACTION_NIL;
        }

        // Si ya estamos en la mejor celda de espera → esperar (ACTION_NIL)
        if (igualPos(mundo.MiPosicion, espera)) {
        //    System.out.println("[NENUFAR] Esperando nenúfar en spawn " + (int)espera.x + "," + (int)espera.y);
            // Revisar si el nenúfar ya está en la celda adyacente desde aquí
            int nx = (int)espera.x + dxN, ny = (int)espera.y + dyN;
            if (hayNenufarEn(nx, ny, mundo)) return dirAAccion(dir);
            return ACTIONS.ACTION_NIL;
        }

        // Navegar hacia la mejor celda de espera usando capa 0
     //   System.out.println("[NENUFAR] Navegando a celda de espera " + (int)espera.x + "," + (int)espera.y);
        return navegarA(espera, mundo, timer);
    }

    /**
     * Devuelve la celda de capa 0 más cercana al spawn del nenúfar en la fila indicada.
     * "Más cerca del spawn" maximiza las probabilidades de coger el nenúfar cuando aparece.
     */
    private Vector2d mejorEspera(int fila, Mundo49 mundo) {
        // Buscar el spawn de esa fila (celda capa-0 con dirección forzada en spawnsNenufar)
        Vector2d spawn = null;
        for (Vector2d sp : mundo.spawnsNenufar) {
            if ((int) sp.y == fila) { spawn = sp; break; }
        }

        if (spawn != null && mundo.mapaTransitable[(int)spawn.x][(int)spawn.y]) {
            // El spawn mismo es accesible → usarlo como punto de espera óptimo
            return spawn;
        }

        // Si el spawn no es accesible directamente en capa 0,
        // buscar la celda de capa 0 en esa fila más cercana al spawn
        int spawnX = (spawn != null) ? (int) spawn.x : mundo.columnas / 2;
        Vector2d mejor = null;
        double mejorDist = Double.MAX_VALUE;
        for (int x = 0; x < mundo.columnas; x++) {
            if (mundo.mapaTransitable[x][fila]) {
                double d = Math.abs(x - spawnX);
                if (d < mejorDist) { mejorDist = d; mejor = new Vector2d(x, fila); }
            }
        }
        return mejor;
    }

    /** A* en capa 0 hacia un destino y devuelve la primera acción. */
    private ACTIONS navegarA(Vector2d destino, Mundo49 mundo, ElapsedCpuTimer timer) {
        List<Vector2d> cam = aStar(mundo.MiPosicion, destino, mundo, false, timer);
        if (cam != null && cam.size() >= 2) return traducirDireccion(cam.get(0), cam.get(1));
        return ACTIONS.ACTION_NIL;
    }

    // ═══════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Vecinos válidos desde una celda.
     *  - Celda de TRAYECTORIA (nenúfar):  4 direcciones → puede saltar a tierra o a otro nenúfar.
     *  - Celda de CAPA 0 con dir. forzada (catapulta/spawn): solo la dirección forzada.
     *  - Celda de CAPA 0 libre (suelo):    4 direcciones estándar.
     */
    private List<Vector2d> vecinos(Vector2d pos, Mundo49 mundo, boolean usarTray) {
        int x = (int) pos.x, y = (int) pos.y;
        int dir = mundo.mapaDireccion[x][y];
        boolean enNenufar = mundo.mapaTrayectoria[x][y] && !mundo.mapaTransitable[x][y];
        List<Vector2d> result = new ArrayList<>();

        if (enNenufar) {
            // Sobre nenúfar: libertad para saltar a tierra o a otro nenúfar adyacente
            int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : dirs4) {
                int nx = x + d[0], ny = y + d[1];
                if (nx < 0 || nx >= mundo.columnas || ny < 0 || ny >= mundo.filas) continue;
                if (mundo.mapaTransitable[nx][ny] || (usarTray && mundo.mapaTrayectoria[nx][ny])) {
                    result.add(new Vector2d(nx, ny));
                }
            }
        } else if (dir != Mundo49.DIR_LIBRE) {
            // Celda con dirección forzada (Catapulta o Spawn)
            if (esCatapulta(x, y, mundo)) {
                // Si es catapulta, el vecino es directamente el DESTINO (salto instantáneo en A*)
                Mundo49.Trayectoria t = buscarTrayectoriaPorOrigen(x, y, mundo);
                if (t != null && t.destino != null) {
                    int tx = (int)t.destino.x, ty = (int)t.destino.y;
                    // Solo aterrizamos si el destino es seguro (suelo o nenúfar)
                    if (mundo.mapaTransitable[tx][ty] || (usarTray && mundo.mapaTrayectoria[tx][ty])) {
                        result.add(t.destino);
                    }
                }
            } else {
                // Si es spawn de nenúfar, solo podemos movernos en la dirección del flujo
                int nx = x + dxDir(dir), ny = y + dyDir(dir);
                if (nx >= 0 && nx < mundo.columnas && ny >= 0 && ny < mundo.filas) {
                    if (mundo.mapaTransitable[nx][ny] || (usarTray && mundo.mapaTrayectoria[nx][ny])) {
                        result.add(new Vector2d(nx, ny));
                    }
                }
            }
        } else {
            // Suelo firme libre: 4 direcciones
            int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : dirs4) {
                int nx = x + d[0], ny = y + d[1];
                if (nx < 0 || nx >= mundo.columnas || ny < 0 || ny >= mundo.filas) continue;
                if (mundo.mapaTransitable[nx][ny]) {
                    result.add(new Vector2d(nx, ny));
                } else if (usarTray && mundo.mapaTrayectoria[nx][ny]) {
                    // Solo permitimos entrar en trayectoria si es de tipo NENUFAR
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
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) return false;
        
        for (Observation obs : grid[x][y]) {
            if (obs.itype == 10 || obs.itype == 11) {
                // Calcular posición relativa al centro de la celda en unidades de grid
                double relX = Math.abs((obs.position.x / mundo.Bloque) - x);
                double relY = Math.abs((obs.position.y / mundo.Bloque) - y);
                
                // Si está suficientemente cerca del centro de la celda (margen 0.35)
                if (relX < 0.35 && relY < 0.35) return true;
            }
        }
        return false;
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

    private ACTIONS dirAAccion(int dir) {
        switch (dir) {
            case Mundo49.DIR_DERECHA:   return ACTIONS.ACTION_RIGHT;
            case Mundo49.DIR_IZQUIERDA: return ACTIONS.ACTION_LEFT;
            case Mundo49.DIR_ABAJO:     return ACTIONS.ACTION_DOWN;
            case Mundo49.DIR_ARRIBA:    return ACTIONS.ACTION_UP;
            default: return ACTIONS.ACTION_NIL;
        }
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

    // ═══════════════════════════════════════════════════════════════
    //  DEBUG: visualización del mapa de dos capas
    // ═══════════════════════════════════════════════════════════════
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
}