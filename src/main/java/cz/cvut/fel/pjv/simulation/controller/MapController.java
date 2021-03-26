package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;

public class MapController {
    private Map map;

    public MapController(Map map) {
        this.map = map;
    }

    public void changeTerrainAtCoord (Block.Terrain terrain, int row, int col) {
        this.map.blocks[row][col].setTerrain(terrain);
    }

    public void changeAnimalAtCoord (Animal animal, int row, int col) {
        this.map.blocks[row][col].setAnimal(animal);
    }

    public void changeTerrainAndAnimalAtCoord (Block.Terrain terrain, Animal animal, int row, int col) {
        this.map.blocks[row][col].setTerrain(terrain);
        this.map.blocks[row][col].setAnimal(animal);
    }

    public boolean addAnimalAtCoord (Animal animal, int row, int col) {
        Animal currentAnimal = this.map.blocks[row][col].getAnimal();
        if(currentAnimal != null) {
            return false;
        }
        this.map.blocks[row][col].setAnimal(animal);
        return true;
    }
}
