# Kevin Jesús Banda Azogil

import numpy as np
import BusquedaLocal as bl
import FicherosComun as fc
import random


def grasp_qap(distancia, flujo, n, semilla_base, puro=False):

    ev = 0
    soluciones_finales = []
    costos_finales = []
    historial_costos = []
    distancias = []

    l = max(1, int(0.1 * n))  # Parametro l como el 10% de n

    solucion_anterior = None  # Para calcular distancias
  
    for iteracion in range(5):
        semilla = semilla_base + (semilla_base * (iteracion + 1) * 103)
        solucion_greedy = construir_solucion_greedy_prob(distancia, flujo, n, semilla, l)
        
        if not puro: 
            solucion_opt, costo_opt, historial, evAux = bl.Algo_busqueda_local_primer_mejor(distancia, flujo, n, solucion_greedy.copy())
            ev += evAux
        else:
            solucion_opt = solucion_greedy
            ev +=1
            costo_opt = fc.calcular_costo(solucion_opt, distancia, flujo, n)

        historial_costos.append(costo_opt)


        if solucion_anterior is not None:
            distancia_hamming = np.sum(solucion_opt != solucion_anterior)
            distancias.append(distancia_hamming)
        else:
            distancias.append(0)  # Si no hay solucion anterior en la primera iteración

        solucion_anterior = solucion_opt.copy()

        soluciones_finales.append(solucion_opt)
        costos_finales.append(costo_opt)

    mejor_idx = np.argmin(costos_finales)
    return soluciones_finales[mejor_idx], costos_finales[mejor_idx], historial_costos, ev, distancias



def construir_solucion_greedy_prob(distancia, flujo, n,semilla ,l=None):
    np.random.seed(semilla)
    random.seed(semilla)

    if l is None:
        l = max(1, int(0.1 * n))

    # Calcular potenciales de distancia y flujo
    pot_dist = [[sum(distancia[j]), j] for j in range(n)]
    pot_dist.sort(key=lambda x: x[0])  # menor a mayor

    pot_flujo = [[sum(flujo[i]), i] for i in range(n)]
    pot_flujo.sort(key=lambda x: x[0], reverse=True)  # mayor a menor

    flujo_list = pot_flujo[:l]
    pot_flujo = pot_flujo[l:]
    dist_list = pot_dist[:l]
    pot_dist = pot_dist[l:]

    asignaciones = []

    while len(asignaciones) < n:
        random_f = random.choice(flujo_list)
        flujo_list.remove(random_f)

        random_d = random.choice(dist_list)
        dist_list.remove(random_d)

        asignaciones.append([random_f[1], random_d[1]])

        if pot_flujo and pot_dist:
            flujo_list.append(pot_flujo.pop(0))
            dist_list.append(pot_dist.pop(0))

    asignaciones.sort(key=lambda x: x[0])
    solucion = [loc for _, loc in asignaciones]

    return np.array(solucion)
