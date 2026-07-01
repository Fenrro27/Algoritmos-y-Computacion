#ifndef P2_1_POTENCIA_H
#define P2_1_POTENCIA_H

#include <iostream>
#include <cstdlib>
#include <chrono>

class P2_1_Potencia {
public:
    // Calcula a^n mediante el algoritmo trivial iterativo
    static long long potencia(int a, int n, int& nOE);

    // Ejecuta el experimento con 25 pruebas aleatorias
    static void ejecutarExperimento(int numPruebas = 25);
};

#endif // P2_1_POTENCIA_H
