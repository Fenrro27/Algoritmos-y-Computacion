import os
import json

def generate_text_traces(json_path):
    print(f"Procesando {json_path}...")
    
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
        
    problem_name = data["problem"].replace(".txt", "")
    output_dir = os.path.dirname(json_path)
    
    # -------------------------------------------------------------
    # 1. EVOLUCIÓN PASO A PASO DE BFS
    # -------------------------------------------------------------
    bfs_data = data.get("bfs", {})
    if bfs_data and bfs_data.get("exito"):
        bfs_txt_path = os.path.join(output_dir, f"{problem_name}_bfs_paso_paso.txt")
        with open(bfs_txt_path, 'w', encoding='utf-8') as btf:
            btf.write(f"Evolucion paso a paso de la Busqueda hacia adelante (BFS) para {problem_name}\n")
            btf.write("=" * 75 + "\n\n")
            for step in bfs_data["trace"]:
                btf.write(f"Paso {step['step']}:\n")
                btf.write(f"  Estado actual: {step['state']}\n")
                btf.write(f"  Accion aplicada: {step['action']}\n")
                btf.write(f"  Estado de procedencia (padre): {step['parentState']}\n")
                btf.write("-" * 50 + "\n")
        print(f"  -> Guardado {bfs_txt_path}")
        
    # -------------------------------------------------------------
    # 2. EVOLUCIÓN DE LA PILA DE STRIPS
    # -------------------------------------------------------------
    strips_data = data.get("strips", {})
    if strips_data and strips_data.get("exito"):
        trace = strips_data["trace"]
        txt_path = os.path.join(output_dir, f"{problem_name}_strips_pila.txt")
        with open(txt_path, 'w', encoding='utf-8') as tf:
            tf.write(f"Evolucion de la Pila de STRIPS para {problem_name}\n")
            tf.write("=" * 60 + "\n\n")
            for step in trace:
                tf.write(f"Paso {step['step']}:\n")
                tf.write(f"  Estado actual: {step['state']}\n")
                tf.write("  Pila (cima -> fondo):\n")
                for item in step["stack"]:
                    tf.write(f"    - {item}\n")
                tf.write("-" * 40 + "\n")
        print(f"  -> Guardado {txt_path}")

    # -------------------------------------------------------------
    # 3. EVOLUCIÓN DE PLANIFICACIÓN DE ORDEN PARCIAL (POP)
    # -------------------------------------------------------------
    pop_data = data.get("pop", {})
    if pop_data and pop_data.get("exito"):
        pop_trace = pop_data.get("trace", [])
        pop_txt_path = os.path.join(output_dir, f"{problem_name}_pop_evolucion.txt")
        with open(pop_txt_path, 'w', encoding='utf-8') as ptf:
            ptf.write(f"Evolucion de la Red de Planificacion POP para {problem_name}\n")
            ptf.write("=" * 80 + "\n\n")
            for step in pop_trace:
                ptf.write(f"Paso {step['step']}:\n")
                ptf.write(f"  Pasos activos: {step['steps']}\n")
                ptf.write(f"  Restricciones de orden: {step['constraints']}\n")
                ptf.write(f"  Enlaces causales:\n")
                for link in step["links"]:
                    ptf.write(f"    - {link}\n")
                ptf.write(f"  Precondiciones abiertas: {step['open']}\n")
                ptf.write("-" * 60 + "\n")
            
            ptf.write("\nResolucion de Conflictos (Amenazas) durante la busqueda:\n")
            ptf.write("=" * 56 + "\n")
            conflict_res = pop_data.get("conflictResolution", [])
            if conflict_res:
                for line in conflict_res:
                    ptf.write(f"  {line}\n")
            else:
                ptf.write("  No se detectaron conflictos.\n")
        print(f"  -> Guardado {pop_txt_path}")


if __name__ == "__main__":
    results_dir = "src/resultados"
    if os.path.exists(results_dir):
        for file in os.listdir(results_dir):
            if file.endswith(".json"):
                generate_text_traces(os.path.join(results_dir, file))
    else:
        print(f"El directorio {results_dir} no existe.")
