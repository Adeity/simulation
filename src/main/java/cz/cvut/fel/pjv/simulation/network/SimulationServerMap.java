package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Map;

import java.util.HashMap;

public class SimulationServerMap {
    HashMap<Integer, SimulationPositionedMap> maps;

    public int totalBlocks;
    public int numOfFoxes = 0;
    public int numOfHare = 0;
    public int numOfAnimals = 0;
    //  G block = Grass block
    public int numOfGBlocks = 0;
    //  B block = Bush block
    public int numOfBBlocks = 0;
    //  W block = Water block
    public int numOfWBlocks = 0;
    //  R lock = Grass with grain block
    public int numOfRBlocks = 0;

    public SimulationServerMap() {
        this.maps = new HashMap<Integer, SimulationPositionedMap>();
    }

    public void setMap(Integer key, Map map) {
        SimulationPositionedMap simulationPositionedMap = null;
        if (maps.containsKey(key)) {
            simulationPositionedMap = maps.get(key);
            simulationPositionedMap.setMap(map);
        }
        else {
            simulationPositionedMap = new SimulationPositionedMap(map);
        }
        maps.put(key, simulationPositionedMap);
    }

    private void recalculateStats() {
        totalBlocks = 0;
        numOfFoxes = 0;
        numOfHare = 0;
        numOfAnimals = 0;
        numOfGBlocks = 0;
        numOfBBlocks = 0;
        numOfWBlocks = 0;
        numOfRBlocks = 0;

        for (SimulationPositionedMap positionedMap : maps.values()) {
            numOfFoxes += positionedMap.map.numOfFoxes;
            numOfHare += positionedMap.map.numOfHare;
            numOfAnimals += positionedMap.map.numOfAnimals;
            numOfGBlocks += positionedMap.map.numOfGBlocks;
            numOfBBlocks += positionedMap.map.numOfBBlocks;
            numOfWBlocks += positionedMap.map.numOfWBlocks;
            numOfRBlocks += positionedMap.map.numOfRBlocks;
        }
    }

    public void printStats() {
        recalculateStats();
        System.out.println("numOfFoxes: " + numOfFoxes);
        System.out.println("numOfHare: " + numOfHare);
        System.out.println("numOfAnimals: " + numOfAnimals);
        System.out.println("numOfGrassBlocks: " + numOfGBlocks);
        System.out.println("numOfBushBlocks: " + numOfBBlocks);
        System.out.println("numOfWaterBlocks: " + numOfWBlocks);
    }

    /**
     * Iterates thourgh maps list and adds up the stats.
     * @return statistics of all maps in a String
     */
    public String stats() {
        recalculateStats();
        String res = "";
        res += "numOfFoxes: " + numOfFoxes + "\n";
        res += "numOfHare: " + numOfHare + "\n";
        res += "numOfAnimals: " + numOfAnimals + "\n";
        res += "numOfGrassBlocks: " + numOfGBlocks + "\n";
        res += "numOfBushBlocks: " + numOfBBlocks + "\n";
        res += "numOfWaterBlocks: " + numOfWBlocks + "\n";
        return  res;
    }
}
