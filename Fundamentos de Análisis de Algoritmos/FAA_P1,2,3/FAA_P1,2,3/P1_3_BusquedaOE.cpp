#include "P1_3_BusquedaOE.h"
#include <iostream>
#include <fstream>
#include <iomanip>
#include <algorithm>
#include <limits>

std::vector<int> P1_3_BusquedaOE::generarVectorAleatorio(int n, std::mt19937& gen) {
    std::vector<int> vec(n);
    for (int i = 0; i < n; ++i) {
        vec[i] = i + 1;
    }
    std::shuffle(vec.begin(), vec.end(), gen);
    return vec;
}

bool P1_3_BusquedaOE::busquedaSecuencial(const std::vector<int>& vec, int target, int& operacionesElementales) {
    operacionesElementales = 0;
    for (int num : vec) {
        operacionesElementales++;
        if (num == target) {
            return true;
        }
    }
    return false;
}

void P1_3_BusquedaOE::ejecutarExperimento(const std::string& nombreArchivo, int minN, int maxN, int paso, int repeticiones) {
    std::ofstream archivo(nombreArchivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    std::random_device rd;
    std::mt19937 gen(rd());

    archivo << std::fixed << std::setprecision(6);
    archivo << "Tamano del Vector;Caso Mejor (OE);Caso Peor (OE);Caso Medio (OE)" << std::endl;

    for (int n = minN; n <= maxN; n += paso) {
        std::vector<int> vectorAleatorio = generarVectorAleatorio(n, gen);

        int operacionesElementalesMejor = std::numeric_limits<int>::max();
        int operacionesElementalesPeor = 0;
        int OEsTotales = 0;

        std::uniform_int_distribution<int> distrib(1, n);

        for (int i = 0; i < repeticiones; ++i) {
            int valorAleatorio = distrib(gen);

            int OEsEjecucion = 0;
            bool encontrado = busquedaSecuencial(vectorAleatorio, valorAleatorio, OEsEjecucion);

            OEsTotales += OEsEjecucion;
            operacionesElementalesMejor = std::min(operacionesElementalesMejor, OEsEjecucion);
            operacionesElementalesPeor = std::max(operacionesElementalesPeor, OEsEjecucion);
        }

        double OEMedia = static_cast<double>(OEsTotales) / repeticiones;
        std::cout << "OEMedia para n=" << n << " -> " << OEMedia << std::endl;

        archivo << n << ";" << operacionesElementalesMejor << ";"
            << operacionesElementalesPeor << ";" << OEMedia << std::endl;
    }

    archivo.close();
    std::cout << "Resultados guardados en: " << nombreArchivo << std::endl;
}
