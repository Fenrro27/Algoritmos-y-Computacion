package si2024.kevinjesusbandaalu.p05;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.*;

public class Practicca_05_exe {
	// Practica de Sudokus

	private static String sourcePath;
	private static String destinationPath;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_CYAN = "\u001B[36m";

	public static void main(String[] args) {

		// Creamos una copia del archivo

		sourcePath = "./src/si2024/kevinjesusbandaalu/p05/archivos/2024_tableros.txt";
		destinationPath = "./src/si2024/kevinjesusbandaalu/p05/archivos/SudokusSolucion.txt";

		// Realizamos una copia
		makeFileCopy(sourcePath, destinationPath);

		int nRepeticion = 0;
		double noResueltos;
		try {
			do {
				System.out.println("Inicio de ejecucion del ciclo "+(nRepeticion+1));
				noResueltos = 0;
				BufferedReader br = new BufferedReader(new FileReader(sourcePath));
				String linea;

				long ini2 = System.currentTimeMillis();
				long tiempoTotal=0;
				int nLinea = 0;// indica la linea que estamos mirando
				while ((linea = br.readLine()) != null && nLinea<100) {
					// Hacemos el sudoku y una vez resuelto hacemos una copia y lo mkodificamos
					System.out.println((nLinea + 1) + ANSI_YELLOW + ": " + linea + ANSI_RESET);
					long ini = System.currentTimeMillis();
					AC3 algoritmoAc3 = new AC3(Node.getNodes(linea));
					String res = algoritmoAc3.resolver();

					if (res != null) {// guardamos la linea modificada si no ha habido fallos
						System.out.println((nLinea + 1) + ANSI_GREEN + " (Completa): " + res + ANSI_RESET);
						ingresarEnDestino(res, nLinea);
					} else {
						noResueltos++;
						System.out.println(ANSI_RED + "No hay solucion para este sudoku." + ANSI_RESET);
					}
					long tiempo = System.currentTimeMillis() - ini;
					System.out.println("Tiempo: " + ANSI_CYAN + (tiempo) + ANSI_RESET
							+ "ms, Total de no resueltos: " + ANSI_RED + (noResueltos) + ANSI_RESET);
					tiempoTotal+=tiempo;
					nLinea++;

				}
				System.out.println(ANSI_CYAN+"Tiempo total: "+(System.currentTimeMillis()-ini2)+"ms,  Tiempo medio de resolucion de sudoku: "+(tiempoTotal/100)+"ms"+ANSI_RESET);
				if(noResueltos>0) {
					sourcePath = destinationPath;
				}
				
				nRepeticion++;
				br.close();
			} while (noResueltos > 0 && nRepeticion < 3);
		} catch (Exception ex) {
			System.out.println("Excepcion: " + ex.getMessage());
		} finally {

			System.out.println("Ejecucion finalizada");
		}

	}

	private static boolean makeFileCopy(String sr, String tg) {
		Path sourcePath = Paths.get(sr);
		Path targetPath = Paths.get(tg);

		try {
			Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Copia realizada exitosamente.");
			return true;
		} catch (IOException ex) {
			System.out.println("Error al realizar la copia: " + ex.getMessage());
			return false;
		}
	}

	public static void ingresarEnDestino(String nuevaLinea, int posicion) {
		List<String> lines = new ArrayList<>();

		// Leer el contenido del archivo
		try (BufferedReader br = new BufferedReader(new FileReader(destinationPath))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException ex) {
			System.out.println("Error al leer el archivo: " + ex.getMessage());
			return;
		}

		// Insertar la nueva línea en la posición deseada
		if (posicion >= 0 && posicion < lines.size()) {
			lines.set(posicion, nuevaLinea);
		} else if (posicion == lines.size()) {
			lines.add(nuevaLinea);
		} else {
			System.out.println("Posición inválida.");
			return;
		}

		// Escribir el contenido modificado de nuevo al archivo
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(destinationPath))) {
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
		} catch (IOException ex) {
			System.out.println("Error al sobreescribir el fichero: " + ex.getMessage());
		}

	}

	public static String leerArchivo(String ruta) {
		StringBuilder cadena = new StringBuilder();

		try (FileReader entrada = new FileReader(ruta)) {
			int c;
			while ((c = entrada.read()) != -1) {
				cadena.append((char) c);
			}
		} catch (IOException ex) {
			System.out.println("Error al leer: " + ex.getMessage());
		}

		return cadena.toString();
	}
}
