# Kevin Jesús Banda Azogil

import numpy as np
import FicherosComun as fc  
from numba import njit


def busqueda_local_mejor_vecino(distancia, flujo, n, semilla):
    np.random.seed(semilla)
    solucion = np.arange(n)
    np.random.shuffle(solucion)
    return Algo_busqueda_local_mejor_vecino(distancia, flujo, n, solucion.copy())

@njit
def Algo_busqueda_local_mejor_vecino(distancia, flujo, n, solucion):
    costo = fc.calcular_costo(solucion, distancia, flujo, n)
    ev=1 # Contamos el costo inicial

    historial_costos = []

    while True:
        mejora = False
        mejor_costo = costo
        mejor_i, mejor_j = -1, -1

        for i in range(n):
            for j in range(i + 1, n):            
                nuevo_costo = fc.calcular_costo_swap(solucion, distancia, flujo, n, i, j, costo)
                ev = ev+1
                if nuevo_costo < mejor_costo:
                    mejor_costo = nuevo_costo
                    mejor_i, mejor_j = i, j
                    mejora = True

        if mejora:
            tmp = solucion[mejor_i]
            solucion[mejor_i] = solucion[mejor_j]
            solucion[mejor_j] = tmp
            costo = mejor_costo
            historial_costos.append(costo)
        else:
            break

    return solucion, costo, historial_costos, ev


def busqueda_local_primer_mejor(distancia, flujo, n, semilla):
    np.random.seed(semilla)
    solucion = np.arange(n)
    np.random.shuffle(solucion)
    return Algo_busqueda_local_primer_mejor(distancia, flujo, n, solucion.copy())


@njit
def Algo_busqueda_local_primer_mejor(distancia, flujo, n, solucion):
    costo = fc.calcular_costo(solucion, distancia, flujo, n)
    ev=1 # Contamos la evaluacion inicial 
    historial_costos = []

    while True:
        mejora = False

        for i in range(n):
            for j in range(i + 1, n):
                nuevo_costo = fc.calcular_costo_swap(solucion, distancia, flujo, n, i, j, costo)
                ev=ev+1
                if nuevo_costo < costo:
                    tmp = solucion[i]
                    solucion[i] = solucion[j]
                    solucion[j] = tmp
                    costo = nuevo_costo
                    historial_costos.append(costo)
                    mejora = True
                    break  # Salta al siguiente ciclo si se mejora
            if mejora:
                break

        if not mejora:
            break

    return solucion, costo, historial_costos, ev
