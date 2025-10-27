import pandas as pd
import numpy as np
from scipy.stats import entropy

def analyze_qtable(csv_path, reward_column='value'):
    """
    Analiza un CSV tipo Q-table:
    - Calcula entropía de cada columna respecto a la recompensa.
    - Muestra relevancia relativa de cada atributo.
    
    Args:
        csv_path (str): Ruta al CSV.
        reward_column (str): Nombre de la columna de recompensa.
    """
    df = pd.read_csv(csv_path)
    
    # Columnas a analizar (todas excepto reward)
    feature_columns = [col for col in df.columns if col != reward_column]
    
    entropies = {}
    
    for col in feature_columns:
        # Discretizamos reward para hacer histograma conjunto
        reward_bins = np.histogram(df[reward_column], bins=10)[0] + 1e-9
        feature_vals = df[col].unique()
        joint_entropy = 0.0

        for val in feature_vals:
            subset = df[df[col] == val][reward_column]
            hist = np.histogram(subset, bins=10)[0] + 1e-9
            prob = hist / hist.sum()
            joint_entropy += (len(subset)/len(df)) * entropy(prob)
        
        entropies[col] = joint_entropy
    
    # Ordenar por relevancia (menor entropía -> más informativa)
    sorted_entropy = sorted(entropies.items(), key=lambda x: x[1])
    
    print("Entropía de cada atributo (menor -> más relevante):")
    for col, ent in sorted_entropy:
        print(f"{col}: {ent:.4f}")
    
    return sorted_entropy

if __name__ == "__main__":
    csv_file = "QTable_Steer.csv"  # Cambia a tu CSV
    analyze_qtable(csv_file)
