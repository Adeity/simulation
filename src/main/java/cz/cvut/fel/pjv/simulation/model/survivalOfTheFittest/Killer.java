package cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest;

import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Map;

public interface Killer {
    boolean willKill(Map map, Animal otherAnimal);
    void kill(Map map, Animal otherAnimal);
    void killHareAddStats();
}
