package QLearning; 

import champ2011client.SensorModel;


public interface IEnvironment {


    /**
     * Devuelve el nombre de esta tarea de aprendizaje 
     */
    String getName();

    int getNumStates();
    int getNumActions();
    
	double getGamma();
	double getAlpha();
	double getEpsilon();

    /**
     * Esta es la función de DISCRETIZACIÓN específica de la tarea.
     */
    int discretizeState(SensorModel sensors);

    /**
     * Calcula la recompensa (reward) basada en los sensores actuales
     */
    double calculateReward(SensorModel sensors);

    /**
     * Comprueba si el episodio ha terminado
     */
    boolean isEpisodeDone(SensorModel sensors);

	public float[][] getActionMap();

    /**
     * Devuelve la traduccion de una accion en el mapa de acciones
     */
	public float[] getActionFromMap(int discreteAction);
	
	public void reset();

	double getDecayEpsilonFactor();

	double getMinEpsilon();


 }