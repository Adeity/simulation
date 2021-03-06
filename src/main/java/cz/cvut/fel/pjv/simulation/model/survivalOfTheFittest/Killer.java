package cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Map;

/**
 * Entity killer in simulation is entity that can kill other animals.
 */
public interface Killer {
    /**
     * Killer will kill other animal if requirements (like attribute energy) are met
     * @param simulation is simulation
     * @param otherAnimal is animal that would get killed
     * @return true if other animal will get killed, false otherwise
     */
    boolean willKill(Simulation simulation, Animal otherAnimal);

    /**
     * Killer kills other animal
     * @param simulation is simulation
     * @param otherAnimal is animal that gets killed
     */
    void kill(Simulation simulation, Animal otherAnimal);

    /**
     * Increase energy of killer after killing prey
     */
    void killHareAddStats();
}
