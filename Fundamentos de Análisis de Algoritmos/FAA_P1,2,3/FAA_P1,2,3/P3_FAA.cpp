#include "P3_FAA.h"

int P3_FAA::A1(int n) {
    if (n < 5) {
        return 1;
    }

    int tiempoDivision = 3 * (n * n);  // Corrigiendo ^ que era XOR, ahora multiplicación
    int tiempoRecursivo = 2 * A1(n - 1) + 3 * A1(n - 2);
    int tiempoCombinacion = 2 * n;

    return tiempoDivision + tiempoRecursivo + tiempoCombinacion;
}

long P3_FAA::factorial(int n) {
    if (n <= 1) {
        return 1;
    }
    return n * factorial(n - 1);
}

int P3_FAA::A2(int n) {
    if (n < 5) {
        return static_cast<int>(factorial(n));
    }

    int tiempoDivComb = static_cast<int>((n * n) * log(n));
    int tiempoRecursivo = 2 * A2(n / 2) + A2(n / 3);

    return tiempoDivComb + tiempoRecursivo;
}

void P3_FAA::ejecutarExperimento(const std::string& nombreArchivo, int numPruebas) {
    std::ofstream archivo(nombreArchivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    archivo << "A1;A2" << std::endl;

    for (int i = 0; i < numPruebas; i++) {
        int resA1 = A1(i);
        int resA2 = A2(i);

        std::cout << i << ": A1: " << resA1 << ", A2: " << resA2 << std::endl;
        archivo << resA1 << ";" << resA2 << std::endl;
    }

    archivo.close();
    std::cout << "Resultados guardados en: " << nombreArchivo << std::endl;
}
