// Kevin Jesús Banda Azogil

#include <iostream>
#include "P1_1_Busqueda.h"
#include "P1_2A_Busqueda.h"
#include "P1_2B_Busqueda.h"
#include "P1_3_BusquedaOE.h"
#include "P2_1_Potencia.h"
#include "P2_2_PotenciaRec.h"
#include "P2_3_PotenciaDV.h"
#include "P3_FAA.h"

using namespace std;

void menuPractica1() {
    int opcion;
    do {
        cout << "\n=== Menu Practica 1 ===" << endl;
        cout << "1. Ejecutar P1_1" << endl;
        cout << "2. Ejecutar P1_2A" << endl;
        cout << "3. Ejecutar P1_2B" << endl;
        cout << "4. Ejecutar P1_3" << endl;
        cout << "5. Ejecutar TODOS los experimentos" << endl;
        cout << "0. Volver al menu principal" << endl;
        cout << "Seleccione una opcion: ";
        cin >> opcion;

        switch (opcion) {
        case 1:
            P1_1_Busqueda::ejecutarExperimento("resultadosP1_1.csv");
            break;
        case 2:
            P1_2A_Busqueda::ejecutarExperimento("resultadosP1_2A.csv");
            break;
        case 3:
            P1_2B_Busqueda::ejecutarExperimento("resultadosP1_2B.csv");
            break;
        case 4:
            P1_3_BusquedaOE::ejecutarExperimento("resultadosP1_3_OE.csv");
            break;
        case 5:
            cout << "\nEjecutando todos los experimentos de Practica 1..." << endl;
            P1_1_Busqueda::ejecutarExperimento("resultadosP1_1.csv");
            P1_2A_Busqueda::ejecutarExperimento("resultadosP1_2A.csv");
            P1_2B_Busqueda::ejecutarExperimento("resultadosP1_2B.csv");
            P1_3_BusquedaOE::ejecutarExperimento("resultadosP1_3_OE.csv");
            break;
        case 0:
            cout << "Volviendo al menu principal..." << endl;
            break;
        default:
            cout << "Opcion invalida, intente de nuevo." << endl;
        }
    } while (opcion != 0);
}

void menuPractica2() {
    int opcion;
    do {
        cout << "\n=== Menu Practica 2 ===" << endl;
        cout << "1. Ejecutar Potencia Iterativa" << endl;
        cout << "2. Ejecutar Potencia Recursiva" << endl;
        cout << "3. Ejecutar Potencia Divide y Venceras" << endl;
        cout << "4. Ejecutar TODOS los experimentos" << endl;
        cout << "0. Volver al menu principal" << endl;
        cout << "Seleccione una opcion: ";
        cin >> opcion;

        switch (opcion) {
        case 1:
            P2_1_Potencia::ejecutarExperimento();
            break;
        case 2:
            P2_2_PotenciaRec::ejecutarExperimento();
            break;
        case 3:
            P2_3_PotenciaDV::ejecutarExperimento();
            break;
        case 4:
            cout << "Ejecutando TODOS los experimentos de Practica 2 (pendiente de implementar)." << endl;
            cout << "Potencia Iterativa." << endl;
            P2_1_Potencia::ejecutarExperimento();
            cout << "Potencia Recursiva." << endl;
            P2_2_PotenciaRec::ejecutarExperimento();
            cout << "Potencia Divide y Venceras." << endl;
            P2_3_PotenciaDV::ejecutarExperimento();
            break;
        case 0:
            cout << "Volviendo al menu principal..." << endl;
            break;
        default:
            cout << "Opcion invalida, intente de nuevo." << endl;
        }
    } while (opcion != 0);
}



int main() {
    int opcion;
    do {
        cout << "\n=== Menu Principal ===" << endl;
        cout << "1. Practica 1" << endl;
        cout << "2. Practica 2" << endl;
        cout << "3. Practica 3 - Comparacion de algoritmos teoricos" << endl;
        cout << "0. Salir" << endl;
        cout << "Seleccione una opcion: ";
        cin >> opcion;

        switch (opcion) {
        case 1:
            menuPractica1();
            break;
        case 2:
            menuPractica2();
            break;
        case 3:
            cout << "Comparativa de A1 y A2" << endl;
			P3_FAA::ejecutarExperimento();
            break;
        case 0:
            cout << "Saliendo de la aplicacion..." << endl;
            break;
        default:
            cout << "Opcion invalida, intente de nuevo." << endl;
        }
    } while (opcion != 0);

    return 0;
}
