#ifndef P3_FAA_H
#define P3_FAA_H

#include <iostream>
#include <fstream>
#include <cmath>

class P3_FAA {
public:
    // Algoritmo A1
    static int A1(int n);

    // Factorial
    static long factorial(int n);

    // Algoritmo A2
    static int A2(int n);

    // Ejecuta el experimento y guarda los resultados en CSV
    static void ejecutarExperimento(const std::string& nombreArchivo = "resultadosP3.csv", int numPruebas = 20);
};

#endif // P3_FAA_H
