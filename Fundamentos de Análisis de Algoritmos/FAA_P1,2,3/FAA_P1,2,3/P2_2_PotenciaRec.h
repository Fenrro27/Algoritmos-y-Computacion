#ifndef P2_2_POTENCIAREC_H
#define P2_2_POTENCIAREC_H

#include <iostream>
#include <cstdlib>
#include <chrono>

class P2_2_PotenciaRec {
public:
    // Calcula a^n de forma recursiva contando operaciones elementales
    static long long potencia(int a, int n, int& nOE);

    // Ejecuta el experimento con numPruebas aleatorias
    static void ejecutarExperimento(int numPruebas = 25);
};

#endif // P2_2_POTENCIAREC_H
