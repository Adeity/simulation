package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.Entity;
import cz.cvut.fel.pjv.simulation.model.Animal;

public class Hare extends Animal implements Entity {

    public Hare() {
        this.energy = 14;
    }

    @Override
    protected String animalCode() {
        return "H";
    }
}
