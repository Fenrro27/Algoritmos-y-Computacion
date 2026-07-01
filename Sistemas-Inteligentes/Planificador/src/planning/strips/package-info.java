/**
 * Planificación STRIPS clásica (Stanford Research Institute Problem Solver).
 *
 * Algoritmo regresivo basado en una pila de objetivos: se descompone el
 * estado final en subobjetivos, se eligen acciones que los logren y se
 * apilan sus precondiciones hasta alcanzar el estado inicial.
 */
package planning.strips;
