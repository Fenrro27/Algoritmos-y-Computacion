#include "P1_2A_Busqueda.h"
#include <iostream>
#include <fstream>
#include <iomanip>
#include <algorithm>
#include <chrono>
#include <limits>

std::vector<int> P1_2A_Busqueda::generarVectorAleatorio(int n, std::mt19937& gen) {
    std::vector<int> vec(n);
    for (int i = 0; i < n; ++i) {
        vec[i] = i + 1;
    }
    std::shuffle(vec.begin(), vec.end(), gen);
    return vec;
}

bool P1_2A_Busqueda::busquedaSecuencial(const std::vector<int>& vec, int target) {
    for (int num : vec) {
        if (num == target) {
            return true;
        }
    }
    return false;
}

void P1_2A_Busqueda::ejecutarExperimento(const std::string& nombreArchivo, int minN, int maxN, int paso, int repeticiones) {
    std::ofstream archivo(nombreArchivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    std::random_device rd;
    std::mt19937 gen(rd());

    archivo << std::fixed << std::setprecision(6);
    archivo << "Tamano del Vector;Mejor Tiempo (us);Peor Tiempo (us);Tiempo Medio (us)" << std::endl;

    for (int n = minN; n <= maxN; n += paso) {
        std::vector<int> vectorAleatorio = generarVectorAleatorio(n, gen);

        double tiempo_total = 0.0;
        double mejor_tiempo = std::numeric_limits<double>::max();
        double peor_tiempo = 0.0;

        std::uniform_int_distribution<int> distrib(1, n);

        for (int i = 0; i < repeticiones; ++i) {
            int valorAleatorio = distrib(gen);

            auto inicio = std::chrono::high_resolution_clock::now();
            bool encontrado = busquedaSecuencial(vectorAleatorio, valorAleatorio);
            auto fin = std::chrono::high_resolution_clock::now();

            double tiempo_busqueda = std::chrono::duration<double, std::micro>(fin - inicio).count();
            tiempo_total += tiempo_busqueda;

            mejor_tiempo = std::min(mejor_tiempo, tiempo_busqueda);
            peor_tiempo = std::max(peor_tiempo, tiempo_busqueda);
        }

        double tiempo_medio = tiempo_total / repeticiones;
        archivo << n << ";" << mejor_tiempo << ";" << peor_tiempo << ";" << tiempo_medio << std::endl;
    }

    archivo.close();
    std::cout << "Resultados guardados en: " << nombreArchivo << std::endl;
}
