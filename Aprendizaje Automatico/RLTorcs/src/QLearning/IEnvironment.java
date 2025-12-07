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

    int getNumStates();
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

	public float[][] getActionMap();

    /**
     * Devuelve la traduccion de una accion en el mapa de acciones
     * @param discreteAction
     * @return
     */
	public float[] getActionFromMap(int discreteAction);
	
	public void reset();

	double getDecayEpsilonFactor();

	double getMinEpsilon();


 }