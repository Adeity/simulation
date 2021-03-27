package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.model.Animal;

public class Block {
    public enum Terrain {
        GRASS,
        GRASS_WITH_GRAIN,
        BUSH,
        WATER;


        public String terrainCode() {
            String res ="";
            switch (this) {
                case GRASS:
                    res = "G";
                    break;
                case BUSH:
                    res = "B";
                    break;
                case WATER:
                    res = "W";
                    break;
                case GRASS_WITH_GRAIN:
                    res = "R";
                    break;
            }
            return res;

        }
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
        String animal;
        String terrain;
        if(this.animal == null) {
            animal = "N";
        }
        else{
            animal = this.animal.animalCode();
        }
        if(this.terrain == null){
            terrain = "N";
        }
        else {
            terrain = this.terrain.terrainCode();
        }
        return terrain+animal;
    }
}
