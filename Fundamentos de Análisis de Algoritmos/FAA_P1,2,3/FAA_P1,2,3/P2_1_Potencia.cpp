#include "P2_1_Potencia.h"

long long P2_1_Potencia::potencia(int a, int n, int& nOE) {
    nOE = 0; // Inicializamos
    nOE = nOE + 1 + 2; // Asignaci�n de r, inicializaci�n de i, comparaci�n i

    long long r = 1;
    for (int i = 0; i < n; i++) {
        nOE = nOE + 2 + 2 + 1; // Multiplicaci�n r, asignaci�n r, suma i, asignaci�n i, comparaci�n i 
        r = r * a;
    }
    nOE = nOE + 1; // return r
    return r;
}

void P2_1_Potencia::ejecutarExperimento(int numPruebas) {
    std::srand(42);  // Semilla fija para reproducibilidad

    for (int i = 0; i < numPruebas; i++) {
        int a = (std::rand() % 15) + 1;  // Base aleatoria [1,15]
        int n = (std::rand() % 15) + 1;  // Exponente aleatorio [1,15]
        int nOE = -1;

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
