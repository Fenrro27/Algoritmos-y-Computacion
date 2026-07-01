#pragma once
#include <iostream>
#include <vector>


class AlgoritmosEmpiricos
{

public:
	// Practica4
	// Algoritmos de Busqueda
	static int BusquedaBinaria1(const std::vector<int>& A, int izq, int der, int x);
	static int BusquedaBinaria2(const std::vector<int>& A, int izq, int der, int x);
	static int BusquedaBinariaInterpolacion(const std::vector<int>& A, int izq, int der, int x);
	
	static int BusquedaBinaria1Iterativo(const std::vector<int>& A, int izq, int der, int x);
	static int BusquedaBinaria2Iterativo(const std::vector<int>& A, int izq, int der, int x);
	static int BusquedaBinariaInterpolacionIterativo(const std::vector<int>& A, int izq, int der, int x);


	// Algoritmos de Ordenacion
	static void mergeSort(std::vector<int>& arr, int izquierda, int derecha);
	static void mergeSortHibridoInserctionSort(std::vector<int>& arr, int izquierda, int derecha, int k);
	static void merge(std::vector<int>& arr, int izquierda, int medio, int derecha);
	static void InsertionSort(std::vector<int>& arr);

	static void mergeSortModificado(std::vector<int>& arr, int izquierda, int derecha);
	static void mergeSortModificadoHelper(std::vector<int>& arr, std::vector<int>& aux, int izquierda, int derecha);
	static void mergeModificado(std::vector<int>& arr, std::vector<int>& aux, int izquierda, int medio, int derecha);

	static std::vector<int> encontrarUnicos(std::vector<int>& A);
	static std::vector<int> encontrarElementosConFrecuencia(std::vector<int>& A, int k);

};

