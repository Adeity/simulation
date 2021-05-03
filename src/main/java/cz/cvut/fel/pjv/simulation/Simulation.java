package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.*;
import cz.cvut.fel.pjv.simulation.network.client.SimulationClient;

import java.io.*;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;
import java.util.stream.Stream;

public class Simulation implements Serializable{
    public Map map;
    public boolean isRunning;
    public int day = 0;

    public SimulationClient simulationClient = null;

    public SimulationClient getSimulationClient() {
        return simulationClient;
    }

    public void setSimulationClient(SimulationClient simulationClient) {
        this.simulationClient = simulationClient;
    }

    public Simulation() {
    }

    /**
     * starts simulation
     * finishes when complete
     */
    public void run(int size) {
        this.isRunning = true;
        System.out.println("init new map");
        this.map = new Map(size, this);
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void run(String filename){
        this.isRunning = true;
        this.map = new Map(filename, this);
        if (simulationClient != null) {
            simulationClient.sendStateReady(map.blocks);
        }
    }

    public void simulateDay(){
        this.map.evaluate();
        if (simulationClient != null) {
            simulationClient.sendStateReady(map.blocks);
        }
        day++;
    }

    public void printStats() {
        if(isRunning) {
            System.out.println("Simulation is running");
            map.printStats();
        }
    }

    public void serializeWrite(String name) {
        try (OutputStream fos = new FileOutputStream(CONF.MAP_SAVES_DIRECTORY + CONF.fS + name);
             ObjectOutputStream out = new ObjectOutputStream(fos)
        ) {
            out.writeObject(this.map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serializeRead(String name) {


        try (InputStream fis = new FileInputStream(CONF.MAP_SAVES_DIRECTORY + CONF.fS + name);
             ObjectInputStream in = new ObjectInputStream(fis)) {

            // Deserializace objektu
            // přečtu objekt a pokusím se jej přetypovat
            Map map = (Map) in.readObject();
            isRunning = true;
            this.map = map;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Block[] getSurroundingBlocks (Block block) {
        int coordX = block.coordX;
        int coordY = block.coordY;
        return new Block[]{
                this.getBlock(coordX - 1, coordY - 1),
                this.getBlock(coordX - 1, coordY),
                this.getBlock(coordX - 1, coordY + 1),
                this.getBlock(coordX, coordY - 1),
                this.getBlock(coordX, coordY + 1),
                this.getBlock(coordX + 1, coordY - 1),
                this.getBlock(coordX + 1, coordY),
                this.getBlock(coordX + 1, coordY + 1),
        };
    }

    public Block getBlock(int coordX, int coordY) {
        if (isOnMyMap(coordX, coordY)) {
            return map.getBlock(coordX, coordY);
        }
        else {
            if (simulationClient != null) {
                Block block = simulationClient.getBlock(coordX, coordY);
                if (block == null) {
                    System.out.println("This is simulation speaking, thank you for the block: null " + coordX + " " + coordY);
                }
                else {
                    System.out.println("This is simulation speaking, thank you for the block: " + block.toString()+ " " +  coordX + " " + coordY);
                }

                return block;
            }
            else {
                return null;
            }
        }
    }

    public boolean isOnMyMap(int x, int y) {
        boolean isLocalX = (x >= 0 && x < map.sizeOfMap);
        boolean isLocalY = (y >= 0 && y < map.sizeOfMap);
        return isLocalX && isLocalY;
    }

    public boolean setBlock (int x, int y, Block newBlock) {
        if (isOnMyMap(x, y)) {
            return map.setBlock(x, y, newBlock);
        }
        else {
            if (simulationClient != null) {
                return simulationClient.setBlock(x, y, newBlock);
            }
            else {
                return false;
            }
        }
    }

    public Block findFreeBlockForMating(Animal a1, Animal a2) {
        Block[] a1Sb = getSurroundingBlocks(a1.block);
        Block[] a2Sb = getSurroundingBlocks(a2.block);
        Block[] surroundingBlocks = concatSurroundingBlocks(a1Sb, a2Sb);
        for (Block b : surroundingBlocks) {
            if (b == null) {
                continue;
            }
            if (b.isBlockFree()) {
                return b;
            }
        }
        return null;
    }

    public boolean deleteAnimalAtBlock(Block block) {
        int blockX = block.coordX;
        int blockY = block.coordY;

        if (isOnMyMap(blockX, blockY)) {
            map.deleteAnimalAtBlock(block);
            return true;
        }
        else {
            if (simulationClient != null) {
                block.setAnimal(null);
                return simulationClient.setBlock(blockX, blockY, block);
            }
            else {
                return false;
            }
        }
    }

    private Block[] concatSurroundingBlocks (Block[] b1, Block[] b2) {
        return Stream.concat(Arrays.stream(b1), Arrays.stream(b2)).toArray(Block[]::new);
    }

    public boolean serverAsksToSetBlock (int x, int y, Block newBlock) {
        if (isOnMyMap(x, y)) {
            return map.setBlock(x, y, newBlock);
        }
        else {
            System.err.println("Server asks to set block that is not on my map");
            return false;
        }
    }
}
