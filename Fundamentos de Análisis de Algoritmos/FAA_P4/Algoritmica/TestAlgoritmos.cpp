#include "TestAlgoritmos.h"


// Aquí defines e inicializas los miembros estáticos
std::vector<std::string> TestAlgoritmos::nombreAlgoritmoBusqueda = {
    "Busqueda Binaria 1",
    "Busqueda Binaria 2",
    "Busqueda Binaria Interpolacion",
    "Busqueda Binaria 1 (Iterativa)",
    "Busqueda Binaria 2 (Iterativa)",
    "Busqueda Binaria Interpolacion (Iterativa)"
};

std::vector<std::string> TestAlgoritmos::nombreAlgoritmoOrdenacion = {
    "MergeSort",
    "MergeSort Hibrido",
    "InsertionSort",
    "MergeSort Modificado"

};

double TestAlgoritmos::buscaEnArrayDeInt(int key, const std::vector<int>& v, int size, algoritmosBusquedaDisponibles algo, double &tiempo) {
	AlgoritmosEmpiricos algoritmo;
	auto inicio = std::chrono::high_resolution_clock::now();
    double posicion=-1;

	switch (algo)
	{
	case BusquedaBinaria1:
		posicion=algoritmo.BusquedaBinaria1(v,0,v.size() - 1,key);
		break;
	case BusquedaBinaria2:
		posicion=algoritmo.BusquedaBinaria2(v, 0, v.size() - 1, key);
		break;
	case BusquedaBinariaInterpolacion:
		posicion=algoritmo.BusquedaBinariaInterpolacion(v, 0, v.size()-1, key);
		break;
    case BusquedaBinaria1Iterativo:
        posicion = algoritmo.BusquedaBinaria1Iterativo(v, 0, v.size() - 1, key);
        break;
    case BusquedaBinaria2Iterativo:
        posicion = algoritmo.BusquedaBinaria2Iterativo(v, 0, v.size() - 1, key);
        break;
    case BusquedaBinariaInterpolacionIterativo:
        posicion = algoritmo.BusquedaBinariaInterpolacionIterativo(v, 0, v.size() - 1, key);
        break;
	default:
		break;
	}
	auto fin = std::chrono::high_resolution_clock::now();

	// Calcula duración en nanoSegundos
	std::chrono::duration<double, std::nano> duracion = fin - inicio;
	tiempo = duracion.count();  // devuelve tiempo en nanosegundos
    return posicion;

}



void TestAlgoritmos::comprobarAlgoritmosBusqueda() {
    system("cls");
    std::cout << std::endl << "\t\t\t**** Comprobacion de los metodos de Busqueda ****" << std::endl;

    int talla;
    std::cout << "Introduce la talla: ";
    std::cin >> talla;

    GenerarVectores generador(talla);
    // Generar nuevo vector aleatorio
    generador.GeneraVector(talla);

    for (size_t metodo = 0; metodo < nombreAlgoritmoBusqueda.size(); metodo++) {
        std::vector<int> v = generador.getDatos();

        // Ordenar por necesidad de busqueda binaria
        sort(v.begin(), v.end());

        // Mostrar el vector
        std::cout << std::endl << "* Vector para el metodo " << nombreAlgoritmoBusqueda[metodo] << ": ";
        for (size_t i = 0; i < v.size(); ++i)
            std::cout << v[i] << (i < v.size() - 1 ? ", " : "\n");

        // Generar clave y realizar búsqueda
        int key = generador.generaKey();
        std::cout << std::endl << "* Clave a buscar: " << key << std::endl;
        double tiempo = -1;
        double posicion = buscaEnArrayDeInt(key, v, talla, static_cast<algoritmosBusquedaDisponibles>(metodo), tiempo);

        // Mostrar resultados
        std::cout << std::endl << "=> Posicion de " << key << " buscado con el metodo " << nombreAlgoritmoBusqueda[metodo] << ": " << posicion << std::endl;
        std::cout << "=> Tiempo de ejecucion = " << tiempo << " nanosegundos" << std::endl << std::endl;

        system("pause");
    }
}

void TestAlgoritmos::casoBusqueda(algoritmosBusquedaDisponibles algo) {
    // Parámetros base
    const int tallaIni = 1000;
    const int tallaFin = 100000;
    const int NUMEJECUCIONES = 10;
    const int incTalla = (tallaFin - tallaIni) / (NUMEJECUCIONES - 1);
    const int NUMREPETICIONES = 10;

    // Abrir el archivo CSV para guardar los resultados
    std::string nombre_archivo = "resultados/Busqueda_" + nombreAlgoritmoBusqueda[algo] + ".csv";
        std::ofstream archivo(nombre_archivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    // Escribir los encabezados del archivo CSV
    archivo << std::fixed << std::setprecision(6); // Fijar la precisión decimal
    archivo << "Talla;Tiempo Promedio (nanoS)" << std::endl;

    // Imprimir los resultados y guardarlos en el archivo CSV
    std::cout << std::endl << "Busqueda " << nombreAlgoritmoBusqueda[algo] << ". Tiempos de ejecucion promedio" << std::endl << std::endl;
    std::cout << "\tTalla\t\tTiempo (nanoS)" << std::endl << std::endl;

    // Iterar sobre las tallas de los vectores
    for (int talla = tallaIni; talla <= tallaFin; talla += incTalla) {
        double tiempoAcumulado = 0.0;
        GenerarVectores generador(talla);

        // Ejecutar varias repeticiones para obtener un promedio
        for (int rep = 0; rep < NUMREPETICIONES; rep++) {
            generador.GeneraVector(talla);
            std::vector<int> v = generador.getDatos();

            std::sort(v.begin(), v.end()); // Ordenamos el vector

            int key = generador.generaKey(); // Generamos la clave para buscar
            double tiempo = 0.0;

            // Realizar la búsqueda y medir el tiempo
            buscaEnArrayDeInt(key, v, talla, algo, tiempo);

            // Acumulamos el tiempo
            tiempoAcumulado += tiempo;
        }

        // Calcular el tiempo promedio
        double tiempoPromedio = tiempoAcumulado / NUMREPETICIONES;

        // Mostrar el resultado por consola
        std::cout << "\t" << talla << "\t\t" << std::fixed << std::setprecision(2) << tiempoPromedio << std::endl;

        // Escribir el resultado en el archivo CSV
        archivo << talla << ";" << tiempoPromedio << std::endl;
    }

    // Cerrar el archivo CSV
    archivo.close();

    std::cout << std::endl;
    system("pause");
}

void TestAlgoritmos::casoBusqueda(algoritmosBusquedaDisponibles numAlg1, algoritmosBusquedaDisponibles numAlg2) {
    // Parámetros base
    const int tallaIni = 1000;
    const int tallaFin = 100000;
    const int NUMEJECUCIONES = 10; 
    const int incTalla = (tallaFin - tallaIni) / (NUMEJECUCIONES - 1);    
    const int NUMREPETICIONES = 10;



    system("cls");
    std::cout << std::endl << "Comparacion de algoritmos de busqueda: "
        << nombreAlgoritmoBusqueda[numAlg1] << " vs " << nombreAlgoritmoBusqueda[numAlg2] << std::endl;
    std::cout << "\n\tTalla\t\t" << nombreAlgoritmoBusqueda[numAlg1] << " (nanoS)\t"
        << nombreAlgoritmoBusqueda[numAlg2] << " (nanoS)" << std::endl << std::endl;

    // Abrir el archivo CSV para guardar los resultados
    std::string nombre_archivo = "resultados/Busqueda_" + nombreAlgoritmoBusqueda[numAlg1]+ "_"+ nombreAlgoritmoBusqueda[numAlg2] + ".csv";
    std::ofstream archivo(nombre_archivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    // Escribir los encabezados del archivo CSV
    archivo << std::fixed << std::setprecision(6); // Fijar la precisión decimal
    archivo << "Talla;"<< nombreAlgoritmoBusqueda[numAlg1] <<"(nanoS);"<< nombreAlgoritmoBusqueda[numAlg2] << std::endl;

    for (int talla = tallaIni; talla <= tallaFin; talla += incTalla) {
        double tiempo1 = 0.0;
        double tiempo2 = 0.0;
        GenerarVectores generador(talla);

        for (int rep = 0; rep < NUMREPETICIONES; rep++) {
            generador.GeneraVector(talla);
            std::vector<int> v = generador.getDatos();

            // Generar la clave
            int key = generador.generaKey();

            // Ordenar el vector si es necesario (suponemos que siempre lo es aquí)
            std::sort(v.begin(), v.end());

            double t1 = 0.0, t2 = 0.0;
            buscaEnArrayDeInt(key, v, talla, numAlg1, t1);
            buscaEnArrayDeInt(key, v, talla, numAlg2, t2);

            tiempo1 += t1;
            tiempo2 += t2;
        }

        double promedio1 = tiempo1 / NUMREPETICIONES;
        double promedio2 = tiempo2 / NUMREPETICIONES;

        std::cout << "\t" << talla << "\t\t"
            << std::fixed << std::setprecision(2)
            << promedio1 << "\t\t" << promedio2 << std::endl;
        archivo << talla << ";" << promedio1 << ";"<< promedio2<< std::endl;

    }

    archivo.close();
    std::cout << std::endl;
    system("pause");
}

void TestAlgoritmos::casoBusqueda() { // todos los casos, teorico
        // Parámetros base
    // Parámetros base
    const int tallaIni = 1000;
    const int tallaFin = 100000;
    const int NUMEJECUCIONES = 10;
    const int incTalla = (tallaFin - tallaIni) / (NUMEJECUCIONES - 1);
    const int NUMREPETICIONES = 10;

        system("cls");
        std::cout << std::endl << "Comparacion de todos los algoritmos de busqueda" << std::endl;

        // Cabecera
        std::cout << "\n\tTalla\t";
        for (const auto& nombre : nombreAlgoritmoBusqueda) {
            std::cout << "\t" << nombre << " (nanoS)";
        }
        std::cout << std::endl << std::endl;

        for (int talla = tallaIni; talla <= tallaFin; talla += incTalla) {
            std::vector<double> tiempos(nombreAlgoritmoBusqueda.size(), 0.0);
            GenerarVectores generador(talla);

            for (int rep = 0; rep < NUMREPETICIONES; rep++) {
                generador.GeneraVector(talla);
                std::vector<int> v = generador.getDatos();

                // Generar la clave
                int key = generador.generaKey();

                // Ordenar 
                std::sort(v.begin(), v.end());

                for (size_t i = 0; i < nombreAlgoritmoBusqueda.size(); ++i) {
                    double tiempo = 0.0;
                    buscaEnArrayDeInt(key, v, talla, static_cast<algoritmosBusquedaDisponibles>(i), tiempo);
                    tiempos[i] += tiempo;
                }
            }

            std::cout << "\t" << talla;
            for (const auto& t : tiempos) {
                std::cout << "\t\t" << std::fixed << std::setprecision(2) << (t / NUMREPETICIONES);
            }
            std::cout << std::endl;
        }

        std::cout << std::endl;
        system("pause");
    }



// =================================================== ORDENACIÓN ===================================================

double TestAlgoritmos::ordenarVectorDeInt(std::vector<int>& v, int size, algoritmosOrdenacionDisponibles algo) {
    AlgoritmosEmpiricos algoritmo;
    auto inicio = std::chrono::high_resolution_clock::now();

    switch (algo) {
    case mergeSort:
        algoritmo.mergeSort(v, 0, size - 1);
        break;
    case mergeSortHibridoInserctionSort:
        algoritmo.mergeSortHibridoInserctionSort(v, 0, size - 1, 5); // condicion de inserccion k =5
        break;
    case InsertionSort:
        algoritmo.InsertionSort(v);
        break;
    case mergeSortModificado:
        algoritmo.mergeSortModificado(v, 0, size - 1);
        break;
    default:
        break;
    }

    auto fin = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double, std::nano> duracion = fin - inicio;
    return duracion.count(); // tiempo en nanosegundos
}

void TestAlgoritmos::comprobarAlgoritmosOrdenacion() {
    system("cls");
    std::cout << std::endl << "\t\t\t**** Comprobacion de los metodos de Ordenacion ****" << std::endl;

    int talla;
    std::cout << "Introduce la talla: ";
    std::cin >> talla;

    GenerarVectores generador(talla);
    generador.GeneraVector(talla);

    for (int metodo = mergeSort; metodo <= mergeSortModificado; metodo++) {
        std::vector<int> v = generador.getDatos();

        std::cout << std::endl << "* Vector original: ";
        for (size_t i = 0; i < v.size(); ++i)
            std::cout << v[i] << (i < v.size() - 1 ? ", " : "\n");

        double tiempo = ordenarVectorDeInt(v, talla, static_cast<algoritmosOrdenacionDisponibles>(metodo));

        std::cout << std::endl << "=> Vector ordenado con " << nombreAlgoritmoOrdenacion[metodo] << ": ";
        for (size_t i = 0; i < v.size(); ++i)
            std::cout << v[i] << (i < v.size() - 1 ? ", " : "\n");

        std::cout << "=> Tiempo de ejecucion = " << std::fixed << std::setprecision(2) << tiempo << " nanosegundos" << std::endl << std::endl;

        system("pause");
    }
}

void TestAlgoritmos::costeOrdenacion(algoritmosOrdenacionDisponibles algo) {
    const int tallaIni = 1000;
    const int tallaFin = 10000;
    const int incTalla = 1000;
    const int NUMREPETICIONES = 10;

    // Crear nombre del archivo CSV
    std::string nombre_archivo = "resultados/Ordenacion_" + nombreAlgoritmoOrdenacion[algo] + ".csv";
    std::ofstream archivo(nombre_archivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    // Escribir encabezado CSV
    archivo << std::fixed << std::setprecision(6); // Fijar la precisión decimal
    archivo << "Talla;Tiempo Promedio (nanoS)" << std::endl;

    system("cls");
    std::cout << std::endl << "Ordenacion " << nombreAlgoritmoOrdenacion[algo] << ". Tiempos de ejecucion promedio" << std::endl << std::endl;
    std::cout << "\tTalla\t\tTiempo (nanoS)" << std::endl << std::endl;

    for (int talla = tallaIni; talla <= tallaFin; talla += incTalla) {
        double tiempoAcumulado = 0.0;
        GenerarVectores generador(talla);

        for (int rep = 0; rep < NUMREPETICIONES; rep++) {
            generador.GeneraVector(talla);
            std::vector<int> v = generador.getDatos();

            tiempoAcumulado += ordenarVectorDeInt(v, talla, algo);
        }

        double tiempoPromedio = tiempoAcumulado / NUMREPETICIONES;
        std::cout << "\t" << talla << "\t\t" << std::fixed << std::setprecision(2) << tiempoPromedio << std::endl;

        // Guardar el resultado en el archivo CSV
        archivo << talla << ";" << tiempoPromedio << std::endl;
    }

    archivo.close();
    std::cout << std::endl;
    system("pause");
}


void TestAlgoritmos::costeOrdenacion(algoritmosOrdenacionDisponibles numAlg1, algoritmosOrdenacionDisponibles numAlg2) {
    const int tallaIni = 1000;
    const int tallaFin = 10000;
    const int incTalla = 1000;
    const int NUMREPETICIONES = 10;

    // Crear nombre del archivo CSV
    std::string nombre_archivo = "resultados/Ordenacion_" + nombreAlgoritmoOrdenacion[numAlg1] + "_vs_" + nombreAlgoritmoOrdenacion[numAlg2] + ".csv";
    std::ofstream archivo(nombre_archivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo." << std::endl;
        return;
    }

    // Escribir encabezado CSV
    archivo << std::fixed << std::setprecision(6); // Fijar la precisión decimal
    archivo << "Talla;" << nombreAlgoritmoOrdenacion[numAlg1] << "(nanoS);" << nombreAlgoritmoOrdenacion[numAlg2] << "(nanoS)" << std::endl;

    system("cls");
    std::cout << std::endl << "Comparacion de algoritmos de ordenacion: "
        << nombreAlgoritmoOrdenacion[numAlg1] << " vs " << nombreAlgoritmoOrdenacion[numAlg2] << std::endl;
    std::cout << "\n\tTalla\t\t" << nombreAlgoritmoOrdenacion[numAlg1] << " (nanoS)\t"
        << nombreAlgoritmoOrdenacion[numAlg2] << " (nanoS)" << std::endl << std::endl;

    for (int talla = tallaIni; talla <= tallaFin; talla += incTalla) {
        double tiempo1 = 0.0;
        double tiempo2 = 0.0;
        GenerarVectores generador(talla);

        for (int rep = 0; rep < NUMREPETICIONES; rep++) {
            generador.GeneraVector(talla);
            std::vector<int> v1 = generador.getDatos();
            std::vector<int> v2 = v1; // copia para comparar con igualdad de condiciones

            tiempo1 += ordenarVectorDeInt(v1, talla, numAlg1);
            tiempo2 += ordenarVectorDeInt(v2, talla, numAlg2);
        }

        double promedio1 = tiempo1 / NUMREPETICIONES;
        double promedio2 = tiempo2 / NUMREPETICIONES;

        std::cout << "\t" << talla << "\t\t"
            << std::fixed << std::setprecision(2)
            << promedio1 << "\t\t" << promedio2 << std::endl;

        // Guardar el resultado en el archivo CSV
        archivo << talla << ";" << promedio1 << ";" << promedio2 << std::endl;
    }

    archivo.close();
    std::cout << std::endl;
    system("pause");
}


void TestAlgoritmos::costeOrdenacion() {
    const int tallaIni = 1000;
    const int tallaFin = 10000;
    const int incTalla = 1000;
    const int NUMREPETICIONES = 10;

    system("cls");
    std::cout << std::endl << "Comparacion de todos los algoritmos de ordenacion" << std::endl;
    std::cout << "\n\tTalla\t";
    for (const auto& nombre : nombreAlgoritmoOrdenacion) {
        std::cout << "\t" << nombre << " (nanoS)";
    }
    std::cout << std::endl << std::endl;

    for (int talla = tallaIni; talla <= tallaFin; talla += incTalla) {
        std::vector<double> tiempos(nombreAlgoritmoOrdenacion.size(), 0.0);
        GenerarVectores generador(talla);

        for (int rep = 0; rep < NUMREPETICIONES; rep++) {
            generador.GeneraVector(talla);
            std::vector<std::vector<int>> vectores;

            // Crear una copia para cada algoritmo
            for (size_t i = 0; i < nombreAlgoritmoOrdenacion.size(); ++i) {
                vectores.push_back(generador.getDatos());
            }

            for (size_t i = 0; i < nombreAlgoritmoOrdenacion.size(); ++i) {
                tiempos[i] += ordenarVectorDeInt(vectores[i], talla, static_cast<algoritmosOrdenacionDisponibles>(i));
            }
        }

        std::cout << "\t" << talla;
        for (const auto& t : tiempos) {
            std::cout << "\t\t" << std::fixed << std::setprecision(2) << (t / NUMREPETICIONES);
        }
        std::cout << std::endl;
    }

    std::cout << std::endl;
    system("pause");
}

// Función que compara el rendimiento de la Búsqueda Binaria 1 y la Búsqueda Binaria Interpolación
void TestAlgoritmos::compararBusquedaBinaria1EInterpol() {
    system("cls");
    std::cout << std::endl << "Comparacion entre Busqueda Binaria 1 y Busqueda Binaria Interpolacion:" << std::endl;

    int talla;
    std::cout << "Introduce la talla: ";
    std::cin >> talla;

    // Crear generador de vectores con el tamaño especificado
    GenerarVectores generador(talla);

    // Arrays con diferentes distribuciones
    std::cout << "Generando arrays con diferentes distribuciones...\n";

    // 1. Arrays con valores uniformemente distribuidos
    generador.GeneraVectorUniforme(talla, 0, 1000);
    std::vector<int> vUniforme = generador.getDatos();
    sort(vUniforme.begin(), vUniforme.end());
    int keyUniforme = generador.generaKey();


    // 2. Arrays con valores exponencialmente distribuidos
    generador.GeneraVectorExponencial(talla);
    std::vector<int> vExponencial = generador.getDatos();
    sort(vExponencial.begin(), vExponencial.end());
    int keyExponencial = generador.generaKey();


    // 3. Arrays con valores distribuidos normalmente
    generador.GeneraVectorNormal(talla, 500, 100);
    std::vector<int> vNormal = generador.getDatos();
    sort(vNormal.begin(), vNormal.end());
    int keyNormal = generador.generaKey();


    // Comparación para el algoritmo Búsqueda Binaria 1
    std::cout << std::endl << "* Comparacion para el algoritmo " << nombreAlgoritmoBusqueda[BusquedaBinaria1] << ":" << std::endl;


    // Realizar búsquedas en cada distribución usando Búsqueda Binaria 1
    double tiempo1 = 0.0, tiempo2 = 0.0;
    double posUniforme = buscaEnArrayDeInt(keyUniforme, vUniforme, talla, BusquedaBinaria1, tiempo1);
    double posExponencial = buscaEnArrayDeInt(keyExponencial, vExponencial, talla, BusquedaBinaria1, tiempo2);
    double posNormal = buscaEnArrayDeInt(keyNormal, vNormal, talla, BusquedaBinaria1, tiempo2);

    // Mostrar resultados para Búsqueda Binaria 1
    std::cout << "\tDistribucion Uniforme: ";
    std::cout << "Posicion = " << posUniforme << ", Tiempo = " << tiempo1 << " nanosegundos" << std::endl;
    std::cout << "\tDistribucion Exponencial: ";
    std::cout << "Posicion = " << posExponencial << ", Tiempo = " << tiempo2 << " nanosegundos" << std::endl;
    std::cout << "\tDistribucion Normal: ";
    std::cout << "Posicion = " << posNormal << ", Tiempo = " << tiempo2 << " nanosegundos" << std::endl;

    // Comparación para el algoritmo Búsqueda Binaria Interpolación
    std::cout << std::endl << "* Comparacion para el algoritmo " << nombreAlgoritmoBusqueda[BusquedaBinariaInterpolacion] << ":" << std::endl;

    // Realizar búsquedas en cada distribución usando Búsqueda Binaria Interpolación
    double tiempo3 = 0.0, tiempo4 = 0.0;
    double posUniformeInterpolacion = buscaEnArrayDeInt(keyUniforme, vUniforme, talla, BusquedaBinariaInterpolacion, tiempo3);
    double posExponencialInterpolacion = buscaEnArrayDeInt(keyExponencial, vExponencial, talla, BusquedaBinariaInterpolacion, tiempo4);
    double posNormalInterpolacion = buscaEnArrayDeInt(keyNormal, vNormal, talla, BusquedaBinariaInterpolacion, tiempo4);

    // Mostrar resultados para Búsqueda Binaria Interpolación
    std::cout << "\tDistribucion Uniforme: ";
    std::cout << "Posicion = " << posUniformeInterpolacion << ", Tiempo = " << tiempo3 << " nanosegundos" << std::endl;
    std::cout << "\tDistribucion Exponencial: ";
    std::cout << "Posicion = " << posExponencialInterpolacion << ", Tiempo = " << tiempo4 << " nanosegundos" << std::endl;
    std::cout << "\tDistribucion Normal: ";
    std::cout << "Posicion = " << posNormalInterpolacion << ", Tiempo = " << tiempo4 << " nanosegundos" << std::endl;

    std::cout << std::endl;
    system("pause");
}

void TestAlgoritmos::experimentoKMergesortHibrido() {
    AlgoritmosEmpiricos algoritmo;
    const int REPETICIONES = 4;
    std::vector<int> tamanos = { 100, 500, 1000, 5000, 10000 };

    // Crear archivo CSV
    std::string nombre_archivo = "resultados/ExperimentoK_MergeSortHibrido.csv";
    std::ofstream archivo(nombre_archivo);
    if (!archivo.is_open()) {
        std::cerr << "Error al abrir el archivo CSV." << std::endl;
        return;
    }

    // Encabezado CSV
    archivo << std::fixed << std::setprecision(2);
    archivo << "Tamano;K" << std::endl;

    // Para cada tamaño de array
    for (int tamano : tamanos) {
        std::cout << "\nProbando para tamanno de array: " << tamano << std::endl;

        int mejorK = -1;
        long long mejorTiempo = LLONG_MAX;

        for (int k = 10; k <= 100; k += 10) {
            long long tiempoTotal = 0;

            for (int rep = 0; rep < REPETICIONES; ++rep) {
                GenerarVectores generador(tamano);
                generador.GeneraVector(tamano);
                std::vector<int> arr = generador.getDatos();

                auto start = std::chrono::high_resolution_clock::now();
                algoritmo.mergeSortHibridoInserctionSort(arr, 0, arr.size() - 1, k);
                auto end = std::chrono::high_resolution_clock::now();

                auto duracion = std::chrono::duration_cast<std::chrono::nanoseconds>(end - start).count();
                tiempoTotal += duracion;
            }

            long long promedio = tiempoTotal / REPETICIONES;
            std::cout << "k = " << k << ", Tiempo promedio = " << promedio << " ns" << std::endl;


            if (promedio < mejorTiempo) {
                mejorTiempo = promedio;
                mejorK = k;
            }
        }

        archivo << tamano << ";" << mejorK << std::endl;
        std::cout << ">> Mejor k para tamaño " << tamano << ": " << mejorK << " (" << mejorTiempo << " ns)" << std::endl;
    }

    archivo.close();
    std::cout << "\nResultados guardados en: " << nombre_archivo << std::endl;
    system("pause");
}


void TestAlgoritmos::casoUnicos() {
    system("cls");
    std::cout << "\n*** Ejecucion del algoritmo encontrarUnicos() ***\n\n";

    int talla;
    std::cout << "Introduce la talla del vector: ";
    std::cin >> talla;

    GenerarVectores generador(talla);
    generador.GeneraVectorUniforme(talla,0,talla);
    std::vector<int> datos = generador.getDatos();

    // Mostrar el vector generado
    std::cout << "\nVector generado:\n";
    for (size_t i = 0; i < datos.size(); ++i)
        std::cout << datos[i] << (i < datos.size() - 1 ? ", " : "\n");

    AlgoritmosEmpiricos algoritmo;

    auto inicio = std::chrono::high_resolution_clock::now();
    std::vector<int> resultado = algoritmo.encontrarUnicos(datos);
    auto fin = std::chrono::high_resolution_clock::now();

    std::chrono::duration<double, std::nano> duracion = fin - inicio;

    std::cout << "\nTiempo de ejecucion: " << std::fixed << std::setprecision(2)
        << duracion.count() << " nanosegundos\n";

    std::cout << "\nElementos unicos encontrados:\n";
    for (size_t i = 0; i < resultado.size(); ++i)
        std::cout << resultado[i] << (i < resultado.size() - 1 ? ", " : "\n");

    std::cout << std::endl;
    system("pause");
}

void TestAlgoritmos::casoFrecuenciaK() {
    system("cls");
    std::cout << "\n*** Ejecucion del algoritmo encontrarElementosConFrecuencia(k) ***\n\n";

    int talla, k;
    std::cout << "Introduce la talla del vector: ";
    std::cin >> talla;

    GenerarVectores generador(talla);
    generador.GeneraVectorUniforme(talla, 0, talla);
    std::vector<int> datos = generador.getDatos();

    // Mostrar el vector generado
    std::cout << "\nVector generado:\n";
    for (size_t i = 0; i < datos.size(); ++i)
        std::cout << datos[i] << (i < datos.size() - 1 ? ", " : "\n");

    std::cout << "Introduce el valor de k (frecuencia exacta a buscar): ";
    std::cin >> k;
    AlgoritmosEmpiricos algoritmo;

    auto inicio = std::chrono::high_resolution_clock::now();
    std::vector<int> resultado = algoritmo.encontrarElementosConFrecuencia(datos, k);
    auto fin = std::chrono::high_resolution_clock::now();

    std::chrono::duration<double, std::nano> duracion = fin - inicio;

    std::cout << "\nTiempo de ejecucion: " << std::fixed << std::setprecision(2)
        << duracion.count() << " nanosegundos\n";

    std::cout << "\nElementos que aparecen exactamente " << k << " veces:\n";
    for (size_t i = 0; i < resultado.size(); ++i)
        std::cout << resultado[i] << (i < resultado.size() - 1 ? ", " : "\n");

    std::cout << std::endl;
    system("pause");
}
