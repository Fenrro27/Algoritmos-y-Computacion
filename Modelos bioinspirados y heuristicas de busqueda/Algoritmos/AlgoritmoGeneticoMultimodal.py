# Kevin Jesús Banda Azogil

import numpy as np
import random
from FicherosComun import calcular_costo


def algoritmo_genetico_clearing(distancia, flujo, n, semilla, generaciones=None):
    np.random.seed(semilla)
    random.seed(semilla)

    # Parametros del algoritmo
    tam_poblacion = 50
    if generaciones is None: generaciones = 5000
    prob_cruce = 0.9
    tam_torneo = max(2, int(0.1 * tam_poblacion))
    tam_mutacion = max(1, int(0.05 * n))

    # Parametros de clearing
    sigma_ratio = 0.2  # Proporcion del tamanio del individuo usada como umbral de similitud, tamaño del nicho
    sigma = int(sigma_ratio * n)  # Umbral de distancia de Hamming (definición de nicho)
    kappa = 1  # Numero maximo de ganadores por nicho

    # Inicializacion de la poblacion y sus costos
    poblacion = [np.random.permutation(n) for _ in range(tam_poblacion)]
    costos = np.array([calcular_costo(ind, distancia, flujo, n) for ind in poblacion])
    historial_costos = [costos.min()]
    mejor_solucion = poblacion[np.argmin(costos)].copy()
    mejor_costo = costos.min()
    evaluaciones = 0

    for _ in range(generaciones):
        # Se aplica el operador de clearing para mantener diversidad, reduce duplicados locales
        poblacion, costos = aplicar_clearing(poblacion, costos, sigma, kappa)

        # Seleccion de dos padres mediante torneo
        p1, p2 = seleccion_torneo(poblacion, costos, tam_torneo)

        # Se generan los hijos
        hijos = []
        if np.random.rand() < prob_cruce:
            h1, h2 = cruce_OX(p1, p2), cruce_OX(p2, p1)
        else:
            h1, h2 = mutar_optN_dos(p1.copy(), p2.copy(), tam_mutacion)
        hijos.extend([h1, h2])

        # Evaluacion e insercion de los hijos si son mejores que el peor actual
        for hijo in hijos:
            costo_hijo = calcular_costo(hijo, distancia, flujo, n)
            evaluaciones += 1
            peor_idx = np.argmax(costos)
            # comparacion con el peor
            if costo_hijo < costos[peor_idx]:
                poblacion[peor_idx] = hijo
                costos[peor_idx] = costo_hijo
                if costo_hijo < mejor_costo:# ver si es mejor que la solucion actual
                    mejor_costo = costo_hijo
                    mejor_solucion = hijo.copy()

        # Se guarda el mejor costo de esta generacion
        historial_costos.append(mejor_costo)

    # Se retorna la mejor solucion, su costo, el historial de mejoras y el total de evaluaciones
    return mejor_solucion, mejor_costo, historial_costos, evaluaciones



def aplicar_clearing(poblacion, costos, sigma, kappa=1):
    # Se convierten a arreglos de numpy para facilidad de manejo
    poblacion = np.array(poblacion)
    costos = np.array(costos)

    # Se ordena la poblacion por costo (de mejor a peor)
    orden = np.argsort(costos)
    poblacion = poblacion[orden]
    costos = costos[orden]

    eliminados = set()  # Indices que serán "eliminados" por estar demasiado cerca a un mejor individuo

    # Se recorre la poblacion para aplicar el operador de clearing
    for i in range(len(poblacion)):
        if i in eliminados:
            continue # Saltamos si ya fue eliminado

        num_ganadores = 1  # El mejor del nicho siempre se queda
        for j in range(i + 1, len(poblacion)):
            if j in eliminados:
                continue
            # Se calcula la distancia Hamming
            hamming = sum(g1 != g2 for g1, g2 in zip(poblacion[i], poblacion[j]))
            
            # Si está dentro del nicho y ya hay demasiados ganadores, se elimina
            if hamming < sigma:
                if num_ganadores < kappa:
                    num_ganadores += 1
                else:
                    eliminados.add(j)  # El individuo j es eliminado del nicho

    # Se filtran los individuos no eliminados
    nueva_poblacion = [ind for idx, ind in enumerate(poblacion) if idx not in eliminados]
    nuevos_costos = [cost for idx, cost in enumerate(costos) if idx not in eliminados]

    # Si hay menos individuos que el tamanio original, se rellenan con clones de los mejores
    while len(nueva_poblacion) < len(poblacion):
        idx = np.random.randint(len(nueva_poblacion))
        clone = nueva_poblacion[idx].copy()
        nueva_poblacion.append(clone)
        nuevos_costos.append(nuevos_costos[idx])  # Se usa el mismo costo que el original

    # Se retorna la nueva poblacion y sus costos
    return nueva_poblacion, nuevos_costos




def seleccion_torneo(poblacion, costos, tam_torneo):
    # tam_torneo: numero de individuos q representan el x% de la poblacion
    # Se seleccionan aleatoriamente 'tam_torneo' individuos.
    indices = random.sample(range(len(poblacion)), tam_torneo)

    # Se escoge el mejor
    p1_idx = min(indices, key=lambda idx: costos[idx])  # Mejor del torneo
    indices.remove(p1_idx)
    # Se escoge como padre 2 el siguiente mejor individuo (sin reemplazo)
    p2_idx = min(indices, key=lambda idx: costos[idx])  # Segundo mejor

    return poblacion[p1_idx].copy(), poblacion[p2_idx].copy()


def cruce_OX(padre1, padre2):
    # CRUCE OX (Order Crossover) - ESTILO DE LOS APUNTES
    n = len(padre1)
    ini, fin = sorted(random.sample(range(n), 2)) # Se elige un segmento aleatorio
    hijo = [-1] * n  # Inicializa el hijo con valores vacios

    segmento = set()
    # Se copia un segmento del primer padre respetando la poblacion
    for i in range(ini, fin):
        hijo[i] = padre1[i]
        segmento.add(padre1[i]) # Se guarda el contenido para evitar duplicados


    # Se completa el hijo con los elementos del segundo padre en orden (Sin repetir)
    idx_padre2 = 0
    for i in range(n):
        if hijo[i] == -1:
            while padre2[idx_padre2] in segmento: # Avanzamos mientras q el elemento del padre2 sea el q ha dado el padre1
                idx_padre2 += 1
            hijo[i] = padre2[idx_padre2]
            idx_padre2 += 1

    return np.array(hijo)


def mutar_optN_dos(padre1, padre2, tam):
    # Cada hijo tiene una mutacion no relacionada
    # Realiza una mutacion circular por reordenamiento de subsecuencia en dos padres
    
    n = len(padre1)
    inicio = random.randint(0, n - 1) # punto de inicio aleatorio
    indices = [(inicio + i) % n for i in range(tam)]  # Indices de la subsecuencia circulares (por si la subsecuencia excede el final)

    # Se extraen las subsecuencias de los padres
    sub1 = [padre1[i] for i in indices]
    sub2 = [padre2[i] for i in indices]

    # Se mezclan aleatoriamente las subsecuencias
    random.shuffle(sub1)
    random.shuffle(sub2)

    # Se insertan las subsecuencias mutadas en los respectivos hijos
    hijo1 = padre1.copy()
    hijo2 = padre2.copy()
    for idx, i in enumerate(indices):
        hijo1[i] = sub1[idx]
        hijo2[i] = sub2[idx]

    return np.array(hijo1), np.array(hijo2)
