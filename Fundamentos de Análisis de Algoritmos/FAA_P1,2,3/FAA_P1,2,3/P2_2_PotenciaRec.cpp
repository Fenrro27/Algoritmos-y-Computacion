#include "P2_2_PotenciaRec.h"

long long P2_2_PotenciaRec::potencia(int a, int n, int& nOE) {
    nOE += 1; // Comparación n
    if (n == 0) {
        nOE += 1; // return
        return 1;
    }
    else {
        nOE = nOE + 1 + 1 + 1; // multiplicación, return, resta
        return a * potencia(a, n - 1, nOE);
    }
}

void P2_2_PotenciaRec::ejecutarExperimento(int numPruebas) {
    std::srand(42);  // Semilla fija para reproducibilidad

    for (int i = 0; i < numPruebas; i++) {
        int a = (std::rand() % 15) + 1;  // Base aleatoria [1,15]
        int n = (std::rand() % 15) + 1;  // Exponente aleatorio [1,15]
        int nOE = 0;

        auto inicio = std::chrono::high_resolution_clock::now();
        long long res = potencia(a, n, nOE);
        auto fin = std::chrono::high_resolution_clock::now();

        double tiempo = std::chrono::duration<double, std::micro>(fin - inicio).count();

        std::cout << i << ": Base(a): " << a
            << "\tExponente(n): " << n
            << "\tResultado: " << res
            << "\tNumero de operaciones elementales: " << nOE
            << "\tTiempo: " << tiempo << " us" << std::endl;
    }
}
