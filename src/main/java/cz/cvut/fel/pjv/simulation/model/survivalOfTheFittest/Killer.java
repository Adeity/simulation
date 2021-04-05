package cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest;

import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Map;

public interface Killer {
    /**
     * Killer will kill other animal if requirements (like attribute energy) are met
     * @param map is map of simulation
     * @param otherAnimal is animal that would get killed
     * @return true if other animal will get killed, false otherwise
     */
    boolean willKill(Map map, Animal otherAnimal);

    /**
     * Killer kills other animal
     * @param map of simulation
     * @param otherAnimal is animal that gets killed
     */
    void kill(Map map, Animal otherAnimal);

    /**
     * Increase satiety and decrease energy of killer after killing hare
     */
    void killHareAddStats();
//    void findClosestPrey();
}
