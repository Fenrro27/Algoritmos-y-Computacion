# Kevin Jesús Banda Azogil

import FicherosComun as fc
import AlgoritmoComparacionGreedy as greedy
import BusquedaAleatoria as bl

import numpy as np
import FicherosComun as fc
import BusquedaAleatoria as bl

def temp_inicial(u, ci, o):
    return (u / (-np.log(o))) * ci

def generar_vecino(solucion):
    vecino = solucion.copy()
    i, j = np.random.choice(len(solucion), 2, replace=False)
    vecino[i], vecino[j] = vecino[j], vecino[i]  # Intercambio de posiciones
    return vecino, i,j

def enfriamiento_simulado(distancia, flujo, n, semilla, solucion=None):
    np.random.seed(semilla)
    ev=0
    u, o = 0.3, 0.3  # Parametros para el calculo de T0

    if solucion is None:
        solucion, costo, _, evAux = bl.busqueda_aleatoria(distancia, flujo, n, semilla=semilla)
        ev=ev+evAux
    else:
        costo = fc.calcular_costo(solucion, distancia, flujo, n)
        ev=ev+1

    T0 = temp_inicial(u, costo, o)
    T = T0
    max_enfriamientos = 50 * n
    historial_costos = []

    for k in range(max_enfriamientos):
        aceptados = 0
        for _ in range(40):  # Intentos por temperatura
            vecino,i,j = generar_vecino(solucion) # Numero maximo de vecinos
            costo_vecino = fc.calcular_costo_swap(solucion, distancia, flujo, n,i,j, costo)
            ev=ev+1

            delta = costo_vecino - costo # Diferencia entre la solucion actual y la vecina

            # si tiene menos costo el nuevo vecino aceptamos, añadimos probabilidad de aceptacion en caso de q sea peor
            if delta < 0 or np.random.rand() < np.exp(-delta / T): 
                solucion, costo = vecino, costo_vecino
                aceptados += 1

            historial_costos.append(costo)  # Se almacena en cada iteracion

            if aceptados >= 5:
                break  

        T = T0 / (1 + k)  # Esquema de enfriamiento de Cauchy

    return solucion, costo, historial_costos, ev  # Devolver historial como array

