package cz.cvut.fel.pjv.simulation.model;

import java.io.Serializable;

/**
 * Block is what map is made of. It carries information about terrain, animal currently standing on it and row and column coordinates.
 */
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
    private Animal animal;
    private int coordX;
    private int coordY;


    /**
     * in this constructor with only three parameters, animal is automatically set to null
     * @param terrain is one terrains, GRASS, BUSH, WATER or GRASS_WITH_GRAIN
     */
    public Block(Terrain terrain, int coordX, int coordY) {
        this(terrain, null, coordX, coordY);
    }
    public Block(Terrain terrain, Animal animal, int coordX, int coordY) {
        this.setTerrain(terrain);
        this.setAnimal(animal);
        this.setCoordX(coordX);
        this.setCoordY(coordY);
    }

    /**
     * check wheter block is movable to. or if a newborn can be born here. there must be no water and no animal
     * @return true if block is free, false otherwise
     */
    public boolean isBlockFree() {
        if (this.getTerrain() == Terrain.WATER) {
            return false;
        }
        return this.getAnimal() == null;
    }


    @Override
    public String toString() {
        String animal;
        String terrain;
        if(this.getAnimal() == null) {
            animal = "N";
        }
        else{
            animal = this.getAnimal().animalCode();
        }
        if(this.getTerrain() == null){
            terrain = "N";
        }
        else {
            terrain = this.getTerrain().terrainCode();
        }
        return terrain+animal;
    }

    /**
     * What terrain is currently on block
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * What terrain is currently on block
     */
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    /**
     * What animal is currently standing on block
     */
    public Animal getAnimal() {
        return animal;
    }

    /**
     * What animal is currently standing on block
     */
    public void setAnimal(Animal animal) {
        this.animal = animal;
    }


    /**
     * Row coordinate of block
     */
    public int getCoordX() {
        return coordX;
    }

    /**
     * Row coordinate of block
     */
    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    /**
     * Column coordinate of block
     */
    public int getCoordY() {
        return coordY;
    }

    /**
     * Column coordinate of block
     */
    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }
}
