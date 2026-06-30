# GVGAI - General Video Game AI Framework

Este proyecto forma parte de la asignatura de **Sistemas Inteligentes** (Universidad de Huelva, 2026) y utiliza el framework **GVGAI (General Video Game Artificial Intelligence)** para el desarrollo de agentes inteligentes capaces de jugar a diversos videojuegos de forma autónoma.

## 🎮 ¿Qué es GVGAI?

**GVGAI** (http://www.gvgai.net/) es un entorno de investigación diseñado para el desarrollo de algoritmos de Inteligencia Artificial que puedan generalizar su comportamiento a través de múltiples juegos.

El framework se divide en varias pistas (tracks), siendo la **Single Player Planning Track** la utilizada en estas prácticas, donde el agente dispone de un tiempo limitado por paso para planificar sus movimientos.

---

## 🛠️ Prácticas Desarrolladas

En este repositorio se implementan diversos agentes localizados en el paquete `si2026.kevinjesusbandaalu`.

### 🏁 Práctica 04: Gymkhana (Agente Avanzado)
Esta es la práctica principal centrada en la resolución del entorno **Gymkhana**, un juego complejo que requiere navegación precisa, gestión de estados y superación de obstáculos dinámicos.

*   **Mundo49**: Analizador de entorno encargado de la detección de entidades, mapeo de peligros (agua) y gestión de elementos especiales como nenúfares móviles y catapultas.
*   **Motor49**: Motor de búsqueda basado en el algoritmo **A***. Implementa:
    *   **Heurísticas Jerárquicas**: Priorización de objetivos y seguridad.
    *   **Simulación de Trayectorias**: Cálculo de saltos en catapultas y sincronización con plataformas móviles.
    *   **Gestión de Transformaciones**: Adaptación del comportamiento según el estado del avatar (vuelo, orientación, etc.).

### 📂 Otras Prácticas
*   **P01 / P02**: Implementaciones iniciales de agentes reactivos y de búsqueda básica para familiarización con la API de GVGAI.

---

## 🚀 Estructura del Código del Agente

Para cumplir con los requisitos de entrega, cada agente se organiza en su propio subpaquete:
```text
src/si2026/kevinjesusbandaalu/
├── p04/
│   ├── Practica_04_exe.java  (Clase principal/Agente)
│   ├── Motor49.java          (Lógica de búsqueda A*)
│   ├── Mundo49.java          (Análisis del estado del juego)
│   └── Node.java             (Estructura para el árbol de búsqueda)
└── common/                   (Utilidades compartidas)
```

## 📖 Instrucciones de Ejecución

Para ejecutar un determinado agente para un juego concreto, se debe compilar y ejecutar desde la clase: `si2026.kevinjesusbandaalu.p0x.Practica_0x_exe`, dentro de esta tendra una ruta al agente concreto donde implementamos la tecnica de resolución del videojuego en cuestion.

---
*Desarrollado para la asignatura de Sistemas Inteligentes - Universidad de Huelva.*
