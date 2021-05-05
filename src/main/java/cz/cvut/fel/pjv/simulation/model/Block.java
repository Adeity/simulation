package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.model.Animal;

import java.io.Serializable;

public class Block implements Serializable, Cloneable {
    /**
     * enumaration of possible terrains of a block
     */
    public enum Terrain implements Serializable{
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


    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    private Terrain terrain;
    public Animal animal;
    public int coordX;
    public int coordY;

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
     * in this constructor with only three parameters, animal is automatically set to null
     * @param terrain is one terrains, GRASS, BUSH, WATER or GRASS_WITH_GRAIN
     */
    public Block(Terrain terrain, int coordX, int coordY) {
        this(terrain, null, coordX, coordY);
    }
    public Block(Terrain terrain, Animal animal, int coordX, int coordY) {
        this.terrain = terrain;
        this.animal = animal;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    /**
     * check wheter block is movable to. or if a newborn can be born here. there must be no water and no animal
     * @return true if block is free, false otherwise
     */
    public boolean isBlockFree() {
        if (this.terrain == Terrain.WATER) {
            return false;
        }
        return this.animal == null;
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
