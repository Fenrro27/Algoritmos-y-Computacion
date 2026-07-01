#pragma once 
#include <vector>  // Usamos vector de la STL
#include <iostream>
#include <ctime>  // Para time
#include <cstdlib> // Para srand, rand
#include <cmath>
#include <random>

class GenerarVectores
{
private:
    std::vector<int> datos; // datos: ahora es un vector de enteros

public:
   
    GenerarVectores(int max = 0);

    int generaKey();
    void vaciar();
    void GeneraVector(int n);
    void GeneraVectorUniforme(int n, int min, int max);
    void GeneraVectorExponencial(int n);
    void GeneraVectorNormal(int n, double media, double desviacion);
    void VerVector();
    std::vector<int>& getDatos();
};
