from os import listdir, path  # Solo importamos las funciones que usamos
from csv import reader  # Solo importamos la función reader
import matplotlib.pyplot as plt  # Esto sigue igual porque solo usas `plt`

# Ruta de la carpeta donde están los archivos CSV
folder_path = './'  # Cambia esto a la ruta correcta de tu carpeta

# Obtener todos los archivos CSV en la carpeta
csv_files = [f for f in listdir(folder_path) if f.endswith('.csv')]

# Leer y graficar cada archivo CSV
for csv_file in csv_files:
    file_path = path.join(folder_path, csv_file)
    
    # Abrir el archivo CSV
    with open(file_path, newline='', encoding='utf-8') as f:
        data = reader(f, delimiter=';')
        
        # Leer las cabeceras (primer fila)
        headers = next(data)
        headers = [h.strip() for h in headers]  # Limpiar los nombres de las columnas
        
        # Crear listas para las columnas
        x = []  # "Tamaño del Vector"
        columns = {header: [] for header in headers[1:]}  # Almacenar las otras columnas por nombre
        
        # Leer las filas del archivo CSV
        for row in data:
            x.append(int(row[0]))  # Primer valor es el "Tamaño del Vector"
            for i, header in enumerate(headers[1:], start=1):
                columns[header].append(float(row[i]))  # Los otros valores son los tiempos (convertidos a float)
    
    # Crear una nueva figura para cada archivo CSV
    plt.figure(figsize=(10, 6))

    # Imprimir las columnas para verificar
    print(f"Columnas de {csv_file}: {headers[1:]}")

    # Graficar cada una de las otras columnas
    for col, values in columns.items():
        plt.plot(x, values, label=col)

    # Configurar el gráfico para cada archivo
    plt.title(f'Gráficas de tiempos para {csv_file}')
    plt.xlabel('Tamaño del Vector')
    plt.ylabel('Tiempo (us)')
    plt.legend()

    # Mostrar el gráfico
    plt.show()
