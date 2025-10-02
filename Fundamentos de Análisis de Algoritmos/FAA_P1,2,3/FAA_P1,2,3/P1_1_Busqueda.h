#ifndef P1_1_BUSQUEDA_H
#define P1_1_BUSQUEDA_H

#include <vector>
#include <random>

class P1_1_Busqueda {
public:
	// Genera un vector aleatorio de tamaño n con valores únicos
	static std::vector<int> generarVectorAleatorio(int n, std::mt19937& gen);

	// Realiza una búsqueda secuencial en el vector
	static bool busquedaSecuencial(const std::vector<int>& vec, int target);

	// Ejecuta el experimento de medición de tiempos y guarda en CSV
	static void ejecutarExperimento(const std::string& nombreArchivo, int minN = 1000, int maxN = 10000, int paso = 1000);

};
#endif
