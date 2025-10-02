# Kevin Jesús Banda Azogil

import numpy as np
import BusquedaLocal as bl

def ils_qap(distancia, flujo, n, semilla):
    np.random.seed(semilla)
    ev=0
    distancias = []
    historial_costo = []
    # Tamaño de sublista para la mutacion
    s = max(1, n // 4)

    # Generar solucion aleatoria inicial
    solucion = np.arange(n)
    np.random.shuffle(solucion)
    
    mejor_solucion, mejor_costo, historial_total, evAux = bl.Algo_busqueda_local_primer_mejor(distancia, flujo, n, solucion.copy())
    ev=ev+evAux
    historial_costo.append(mejor_costo)


    solucion_anterior = mejor_solucion.copy()
 

    for _ in range(9):
        solucion_mutada = mutacion_sublista(mejor_solucion.copy(), s)
        
        nueva_solucion, nuevo_costo, historial, evAux = bl.Algo_busqueda_local_primer_mejor(distancia, flujo, n, solucion_mutada)
        ev=ev+evAux
        historial_costo.append(nuevo_costo)


        distancia_hamming = np.sum(nueva_solucion != solucion_anterior)
        distancias.append(distancia_hamming)
        solucion_anterior = nueva_solucion.copy()

        if nuevo_costo < mejor_costo:
            mejor_solucion = nueva_solucion
            mejor_costo = nuevo_costo

        historial_total.extend(historial)

    return mejor_solucion, mejor_costo, historial_costo, ev, distancias


def mutacion_sublista(solucion, s):
    n = len(solucion)
    i = np.random.randint(0, n)
    indices = [(i + k) % n for k in range(s)]

    sublista = [solucion[idx] for idx in indices]
    np.random.shuffle(sublista)

    for idx, val in zip(indices, sublista):
        solucion[idx] = val

    return solucion
