#ifndef P2_3_POTENCIADV_H
#define P2_3_POTENCIADV_H

#include <iostream>
#include <cstdlib>
#include <chrono>

class P2_3_PotenciaDV {
public:
    // Calcula a^n usando divide y vencerás contando operaciones elementales
    static long long potencia3(int a, int n, int& nOE);

    // Ejecuta el experimento con numPruebas aleatorias
    static void ejecutarExperimento(int numPruebas = 25);
};

#endif // P2_3_POTENCIADV_H
