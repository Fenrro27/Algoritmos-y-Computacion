package si2026.kevinjesusbandaalu.common;

import core.vgdl.VGDLParser;
import core.game.Game;
import core.game.StateObservation;
import core.game.Observation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import tools.Utils;
import core.vgdl.VGDLFactory;
import core.vgdl.VGDLRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExtractorNumerico {
    
    private static final int MAX_MAPAS = 20;

    public static void main(String[] args) {
        
        // --- 0. INICIALIZAR EL MOTOR GVGAI ---
        VGDLFactory.GetInstance().init();
        VGDLRegistry.GetInstance().init();

        String spGamesCollection = "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);

        int gameIdx = 78; 
        String gameName = games[gameIdx][1];
        String gamePath = games[gameIdx][0];

        PrintStream consoleOut = System.out;
        
        try {
            String dirPath = "src/si2026/kevinjesusbandaalu/common/mapas";
            File directorio = new File(dirPath);
            if (!directorio.exists()) {
                directorio.mkdirs(); 
            }

            String filePath = dirPath + "/" + gameName + "_mapas.txt";
            FileOutputStream fileOut = new FileOutputStream(filePath);
            
            DoubleStream doubleStream = new DoubleStream(consoleOut, fileOut);
            System.setOut(new PrintStream(doubleStream));

            System.out.println("Analizando juego: " + gameName);
            System.out.println("Archivo VGDL: " + gamePath);

            // --- 2. PARSEO DEL JUEGO ---
            VGDLParser parser = new VGDLParser();
            Game game = parser.parseGame(gamePath);

            imprimirLeyendaVGDL(game);
            
            HashSet<Integer> itypesVistos = new HashSet<>();

            // --- 3. BUCLE POR TODOS LOS NIVELES ---
            for (int levelIdx = 0; levelIdx < 5; levelIdx++) {
                
                String levelPath = gamePath.replace(gameName, gameName + "_lvl" + levelIdx);
                if (levelPath.endsWith(".vgdl")) {
                    levelPath = levelPath.replace(".vgdl", ".txt");
                }

                System.out.println("\n==================================================");
                System.out.println("NIVEL " + levelIdx + " | Archivo: " + levelPath);
                System.out.println("==================================================");

                try {
                    game.buildLevel(levelPath, 0); 
                    StateObservation stateObs = game.getObservation();

                    int avatarItype = stateObs.getAvatarType();
                    procesarItypeIndividual("Avatar", avatarItype, itypesVistos);

                    procesarGrupoItypes("NPCs", stateObs.getNPCPositions(), itypesVistos);
                    procesarGrupoItypes("Inmovibles", stateObs.getImmovablePositions(), itypesVistos);
                    procesarGrupoItypes("Recursos", stateObs.getResourcesPositions(), itypesVistos);
                    procesarGrupoItypes("Portales", stateObs.getPortalsPositions(), itypesVistos);

                    System.out.println("\n--- DIBUJANDO MAPAS DEL NIVEL " + levelIdx + " ---");
                    dibujarMapas(stateObs, avatarItype);

                    // --- RECONSTRUCCIÓN CON EL NUEVO ALGORITMO ---
                    reconstruirMapa(stateObs, game);

                    imprimirMapaCaracteres(levelPath);

                } catch (Exception e) {
                    System.out.println("No se pudo cargar el nivel " + levelIdx + " (¿quizás el juego tiene menos niveles?).");
                }
            }

            System.out.println("\n✅ Análisis completado con éxito.");
            System.out.println("📄 Los resultados se han guardado en: " + filePath);

            System.out.flush();
            fileOut.close();

        } catch (Exception e) {
            System.setOut(consoleOut);
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- NUEVO ALGORITMO: RECONSTRUIR MAPA DESDE STATE OBSERVATION ---
    private static void reconstruirMapa(StateObservation stateObs, Game game) {
        System.out.println("\n--- MAPA RECONSTRUIDO (Ingeniería Inversa desde el Grid) ---");
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        if (grid == null) {
            System.out.println("El grid está vacío para este nivel.");
            return;
        }

        int width = grid.length;
        int height = grid[0].length;
        
        // 1. Recopilar todos los itypes que REALMENTE son visibles en el grid
        HashSet<Integer> itypesVisibles = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[x][y] != null) {
                    for (Observation obs : grid[x][y]) {
                        itypesVisibles.add(obs.itype);
                    }
                }
            }
        }
        
        // Añadimos el Avatar explícitamente a los visibles (GVGAI lo oculta del grid)
        int avatarItype = stateObs.getAvatarType();
        itypesVisibles.add(avatarItype);

        // 2. Crear las firmas efectivas (Firma VGDL original eliminando objetos invisibles)
        HashMap<Character, HashSet<Integer>> charToEffectiveSignature = new HashMap<>();
        HashMap<Character, ArrayList<String>> charMapping = game.getCharMapping();
        VGDLRegistry registry = VGDLRegistry.GetInstance();

        if (charMapping != null) {
            for (Character c : charMapping.keySet()) {
                HashSet<Integer> effectiveSignature = new HashSet<>();
                for (String spriteName : charMapping.get(c)) {
                    int itype = registry.getRegisteredSpriteValue(spriteName);
                    // Solo añadimos el objeto a la firma si GVGAI decide renderizarlo
                    if (itypesVisibles.contains(itype)) {
                        effectiveSignature.add(itype);
                    }
                }
                charToEffectiveSignature.put(c, effectiveSignature);
            }
        }

        // Posición del avatar en casillas (para inyectarlo manualmente en la celda correspondiente)
        int avatarX = (int) (stateObs.getAvatarPosition().x / stateObs.getBlockSize());
        int avatarY = (int) (stateObs.getAvatarPosition().y / stateObs.getBlockSize());

        // 3. Recorrer el grid y buscar la coincidencia exacta
        for (int y = 0; y < height; y++) {
            StringBuilder fila = new StringBuilder();
            for (int x = 0; x < width; x++) {
                HashSet<Integer> cellItypes = new HashSet<>();
                
                if (grid[x][y] != null) {
                    for (Observation obs : grid[x][y]) {
                        cellItypes.add(obs.itype);
                    }
                }

                // Inyectar el avatar si estamos en su celda geométrica
                if (x == avatarX && y == avatarY) {
                    cellItypes.add(avatarItype);
                }

                char deducido = ' ';
                boolean coincidenciaEncontrada = false;

                // Buscamos qué carácter del LevelMapping coincide EXACTAMENTE con lo que vemos
                for (Character c : charToEffectiveSignature.keySet()) {
                    HashSet<Integer> signature = charToEffectiveSignature.get(c);
                    
                    if (signature.equals(cellItypes)) {
                        deducido = c;
                        coincidenciaEncontrada = true;
                        break; 
                    }
                }
                
                // Si la celda tiene objetos dinámicos o extraños que no coinciden con la leyenda, ponemos '?'
                if (!coincidenciaEncontrada && !cellItypes.isEmpty()) {
                    deducido = '?';
                }

                fila.append("[").append(String.format("%-6s", "  " + deducido)).append("]");
            }
            System.out.println(fila.toString());
        }
    }


    // --- RESTO DE MÉTODOS ---
    private static void imprimirLeyendaVGDL(Game game) {
        System.out.println("\n==================================================");
        System.out.println(" LEYENDA ORIGINAL DEL VGDL (LevelMapping)");
        System.out.println("==================================================");
        
        HashMap<Character, ArrayList<String>> charMapping = game.getCharMapping();
        VGDLRegistry registry = VGDLRegistry.GetInstance();
        
        if (charMapping == null || charMapping.isEmpty()) {
            System.out.println("No se encontró LevelMapping en el archivo VGDL.");
            return;
        }

        for (Character c : charMapping.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Carácter [%c] genera -> ", c));
            
            for (String spriteName : charMapping.get(c)) {
                int itype = registry.getRegisteredSpriteValue(spriteName);
                sb.append(String.format("%s (itype: %d)  ", spriteName, itype));
            }
            System.out.println(sb.toString());
        }
    }

// --- MÉTODOS ACTUALIZADOS ---
    
    private static void procesarItypeIndividual(String listaOrigen, int itype, HashSet<Integer> vistos) {
        String estado = vistos.contains(itype) ? "[Ya existía]" : "[NUEVO]";
        vistos.add(itype); 
        
        // Obtenemos el nombre del sprite usando el Registro VGDL
        String nombreSprite = VGDLRegistry.GetInstance().getRegisteredSpriteKey(itype);
        if (nombreSprite == null) nombreSprite = "Desconocido";

        // Formateamos para que el nombre aparezca entre paréntesis junto al itype
        String infoItype = String.format("%2d (%s)", itype, nombreSprite);
        
        System.out.println(String.format("Lista: %-12s -> itype: %-20s | category: %2d %s", 
                                         listaOrigen, infoItype, 1, estado));
    }

    private static void procesarGrupoItypes(String listaOrigen, ArrayList<Observation>[] grupos, HashSet<Integer> vistos) {
        if (grupos == null) return;
        for (ArrayList<Observation> grupo : grupos) {
            if (!grupo.isEmpty()) {
                Observation obs = grupo.get(0);
                int itype = obs.itype;
                int category = obs.category; 
                String estado = vistos.contains(itype) ? "[Ya existía]" : "[NUEVO]";
                vistos.add(itype);
                
                // Obtenemos el nombre del sprite usando el Registro VGDL
                String nombreSprite = VGDLRegistry.GetInstance().getRegisteredSpriteKey(itype);
                if (nombreSprite == null) nombreSprite = "Desconocido";

                // Formateamos para que el nombre aparezca entre paréntesis junto al itype
                String infoItype = String.format("%2d (%s)", itype, nombreSprite);

                System.out.println(String.format("Lista: %-12s -> itype: %-20s | category: %2d %s", 
                                                 listaOrigen, infoItype, category, estado));
            }
        }
    }

    private static void dibujarMapas(StateObservation stateObs, int avatarItype) {
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        if (grid == null) return;

        int width = grid.length;
        int height = grid[0].length;
        ArrayList<String>[][] cellStrings = new ArrayList[width][height];
        ArrayList<int[]> celdasMultiples = new ArrayList<>();
        String[][] mapaBase = new String[width][height];

        long totalCombinaciones = 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cellStrings[x][y] = new ArrayList<>();
                ArrayList<Observation> obsList = grid[x][y];

                if (obsList == null || obsList.isEmpty()) {
                    cellStrings[x][y].add(String.format("%-6s", "")); 
                    mapaBase[x][y] = cellStrings[x][y].get(0);
                } else {
                    for (Observation obs : obsList) {
                        if (obs.itype == avatarItype) {
                            cellStrings[x][y].add(String.format("%-6s", "  Y"));
                        } else {
                            String etiqueta = obs.category + "/" + obs.itype;
                            cellStrings[x][y].add(String.format("%-6s", etiqueta));
                        }
                    }
                    if (cellStrings[x][y].size() == 1) {
                        mapaBase[x][y] = cellStrings[x][y].get(0);
                    } else {
                        celdasMultiples.add(new int[]{x, y});
                        totalCombinaciones *= cellStrings[x][y].size();
                    }
                }
            }
        }

        if (totalCombinaciones > MAX_MAPAS) {
            System.out.println("⚠️ ATENCIÓN: Solo se dibujarán los primeros " + MAX_MAPAS + " mapas de las combinaciones posibles.");
        }

        int[] contadorMapas = {1}; 
        generarCombinaciones(mapaBase, celdasMultiples, 0, cellStrings, width, height, contadorMapas);
    }

    private static void generarCombinaciones(String[][] mapaActual, ArrayList<int[]> celdasMultiples, 
                                             int indiceMulti, ArrayList<String>[][] cellStrings, 
                                             int width, int height, int[] contadorMapas) {
        
        if (contadorMapas[0] > MAX_MAPAS) return;

        if (indiceMulti == celdasMultiples.size()) {
            System.out.println("\n--- VERSIÓN DEL MAPA DE ITYPES " + contadorMapas[0] + " ---");
            for (int y = 0; y < height; y++) {
                StringBuilder fila = new StringBuilder();
                for (int x = 0; x < width; x++) {
                    fila.append("[").append(mapaActual[x][y]).append("]");
                }
                System.out.println(fila.toString());
            }
            contadorMapas[0]++;
            return;
        }

        int[] coordenadas = celdasMultiples.get(indiceMulti);
        int x = coordenadas[0];
        int y = coordenadas[1];

        for (String opcion : cellStrings[x][y]) {
            mapaActual[x][y] = opcion;
            generarCombinaciones(mapaActual, celdasMultiples, indiceMulti + 1, cellStrings, width, height, contadorMapas);
        }
    }

    private static void imprimirMapaCaracteres(String levelPath) {
        System.out.println("\n--- MAPA DE CARACTERES ORIGINAL (Archivo TXT) ---");
        try {
            List<String> lineas = Files.readAllLines(Paths.get(levelPath));
            for (String linea : lineas) {
                if (linea.trim().isEmpty()) continue;
                StringBuilder sb = new StringBuilder();
                for (char c : linea.toCharArray()) {
                    sb.append("[").append(String.format("%-6s", "  " + c)).append("]");
                }
                System.out.println(sb.toString());
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo original.");
        }
    }

    // =========================================================================
    // CLASE AUXILIAR
    // =========================================================================
    private static class DoubleStream extends OutputStream {
        private final OutputStream os1;
        private final OutputStream os2;

        public DoubleStream(OutputStream os1, OutputStream os2) {
            this.os1 = os1;
            this.os2 = os2;
        }

        @Override
        public void write(int b) throws IOException { os1.write(b); os2.write(b); }
        @Override
        public void write(byte[] b, int off, int len) throws IOException { os1.write(b, off, len); os2.write(b, off, len); }
        @Override
        public void flush() throws IOException { os1.flush(); os2.flush(); }
        @Override
        public void close() throws IOException { os2.close(); }
    }
}