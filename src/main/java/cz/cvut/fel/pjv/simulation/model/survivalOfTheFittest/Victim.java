package cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest;

import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Map;

/**
 * Entity victim in simulation is entity that can get killed by other animals.
 */
public interface Victim {
    /**
     * Victim looks at another animal and evaluates wheter it will get killed
     * @param otherAnimal the predator
     * @return true if victim is about to get killed by predator
     */
    boolean willAnimalGetKilled(Animal otherAnimal);
}
