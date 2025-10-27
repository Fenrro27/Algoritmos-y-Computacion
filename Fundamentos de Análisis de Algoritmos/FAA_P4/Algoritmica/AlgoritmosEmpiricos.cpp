#include "AlgoritmosEmpiricos.h"



int AlgoritmosEmpiricos::BusquedaBinaria1(const std::vector<int>& A, int izq, int der, int x) {
    if (izq > der)
        return -1;

    int medio = (izq + der) / 2;

    if (A[medio] == x)
        return medio;
    else if (A[medio] > x)
        return BusquedaBinaria1(A, izq, medio - 1, x);
    else
        return BusquedaBinaria1(A, medio + 1, der, x);
}


int AlgoritmosEmpiricos::BusquedaBinaria2(const std::vector<int>& A, int izq, int der, int x) {
    if (izq > der)
        return -1;

    int medio = (izq + der) / 2;

    if (A[medio] == x && (medio == izq || A[medio - 1] != x))
        return medio;
    else if (A[medio] >= x)
        return BusquedaBinaria2(A, izq, medio - 1, x);
    else
        return BusquedaBinaria2(A, medio + 1, der, x);
}

int AlgoritmosEmpiricos::BusquedaBinariaInterpolacion(const std::vector<int>& A, int izq, int der, int x) {
    if (izq > der || x < A[izq] || x > A[der])
        return -1;

    // Evita división por cero
    if (A[der] == A[izq])
        return (A[izq] == x) ? izq : -1;

    // Calcular la posición estimada
    int pos = izq + ((double)(x - A[izq]) * (der - izq)) / (A[der] - A[izq]);

    // Validar por posibles problemas numéricos
    if (pos < izq || pos > der)
        pos = (izq + der) / 2;

    if (A[pos] == x)
        return pos;
    else if (A[pos] > x)
        return BusquedaBinariaInterpolacion(A, izq, pos - 1, x);
    else
        return BusquedaBinariaInterpolacion(A, pos + 1, der, x);
}

int AlgoritmosEmpiricos::BusquedaBinaria1Iterativo(const std::vector<int>& A, int izq, int der, int x) {
    while (izq <= der) {
        int medio = (izq + der) / 2;

        if (A[medio] == x)
            return medio;
        else if (A[medio] > x)
            der = medio - 1;
        else
            izq = medio + 1;
    }
    return -1;
}

int AlgoritmosEmpiricos::BusquedaBinaria2Iterativo(const std::vector<int>& A, int izq, int der, int x) {
    int resultado = -1;

    while (izq <= der) {
        int medio = (izq + der) / 2;

        if (A[medio] == x) {
            resultado = medio;
            der = medio - 1;  // sigue buscando hacia la izquierda
        }
        else if (A[medio] > x) {
            der = medio - 1;
        }
        else {
            izq = medio + 1;
        }
    }

    return resultado;
}

int AlgoritmosEmpiricos::BusquedaBinariaInterpolacionIterativo(const std::vector<int>& A, int izq, int der, int x) {
    while (izq <= der && x >= A[izq] && x <= A[der]) {
        if (A[der] == A[izq]) {
            return (A[izq] == x) ? izq : -1;
        }

        int pos = izq + ((double)(x - A[izq]) * (der - izq)) / (A[der] - A[izq]);

        if (pos < izq || pos > der)
            break;

        if (A[pos] == x)
            return pos;
        else if (A[pos] < x)
            izq = pos + 1;
        else
            der = pos - 1;
    }

    return -1;
}


void AlgoritmosEmpiricos::mergeSort(std::vector<int>& arr, int izquierda, int derecha) {
    if (izquierda < derecha) {
        int medio = izquierda + (derecha - izquierda) / 2;
        mergeSort(arr, izquierda, medio);
        mergeSort(arr, medio + 1, derecha);
        merge(arr, izquierda, medio, derecha);
    }
}
void AlgoritmosEmpiricos::mergeSortHibridoInserctionSort(std::vector<int>& arr, int izquierda, int derecha, int k) {
    if (( derecha-izquierda+1 )<= k)
    {
        InsertionSort(arr);
    }
    else if (izquierda < derecha) {
        int medio = izquierda + (derecha - izquierda) / 2;
        mergeSortHibridoInserctionSort(arr, izquierda, medio, k);
        mergeSortHibridoInserctionSort(arr, medio + 1, derecha, k);
        merge(arr, izquierda, medio, derecha);
    }
}


void AlgoritmosEmpiricos::merge(std::vector<int>& arr, int izquierda, int medio, int derecha) {
    int n1 = medio - izquierda + 1;
    int n2 = derecha - medio;

    std::vector<int> L(n1), R(n2);
    for (int i = 0; i < n1; i++) L[i] = arr[izquierda + i];
    for (int j = 0; j < n2; j++) R[j] = arr[medio + 1 + j];

    int i = 0, j = 0, k = izquierda;
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) {
            arr[k++] = L[i++];
        }
        else {
            arr[k++] = R[j++];
        }
    }

    while (i < n1) { arr[k++] = L[i++]; }
    while (j < n2) { arr[k++] = R[j++]; }
}

// Otros algoritmos

void AlgoritmosEmpiricos::InsertionSort(std::vector<int>& arr) {
    int n = arr.size();
    for (int i = 1; i < n; ++i) {
        int key = arr[i];      // Elemento a insertar
        int j = i - 1;

        // Mover los elementos mayores a la derecha
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }

        // Insertar el elemento en su posición correcta
        arr[j + 1] = key;
    }
}

void AlgoritmosEmpiricos::mergeSortModificado(std::vector<int>& arr, int izquierda, int derecha) {
    std::vector<int> aux(arr.size());
    mergeSortModificadoHelper(arr, aux, izquierda, derecha);
}

void AlgoritmosEmpiricos::mergeSortModificadoHelper(std::vector<int>& arr, std::vector<int>& aux, int izquierda, int derecha) {
    if (izquierda < derecha) {
        int medio = izquierda + (derecha - izquierda) / 2;

        mergeSortModificadoHelper(arr, aux, izquierda, medio);
        mergeSortModificadoHelper(arr, aux, medio + 1, derecha);
        mergeModificado(arr, aux, izquierda, medio, derecha);
    }
}



void AlgoritmosEmpiricos::mergeModificado(std::vector<int>& arr, std::vector<int>& aux, int izquierda, int medio, int derecha) {
    int i = izquierda;     // Índice para la primera mitad
    int j = medio + 1;     // Índice para la segunda mitad
    int k = izquierda;     // Índice para el array auxiliar

    // Combinar los elementos en orden en aux
    while (i <= medio && j <= derecha) {
        if (arr[i] <= arr[j]) {
            aux[k++] = arr[i++];
        }
        else {
            aux[k++] = arr[j++];
        }
    }

    // Copiar los elementos restantes de la primera mitad, si los hay
    while (i <= medio) {
        aux[k++] = arr[i++];
    }

    // Copiar los elementos restantes de la segunda mitad, si los hay
    while (j <= derecha) {
        aux[k++] = arr[j++];
    }

    // Copiar el resultado ordenado de vuelta al array original
    for (int l = izquierda; l <= derecha; ++l) {
        arr[l] = aux[l];
    }
}


std::vector<int> AlgoritmosEmpiricos::encontrarUnicos(std::vector<int>& A) {
    // Ordenaamos
    mergeSort(A, 0,A.size() - 1);

    std::vector<int> resultado;
    int n = A.size();

    for (int i = 0; i < n; ) {
        int j = i + 1;
        // Contar cuántas veces se repite A[i]
        while (j < n && A[j] == A[i]) {
            j++;
        }
        // Si no se repite (solo aparece una vez)
        if (j == i + 1) {
            resultado.push_back(A[i]);
        }
        i = j;  // Avanzar al siguiente grupo
    }

    return resultado;
}


std::vector<int> AlgoritmosEmpiricos::encontrarElementosConFrecuencia(std::vector<int>& A, int k) {
    mergeSort(A, 0, A.size() - 1);

    std::vector<int> resultado;
    int n = A.size();

    for (int i = 0; i < n; ) {
        int j = i + 1;
        while (j < n && A[j] == A[i]) {
            j++;
        }

        int frecuencia = j - i;
        if (frecuencia == k) {
            resultado.push_back(A[i]);
        }

        i = j;  // Saltar al siguiente grupo distinto
    }

    return resultado;
}
