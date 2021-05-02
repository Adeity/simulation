package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.network.client.SimulationClient;

import java.io.*;
import java.util.Observable;
import java.util.Scanner;

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
        simulationClient.sendStateReady(map.blocks);
    }

    public void simulateDay(){
        this.map.evaluate();
        simulationClient.sendStateReady(map.blocks);
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
                return simulationClient.getBlock(coordX, coordY);
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
