package si2026.kevinjesusbandaalu.p02;

import java.util.ArrayList;
import java.util.List;

import core.game.Observation;
import core.game.StateObservation;
import si2026.kevinjesusbandaalu.common.IMundo;
import tools.Vector2d;

public class Mundo83 implements IMundo {

	public Vector2d MiPosicion;
	public Vector2d miOrientacion;
	public int Bloque, columnas, filas;
	
	public List<Observation> lasers;
	public List<Observation> enemigos;

	public Mundo83(StateObservation stateObs) {
		Bloque = stateObs.getBlockSize();
		columnas = stateObs.getWorldDimension().width / Bloque;
		filas = stateObs.getWorldDimension().height / Bloque;
		
		lasers = new ArrayList<>();
		enemigos = new ArrayList<>();

	}

	@Override
	public void AnalizarEntorno(StateObservation stateObs) {

		Vector2d Pos = stateObs.getAvatarPosition().copy();
		MiPosicion = new Vector2d(Pos.x / Bloque, Pos.y / Bloque);
		miOrientacion = stateObs.getAvatarOrientation();
		
		lasers.clear();
	    enemigos.clear();
		
		obtenerObservacionesAnalizadas(stateObs);

	}

	// Diccionario: Clave "cat-type" -> Letra a dibujar
	private java.util.HashMap<String, String> diccionarioLetras = new java.util.HashMap<>();

	public void obtenerObservacionesAnalizadas(StateObservation stateObs) {
		List<Observation> distintas = new ArrayList<>();
		java.util.HashSet<String> tiposVistos = new java.util.HashSet<>();
		ArrayList<Observation>[][] grid = stateObs.getObservationGrid();

		// 1. Analizar tipos únicos
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				for (Observation obs : grid[x][y]) {
					
					if (obs.category == 3) {
	                    enemigos.add(obs);
	                } 
	                else if (obs.category == 6) {
	                    lasers.add(obs);
	                }
					
					String clave = obs.category + "-" + obs.itype;
					if (!tiposVistos.contains(clave)) {
						distintas.add(obs);
						tiposVistos.add(clave);

						// Asignamos una letra por defecto al diccionario si no existe
						asignarLetraADiccionario(obs);
					}
				}
			}
		}

		// 2. Pintar el terreno una vez analizado
		pintarTerreno(stateObs);
	}

	private void asignarLetraADiccionario(Observation o) {
		String clave = o.category + "-" + o.itype;
		if (diccionarioLetras.containsKey(clave))
			return;

		String letra = "?"; // Por defecto
		switch (o.category) {
		case 2: { // Spawners
			switch (o.itype) {
			case 6: {
				letra = "H";
				break;
			}
			case 7: {
				letra = "H";
				break;
			}

			case 9: { // spwan de buzos
				letra = "h";
				break;
			}
			default:
				System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
			}
			break;
		}
		case 3: { // NPCs
			switch (o.itype) {
			case 15: { // Enemigos
				letra = "E";
				break;
			}
			case 17: { // Buzos
				letra = "B";
				break;
			}
			default:
				System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
			}
			break;
		}
		case 4: {
			switch (o.itype) {
			case 2: {
				letra = "_";
				break;
			}
			case 3: {
				letra = " ";
				break;
			}
			default:
				System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
			}
			break;
		}
		case 6: { //Proyectiles
			switch (o.itype) {
			case 14: {
				letra = "Y";
				break;
			}
			case 16: {
				letra = "Y";
				break;
			}
			default:
				System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
			}
			break;
		}
		default:
			System.out.println("Categoria: " + o.category + ", itype: " + o.itype);
		}

		diccionarioLetras.put(clave, letra);
	}

	private void pintarTerreno(StateObservation stateObs) {
		ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
		int ancho = grid.length;
		int alto = grid[0].length;

		System.out.println("\n=== MAPA DEL TERRENO ===");

		// Imprimir cabecera de columnas
		System.out.print("    ");
		for (int x = 0; x < ancho; x++)
			System.out.print(String.format("%2d", x));

		System.out.print("\n    ");
		for (int i = 0; i < ancho; i++)
			System.out.print("--");
		System.out.println();

		for (int y = 0; y < alto; y++) {
			// Índice de fila
			System.out.print(String.format("%2d |", y));

			for (int x = 0; x < ancho; x++) {
				ArrayList<Observation> obsEnCelda = grid[x][y];

				if (obsEnCelda.isEmpty()) {
					System.out.print(" ."); // Celda vacía
				} else {
					String letraElegida = null;

					// Priorizamos encontrar una letra válida en el diccionario
					for (Observation o : obsEnCelda) {
						String clave = o.category + "-" + o.itype;
						if (diccionarioLetras.containsKey(clave)) {
							String letraDict = diccionarioLetras.get(clave);

							// Si la letra no es un espacio vacío o el "no encontrado"
							if (letraDict != null && !letraDict.trim().isEmpty() && !letraDict.equals("?")) {
								letraElegida = letraDict;
								break; // Encontramos la letra importante, dejamos de buscar en esta celda
							}
						}
					}

					// Si después de buscar no encontramos letra "importante",
					// pero la celda no está vacía, ponemos un marcador genérico o la primera
					// disponible
					if (letraElegida == null) {
						letraElegida = " ";
					}

					// Imprimimos la letra (puedes añadir un indicador si hay más de uno)
					if (obsEnCelda.size() > 1) {
						// Opcional: podrías usar Mayúsculas o un símbolo especial para indicar grupo
						System.out.print(" " + letraElegida);
					} else {
						System.out.print(" " + letraElegida);
					}
				}
			}
			System.out.println();
		}
		System.out.println("========================\n");
	}
}
