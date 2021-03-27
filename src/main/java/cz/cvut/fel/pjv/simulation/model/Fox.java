package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.Entity;
import cz.cvut.fel.pjv.simulation.model.Animal;

public class Fox extends Animal implements Entity {
    public int satiety;

    public Fox() {
        super();
        this.energy = 10;
        this.satiety = 10;
    }

    @Override
    public String animalCode() {
        return "F";
    }
}
