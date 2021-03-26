package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.model.Animal;

public class Block {
    public enum Terrain {
        GRASS,
        GRASS_WITH_GRAIN,
        BUSH,
        WATER
    }

    private Terrain terrain;
    private Animal animal;

    /**
     * getters and setters
     */
    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    /**
     * constructor
     * @param terrain
     * @param animal
     */

    public Block(Terrain terrain, Animal animal) {
        this.terrain = terrain;
        this.animal = animal;
    }

    public Block(Terrain terrain) {
        this.terrain = terrain;
        this.animal = null;
    }

    @Override
    public String toString() {
        return "T: " + terrain + " A: " + animal;
    }
}
