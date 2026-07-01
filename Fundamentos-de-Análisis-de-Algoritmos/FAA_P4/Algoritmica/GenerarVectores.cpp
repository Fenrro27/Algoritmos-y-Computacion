#include "GenerarVectores.h"



/* Crea un nuevo objeto usando el constructor */
GenerarVectores::GenerarVectores(int max)
{
    // Usamos std::vector, que maneja automáticamente la memoria
    datos.resize(max); // Redimensionamos el vector al tamaño máximo
}

int GenerarVectores::generaKey() {
    // Generar un índice aleatorio dentro del rango del tamaño del vector
    int index = rand() % datos.size();
    // Devolver el valor en ese índice
    return datos[index];
}


void GenerarVectores::vaciar()
{
    datos.clear(); // Elimina todos los elementos del vector
}


void GenerarVectores::GeneraVector(int n)
{
    // Redimensionamos el vector a 'n' elementos si es necesario
    datos.resize(n);

    srand((unsigned)time(NULL)); // Inicializa la semilla para los números aleatorios
    for (int i = 0; i < n; i++) {
        datos[i] = rand() % 1000; // Genera un número aleatorio entre 0 y 999
    }
}


void GenerarVectores::GeneraVectorUniforme(int n, int min, int max)
{
    // Redimensionamos el vector a 'n' elementos si es necesario
    datos.resize(n);

    srand((unsigned)time(NULL)); // Inicializa la semilla para los números aleatorios
    for (int i = 0; i < n; i++) {
        // Genera un número aleatorio entre min y max (incluidos)
        datos[i] = min + rand() % (max - min + 1);
    }
}

void GenerarVectores::GeneraVectorExponencial(int n) {
    datos.resize(n);
    for (int i = 0; i < n; i++) {
        datos[i] = static_cast<int>(pow(2, i % 31)); // evita overflow
    }
}

void GenerarVectores::GeneraVectorNormal(int n, double media, double desviacion) {
    datos.resize(n);
    std::random_device rd;
    std::mt19937 gen(rd());
    std::normal_distribution<> dist(media, desviacion);

    for (int i = 0; i < n; i++) {
        datos[i] = static_cast<int>(dist(gen));
    }
}


void GenerarVectores::VerVector()
{
    for (int i = 0; i < datos.size(); i++)
        std::cout << datos[i] << (i < datos.size() - 1 ? ", " : "\n");
}

std::vector<int>& GenerarVectores::getDatos()
{
    return datos;
}

