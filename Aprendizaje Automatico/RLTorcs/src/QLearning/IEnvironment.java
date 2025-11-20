package QLearning; // O en tu paquete QLearning

import champ2011client.SensorModel;

/**
 * Define una "Tarea de Entorno" que actúa como traductor
 * entre el mundo continuo de TORCS (SensorModel, Action)
 * y el mundo discreto del Agente QLearning (int state, int action).
 */
public interface IEnvironment {

    // --- 1. CONFIGURACIÓN DEL AGENTE ---

    /**
     * Devuelve el nombre de esta tarea de aprendizaje (ej. "AccelLearning").
     */
    String getName();

    /**
     * Devuelve el número total de estados discretos para esta tarea.
     * (Ej: 3 estados de velocidad * 5 de posición = 15 estados)
     */
    int getNumStates();

    /**
     * Devuelve el número total de acciones discretas para esta tarea.
     * (Ej: 3 acciones de acelerador = [Frenar, Mantener, Acelerar])
     */
    int getNumActions();
    
	double getGamma();
	double getAlpha();
	double getEpsilon();

    // --- 2. TRADUCCIÓN (MUNDO -> AGENTE) ---

    /**
     * Convierte el modelo de sensor (continuo) en un único estado (int).
     * Esta es la función de DISCRETIZACIÓN específica de la tarea.
     */
    int discretizeState(SensorModel sensors);

    /**
     * Calcula la recompensa (reward) basada en los sensores actuales,
     * específica para esta tarea.
     */
    double calculateReward(SensorModel sensors);

    /**
     * Comprueba si el episodio ha terminado (choque, fuera de pista, etc.).
     */
    boolean isEpisodeDone(SensorModel sensors);

    /**
     * Devuelve la traduccion de una accion en el mapa de acciones
     * @param discreteAction
     * @return
     */
	public float[] getActionMap(int discreteAction);

 }