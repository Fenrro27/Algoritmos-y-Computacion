package si2026.kevinjesusbandaalu.p07;

import java.util.ArrayList;
import java.util.List;
import core.game.StateObservation;
import tools.Vector2d;
import java.util.HashSet;
import java.util.Set;
import ontology.Types.ACTIONS;

public class Motor78 {

    private Mundo78 mundo;
    private List<ACTIONS> planAcciones;
    private int pasoActual;

    private Plan cachedPlan;
    private int[][] cachedDistMap;
    private Strips.SearchState searchState;

    public Motor78(Mundo78 mundo, tools.ElapsedCpuTimer timer) {
        this.mundo = mundo;
        this.planAcciones = new ArrayList<>();
        
        this.cachedPlan = new Plan();
        construirAccionesYObjetivos(this.cachedPlan);
        this.cachedDistMap = calcularDistanciasYDeadEnds();

        Plan initialSetup = new Plan();
        initialSetup.finalStates = cachedPlan.finalStates;
        initialSetup.actions = cachedPlan.actions;
        obtenerEstadoInicial(initialSetup);

        this.searchState = new Strips.SearchState(initialSetup, cachedDistMap);
        List<String> solution = Strips.generateForwardPlan(this.searchState, timer);
        
        planAcciones = traducirPlan(solution);
        pasoActual = 0;
    }

    public ACTIONS act(StateObservation stateObs, tools.ElapsedCpuTimer timer) {
        if (pasoActual < planAcciones.size()) {
            return planAcciones.get(pasoActual++);
        }

        if (searchState.finalSolution == null) {
            List<String> solution = Strips.generateForwardPlan(this.searchState, timer);
            if (!solution.isEmpty()) {
                planAcciones = traducirPlan(solution);
                pasoActual = 0;
                if (pasoActual < planAcciones.size()) {
                    return planAcciones.get(pasoActual++);
                }
            } else {
                return ACTIONS.ACTION_NIL;
            }
        }

        return ACTIONS.ACTION_NIL;
    }

    private void obtenerEstadoInicial(Plan plan) {
        plan.initialStates = new ArrayList<>();
        if (mundo.posAvatar != null) {
            plan.initialStates.add("A_" + (int)mundo.posAvatar.x + "_" + (int)mundo.posAvatar.y);
        }
        for (Vector2d v : mundo.cajas) {
            plan.initialStates.add("C_" + (int)v.x + "_" + (int)v.y);
        }
    }

    private void construirAccionesYObjetivos(Plan plan) {
        plan.actions = new ArrayList<>();
        plan.finalStates = new ArrayList<>();

        int max_x = mundo.columnas;
        int max_y = mundo.filas;
        
        boolean[][] esMuro = new boolean[max_x][max_y];
        for (Vector2d v : mundo.muros) {
            if (v.x >= 0 && v.x < max_x && v.y >= 0 && v.y < max_y)
                esMuro[(int)v.x][(int)v.y] = true;
        }

        for (Vector2d v : mundo.objetivos) {
            plan.finalStates.add("C_" + (int)v.x + "_" + (int)v.y);
        }

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        String[] dirNames = {"UP", "DOWN", "LEFT", "RIGHT"};

        for (int x = 0; x < max_x; x++) {
            for (int y = 0; y < max_y; y++) {
                if (esMuro[x][y]) continue;

                for (int i = 0; i < 4; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    if (nx >= 0 && nx < max_x && ny >= 0 && ny < max_y && !esMuro[nx][ny]) {
                        String moveName = "MOVE_" + dirNames[i] + "_" + x + "_" + y;
                        List<String> pre = new ArrayList<>();
                        pre.add("A_" + x + "_" + y);
                        pre.add("E_" + nx + "_" + ny);
                        List<String> add = new ArrayList<>();
                        add.add("A_" + nx + "_" + ny);
                        add.add("E_" + x + "_" + y);
                        List<String> del = new ArrayList<>();
                        del.add("A_" + x + "_" + y);
                        del.add("E_" + nx + "_" + ny);
                        plan.actions.add(new Acciones(moveName, pre, add, del));

                        int nnx = nx + dx[i];
                        int nny = ny + dy[i];
                        if (nnx >= 0 && nnx < max_x && nny >= 0 && nny < max_y && !esMuro[nnx][nny]) {
                            String pushName = "PUSH_" + dirNames[i] + "_" + x + "_" + y;
                            List<String> preP = new ArrayList<>();
                            preP.add("A_" + x + "_" + y);
                            preP.add("C_" + nx + "_" + ny);
                            preP.add("E_" + nnx + "_" + nny);
                            List<String> addP = new ArrayList<>();
                            addP.add("A_" + nx + "_" + ny);
                            addP.add("C_" + nnx + "_" + nny);
                            addP.add("E_" + x + "_" + y);
                            List<String> delP = new ArrayList<>();
                            delP.add("A_" + x + "_" + y);
                            delP.add("C_" + nx + "_" + ny);
                            delP.add("E_" + nnx + "_" + nny);
                            plan.actions.add(new Acciones(pushName, preP, addP, delP));
                        }
                    }
                }
            }
        }
        System.err.println("STRIPS: Acciones generadas: " + plan.actions.size());
    }

    private int[][] calcularDistanciasYDeadEnds() {
        int max_x = mundo.columnas;
        int max_y = mundo.filas;
        boolean[][] esMuro = new boolean[max_x][max_y];
        for (Vector2d v : mundo.muros) {
            if (v.x >= 0 && v.x < max_x && v.y >= 0 && v.y < max_y)
                esMuro[(int)v.x][(int)v.y] = true;
        }

        int[][] distMap = new int[max_x][max_y];
        for (int i = 0; i < max_x; i++) {
            for (int j = 0; j < max_y; j++) {
                distMap[i][j] = 1000;
            }
        }

        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        for (Vector2d v : mundo.objetivos) {
            int gx = (int)v.x;
            int gy = (int)v.y;
            distMap[gx][gy] = 0;
            queue.add(new int[]{gx, gy, 0});
        }

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int prevX = curr[0];
            int prevY = curr[1];
            int d = curr[2];

            for (int i = 0; i < 4; i++) {
                int nx = prevX - dx[i]; // Donde estaba la caja antes de llegar a prevX
                int ny = prevY - dy[i];
                int avatarX = nx - dx[i]; // Donde debía estar el avatar para empujarla
                int avatarY = ny - dy[i];
                
                if (nx >= 0 && nx < max_x && ny >= 0 && ny < max_y && !esMuro[nx][ny]) {
                    if (avatarX >= 0 && avatarX < max_x && avatarY >= 0 && avatarY < max_y && !esMuro[avatarX][avatarY]) {
                        if (distMap[nx][ny] > d + 1) {
                            distMap[nx][ny] = d + 1;
                            queue.add(new int[]{nx, ny, d + 1});
                        }
                    }
                }
            }
        }
        
        return distMap;
    }

    private List<ACTIONS> traducirPlan(List<String> solution) {
        List<ACTIONS> acts = new ArrayList<>();
        if (solution == null) return acts;
        for (String s : solution) {
            if (s.startsWith("MOVE_UP") || s.startsWith("PUSH_UP")) acts.add(ACTIONS.ACTION_UP);
            else if (s.startsWith("MOVE_DOWN") || s.startsWith("PUSH_DOWN")) acts.add(ACTIONS.ACTION_DOWN);
            else if (s.startsWith("MOVE_LEFT") || s.startsWith("PUSH_LEFT")) acts.add(ACTIONS.ACTION_LEFT);
            else if (s.startsWith("MOVE_RIGHT") || s.startsWith("PUSH_RIGHT")) acts.add(ACTIONS.ACTION_RIGHT);
        }
        return acts;
    }
}
