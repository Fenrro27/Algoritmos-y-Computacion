# Kevin Jesús Banda Azogil

import numpy as np
import BusquedaLocal as bl  
import FicherosComun as fc

def vns_qap(distancia, flujo, n, semilla):
    np.random.seed(semilla)
    ev = 0
    kmax = 5
    blmax = 10
    distancias = []
    historial_costo=[]

    solucion_actual = np.arange(n)
    np.random.shuffle(solucion_actual)

    costo_actual = fc.calcular_costo(solucion_actual, distancia, flujo, n)
    historial_costo.append(costo_actual)


    bl_count = 0
    k = 1

    while bl_count < blmax:
        if k > kmax:
            k = 1  
            # no repetir, cortar el bucle


        s = int(n / (9 - k))  
        vecino = mutacion_sublista(solucion_actual.copy(), s)

        solucion_nueva, costo_nuevo, historial, evAux = bl.Algo_busqueda_local_primer_mejor(distancia, flujo, n, vecino)
        historial_costo.append(costo_nuevo)

        ev += evAux
        bl_count += 1  

        if costo_nuevo < costo_actual:
            solucion_actual = solucion_nueva
            costo_actual = costo_nuevo
            k = 1
        else:
            k += 1

        distancia_hamming = np.sum(solucion_actual != solucion_nueva)
        distancias.append(distancia_hamming)

    return solucion_actual, costo_actual, historial_costo, ev, distancias



def mutacion_sublista(solucion, s):
    n = len(solucion)
    i = np.random.randint(0, n)
    indices = [(i + k) % n for k in range(s)]

    sublista = [solucion[idx] for idx in indices]
    np.random.shuffle(sublista)

    for idx, val in zip(indices, sublista):
        solucion[idx] = val

    return solucion
