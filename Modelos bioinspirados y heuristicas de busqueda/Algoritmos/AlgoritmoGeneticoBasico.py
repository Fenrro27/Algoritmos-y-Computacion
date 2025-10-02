# Kevin Jesús Banda Azogil

import numpy as np
import random
from FicherosComun import calcular_costo

def algoritmo_genetico_basico(distancia, flujo, n, semilla, generaciones=None):
    np.random.seed(semilla)
    random.seed(semilla)

    # POBLACION INICIAL
    # Se usa un esquema de poblacion ESTACIONARIA (solo se reemplazan algunos individuos por generacion)
    # Para hacerlo GENERACIONAL, se deberia guardar todos los hijos y seleccionar los mejores al final
    tam_poblacion = 50 # Valor razonable entre 30 y 200, balance entre diversidad y eficiencia

    if generaciones is None: 
        generaciones = 5000  # Numero de generaciones por defecto

    prob_cruce = 0.9  # Probabilidad de aplicar cruce
    tam_torneo = max(2, int(0.1 * tam_poblacion))  # Tamanio del torneo para seleccion con 10% de la poblacion
    tam_mutacion = max(1, int(0.05 * n))  # MUTACION optN: 5% de longitud del cromosoma

    # Se genera la poblacion inicial de soluciones aleatorias
    poblacion = [np.random.permutation(n) for _ in range(tam_poblacion)]
    # Se calculan los costos de cada solucion
    costos = np.array([calcular_costo(ind, distancia, flujo, n) for ind in poblacion])
    
    
    historial_costos = [costos.min()]  # Se guarda el mejor costo inicial
    mejor_solucion = poblacion[np.argmin(costos)].copy()  # Mejor solucion encontrada
    mejor_costo = costos.min()
    evaluaciones = tam_poblacion  # Contador de evaluaciones de soluciones

    # Bucle principal de generaciones
    for _ in range(generaciones):
        # Seleccion de dos padres por torneo
        p1, p2 = seleccion_torneo(poblacion, costos, tam_torneo)

        hijos = []

        # Se decide si se hace cruce o mutacion
        if np.random.rand() < prob_cruce:
            # CRUCE OX: conserva un segmento del primer padre y completa con orden del segundo
            h1, h2 = cruce_OX(p1, p2), cruce_OX(p2, p1)
        else:
            # MUTACION optN: se reordena una subsecuencia circular aleatoria
            h1, h2 = mutar_optN_dos(p1.copy(), p2.copy(), tam_mutacion)
        hijos.extend([h1, h2])

        # REEMPLAZO ESTACIONARIO: solo si el hijo mejora al peor actual
        for hijo in hijos:
            costo_hijo = calcular_costo(hijo, distancia, flujo, n)
            evaluaciones += 1
            peor_idx = np.argmax(costos)  # Se busca el peor individuo
            if costo_hijo < costos[peor_idx]:
                # Si el hijo es mejor que el peor, lo reemplaza
                poblacion[peor_idx] = hijo
                costos[peor_idx] = costo_hijo
                # Se actualiza el mejor si corresponde
                if costo_hijo < mejor_costo:
                    mejor_costo = costo_hijo
                    mejor_solucion = hijo.copy()

        # Se guarda el mejor costo de esta generacion
        historial_costos.append(mejor_costo)

    # Se devuelve la mejor solucion encontrada, su costo, historial de costos y cantidad de evaluaciones
    return mejor_solucion, mejor_costo, historial_costos, evaluaciones


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
