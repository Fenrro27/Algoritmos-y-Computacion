//  Kevin Jesús Banda Azogil
#include <iostream>
#include "TestAlgoritmos.h"  // Usamos la nueva clase combinada

void mostrarMenuPrincipal() {
    std::cout << "*** FAA - PRACTICA 4 - Curso 24/25 ***\n\n"
        << "\t*** MENU PRINCIPAL ***\n\n"
        << "1. Menu Ordenacion\n"
        << "2. Menu Busqueda\n"
        << "0. Salir\n"
        << "-------------------------------\n"
        << "Elige una opcion: ";
}

void mostrarMenuOrdenacion() {
    std::cout << "\n*** MENU ORDENACION ***\n"
        << "1. Probar los metodos de ordenacion\n"
        << "2. Obtener el caso medio de un metodo de ordenacion\n"
        << "3. Comparar dos metodos\n"
        << "4. Comparar todos los metodos\n"
        << "5. Experimento K Mergesort Hibrido\n"
        << "0. Volver al menu principal\n"
        << "Elige una opcion: ";
}

void mostrarMenuBusqueda() {
    std::cout << "\n*** MENU BUSQUEDA ***\n"
        << "1. Probar los metodos de busqueda\n"
        << "2. Obtener el caso medio de un metodo de busqueda\n"
        << "3. Comparar dos metodos\n"
        << "4. Comparar todos los metodos\n"
        << "5. Comparar Busqueda Binaria 1 y Busqueda Interpolacion para distintas destribuciones de array\n"
        << "6. Busqueda de los elementos que se repiten una vez en el vector\n"
        << "7. Busqueda de los elementos que se repiten k veces en el vector\n"
        << "0. Volver al menu principal\n"
        << "Elige una opcion: ";
}

void mostrarMetodosOrdenacion() {
    std::cout << "\nMetodos disponibles:\n"
        << "0. MergeSort\n"
        << "1. MergeSort Hibrido\n"
        << "2. Insercion\n"
        << "3. MergeSort Modificado\n"
        << "Elige un metodo: ";
}

void mostrarMetodosBusqueda() {
    std::cout << "\nMetodos disponibles:\n"
        << "0. Busqueda Binaria 1\n"
        << "1. Busqueda Binaria 2\n"
        << "2. Busqueda Binaria Interpolacion\n"
        << "3. Busqueda Binaria 1 (Iterativa)\n"
        << "4. Busqueda Binaria 2 (Iterativa)\n"
        << "5. Busqueda Binaria Interpolacion (Iterativa)\n"
        << "Elige un metodo: ";
}

int main() {
    int op, op2;
    TestAlgoritmos x;
   // tipoCaso caso;

    do {
        system("cls");
        mostrarMenuPrincipal();
        std::cin >> op;

        switch (op) {
        case 1: { // Ordenación
            do {
                system("cls");
                mostrarMenuOrdenacion();
                std::cin >> op2;

                switch (op2) {
                case 1:
                    x.comprobarAlgoritmosOrdenacion();
                    break;
                case 2: {
                    mostrarMetodosOrdenacion();
                    int metodo;
                    std::cin >> metodo;
                    x.costeOrdenacion(static_cast<algoritmosOrdenacionDisponibles>(metodo));
                    break;
                }
                case 3: {
                    mostrarMetodosOrdenacion();
                    int metodo1, metodo2;
                    std::cout << "Metodo 1: ";
                    std::cin >> metodo1;
                    std::cout << "Metodo 2: ";
                    std::cin >> metodo2;
                    x.costeOrdenacion(static_cast<algoritmosOrdenacionDisponibles>(metodo1),
                        static_cast<algoritmosOrdenacionDisponibles>(metodo2));
                    break;
                }
                case 4:
                    x.costeOrdenacion();
                    break;
                case 5:
                    x.experimentoKMergesortHibrido();
                    break;
                case 0:
                    break;
                default:
                    std::cout << "Opcion no valida. Presione cualquier tecla para volver al menu.\n";
                    std::cin.ignore();
                    std::cin.get();
                    break;
                }

                if (op2 != 0) system("pause");

            } while (op2 != 0);
            break;
        }

        case 2: { // Búsqueda
            do {
                system("cls");
                mostrarMenuBusqueda();
                std::cin >> op2;

                switch (op2) {
                case 1:
                    x.comprobarAlgoritmosBusqueda();
                    break;
                case 2: {
                    mostrarMetodosBusqueda();
                    int metodo;
                    std::cin >> metodo;
                    x.casoBusqueda(static_cast<algoritmosBusquedaDisponibles>(metodo));
                    break;
                }
                case 3: {
                    mostrarMetodosBusqueda();
                    int metodo1, metodo2;
                    std::cout << "Metodo 1: ";
                    std::cin >> metodo1;
                    std::cout << "Metodo 2: ";
                    std::cin >> metodo2;
                    x.casoBusqueda(static_cast<algoritmosBusquedaDisponibles>(metodo1),
                        static_cast<algoritmosBusquedaDisponibles>(metodo2));
                    break;
                }
                case 4:
                    x.casoBusqueda();
                    break;
                case 5:
                    x.compararBusquedaBinaria1EInterpol();
                    break;
                case 6:
                    x.casoUnicos();
                    break;
                case 7:
                    x.casoFrecuenciaK();
                    break;
                case 0:
                    break;
                default:
                    std::cout << "Opcion no valida. Presione cualquier tecla para volver al menu.\n";
                    std::cin.ignore();
                    std::cin.get();
                    break;
                }

                if (op2 != 0) system("pause");

            } while (op2 != 0);
            break;
        }

        case 0:
            std::cout << "Gracias por usar el sistema. ¡Hasta luego!\n";
            break;

        default:
            std::cout << "Opcion no valida. Presione cualquier tecla para volver al menu.\n";
            std::cin.ignore();
            std::cin.get();
            break;
        }

    } while (op != 0);

    return 0;
}
