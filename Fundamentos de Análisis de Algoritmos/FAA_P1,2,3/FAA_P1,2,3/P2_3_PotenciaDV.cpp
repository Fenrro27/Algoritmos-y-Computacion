#include "P2_3_PotenciaDV.h"

long long P2_3_PotenciaDV::potencia3(int a, int n, int& nOE) {
    nOE += 1; // comparación n
    if (n == 0) {
        nOE += 1; // return
        return 1;
    }
    else if ((n % 2) == 0) { // exponente par
        nOE = nOE + 1 + 1 + 1; // división, multiplicación
        long long sub = potencia3(a, n / 2, nOE);
        return sub * sub;
    }
    else { // exponente impar
        nOE = nOE + 1 + 1 + 1 + 1 + 1; // división, multiplicación, resta
        long long sub = potencia3(a, (n - 1) / 2, nOE);
        return sub * sub * a;
    }
}

void P2_3_PotenciaDV::ejecutarExperimento(int numPruebas) {
    std::srand(42); // Semilla fija para reproducibilidad

    for (int i = 0; i < numPruebas; i++) {
        int a = (std::rand() % 15) + 1;
        int n = (std::rand() % 15) + 1;
        int nOE = 0;

        auto inicio = std::chrono::high_resolution_clock::now();
        long long res = potencia3(a, n, nOE);
        auto fin = std::chrono::high_resolution_clock::now();

        double tiempo = std::chrono::duration<double, std::micro>(fin - inicio).count();

        std::cout << i << ": Base(a): " << a
            << "\tExponente(n): " << n
            << "\tResultado: " << res
            << "\tNumero de operaciones elementales: " << nOE
            << "\tTiempo: " << tiempo << " us" << std::endl;
    }
}
