#ifndef P1_3_BUSQUEDAOE_H
#define P1_3_BUSQUEDAOE_H

#include <vector>
#include <random>
#include <string>

class P1_3_BusquedaOE {
public:
    // Genera un vector aleatorio de tamaño n con valores únicos
    static std::vector<int> generarVectorAleatorio(int n, std::mt19937& gen);

    // Realiza una búsqueda secuencial en el vector y cuenta operaciones elementales
    static bool busquedaSecuencial(const std::vector<int>& vec, int target, int& operacionesElementales);

    // Ejecuta el experimento midiendo operaciones elementales (OE) y guarda resultados en CSV
    static void ejecutarExperimento(const std::string& nombreArchivo, int minN = 1000, int maxN = 10000, int paso = 1000, int repeticiones = 10);
};

#endif
