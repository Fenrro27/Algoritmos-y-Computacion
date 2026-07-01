#pragma once
#include <vector>
#include <string>
#include <iostream>
#include <chrono>
#include <algorithm>  // para std::sort
#include <cstdlib>    // para std::rand
#include <ctime>
#include <iomanip> // Para el setPrecision
#include <fstream>


#include "GenerarVectores.h"
#include "comun.h"
#include "AlgoritmosEmpiricos.h"
#include "AlgoritmosTeoricos.h"

class TestAlgoritmos {
private:

    static std::vector<std::string> nombreAlgoritmoBusqueda;


    static std::vector<std::string> nombreAlgoritmoOrdenacion ;

public:
    // ================= BUSQUEDA =================

    static double buscaEnArrayDeInt(int key, const std::vector<int>& v, int size, algoritmosBusquedaDisponibles algo, double &tiempo);

    static void comprobarAlgoritmosBusqueda();

    static void casoBusqueda(algoritmosBusquedaDisponibles algo);//empirico
    static void casoBusqueda(algoritmosBusquedaDisponibles numAlg1, algoritmosBusquedaDisponibles numAlg2); // Empirico
    static void casoBusqueda(); // todos los casos, empirico

    static void compararBusquedaBinaria1EInterpol();

    // ================= ORDENACIÓN =================

    static double ordenarVectorDeInt(std::vector<int>& v, int size, algoritmosOrdenacionDisponibles algo);

    static void comprobarAlgoritmosOrdenacion();

    static void costeOrdenacion(algoritmosOrdenacionDisponibles algo);
    static void costeOrdenacion(algoritmosOrdenacionDisponibles numAlg1, algoritmosOrdenacionDisponibles numAlg2);
    static void costeOrdenacion(); // todos los casos
    static void experimentoKMergesortHibrido();

    void casoUnicos();
    void casoFrecuenciaK();



};
