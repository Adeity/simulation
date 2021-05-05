package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.*;
import cz.cvut.fel.pjv.simulation.network.client.SimulationClient;
import cz.cvut.fel.pjv.simulation.view.View;

import java.io.*;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Simulation with a map. There are fox and hare on the map interacting with each other. Fox eat hare and die of old age. They mate. As well as hare do.
 */
public class Simulation implements Serializable{
    private static final Logger LOG = Logger.getLogger(Simulation.class.getName());
    public Map map;
    public boolean isRunning;
    public int day = 0;
    App app;
    View view;
    final Object lock = new Object();

    public SimulationClient simulationClient = null;

    public SimulationClient getSimulationClient() {
        return simulationClient;
    }

    public void setSimulationClient(SimulationClient simulationClient) {
        this.simulationClient = simulationClient;
    }

    public Simulation() {
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    /**
     * Get to first state of simulation
     * initialize map from size parameter
     * @param size of map
     */
    public void run(int size) {
        this.isRunning = true;
        this.map = new Map(size, this);
        this.map.initMap(size);
        if (simulationClient != null) {
            simulationClient.sendStateReady();
            if (getView().getjFrameClientSimulation() == null) {
                getView().openJFrameClientSimulation();
            }
            else {
                getView().repaintJFrameClientSimulation();
            }
        }
        else {
            getView().repaintJFrameSimulation();
            getView().repaintJFrameStats();
        }
    }

    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Get to first state of simulation
     * initialize map from filename parameter
     * @param filename is name of template
     */
    public void run(String filename){
        this.isRunning = true;
        this.map = new Map(filename, this);
        if (simulationClient != null) {
            simulationClient.sendStateReady();
        }
        else {
            getView().repaintJFrameSimulation();
            getView().repaintJFrameStats();
        }
    }

    /**
     * Simulate one day of simulation cycle
     */
    public void simulateDay(){
        this.map.evaluate();
        if (simulationClient != null) {
            simulationClient.sendStateReady();
        }
        else {
            getView().repaintJFrameSimulation();
            getView().repaintJFrameStats();
        }
        day++;
    }

    /**
     * Prints stats of map to command line
     */
    public void printStats() {
        if(isRunning) {
            LOG.info("Simulation is running");
            map.printStats();
        }
    }

    /**
     * Save state of map to serialized file
     * @param name
     */
    public void serializeWrite(String name) {
        try (OutputStream fos = new FileOutputStream(CONF.MAP_SAVES_DIRECTORY + CONF.fS + name);
             ObjectOutputStream out = new ObjectOutputStream(fos)
        ) {
            out.writeObject(this.map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to server on given address and port
     * @param address e.g. localhost
     * @param port e.g. 8888
     * @return false if connection failed. true otherwise
     */
    public boolean connectToServer (String address, int port) {
        this.simulationClient = new SimulationClient(address, port, this);
        if (this.simulationClient.connect()) {
            Thread t = new Thread(this.simulationClient);
            t.start();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Load state of map from serialized file
     * @param name of file
     */
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

    /**
     * Get surrounding block around animal who is calling this method
     * @param block is the block to get surrounding blocks of
     * @return array of surrounding blocks
     */
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

    /**
     * checks if block is on local map, if not, checks if network connection is running. If it is, it asks server for the block
     * @param coordX is x coordinate of the block
     * @param coordY is y coordinate of the block
     * @return the block
     */
    public Block getBlock(int coordX, int coordY) {
        LOG.info("Getting block on: " + coordX + ", " + coordY);
        if (isOnMyMap(coordX, coordY)) {
            LOG.info("The block on " + coordX + ", " + coordY + " is on local map.");
            return map.getBlock(coordX, coordY);
        }
        else {
            if (simulationClient != null) {
                LOG.info("The block on " + coordX + ", " + coordY + " is NOT on local map. Asking server to get this block.");
                Block block = simulationClient.getBlock(coordX, coordY);
                if (block == null) {
                    LOG.info("This is simulation speaking, thank you for the block: null " + coordX + " " + coordY);
                }
                else {
                    LOG.info("This is simulation speaking, thank you for the block: " + block.toString()+ " " +  coordX + " " + coordY);
                }

                return block;
            }
            else {
                return null;
            }
        }
    }

    /**
     * checks based on x and y coordinates wheter something (block) is on the local map
     * @param x coordinate
     * @param y coordinate
     * @return true if it is, false otherwise
     */
    public boolean isOnMyMap(int x, int y) {
        boolean isLocalX = (x >= 0 && x < map.sizeOfMap);
        boolean isLocalY = (y >= 0 && y < map.sizeOfMap);
        return isLocalX && isLocalY;
    }


    /**
     * checks surrounding blocks of both animals
     * @param a1 animal1
     * @param a2 animal2
     * @return surrounding blocks for both animals
     */
    public Block findFreeBlockForMating(Animal a1, Animal a2) {
        Block[] a1Sb = getSurroundingBlocks(a1.block);
        Block[] a2Sb = getSurroundingBlocks(a2.block);
        Block[] surroundingBlocks = concatSurroundingBlocks(a1Sb, a2Sb);
        for (Block b : surroundingBlocks) {
            if (b == null) {
                continue;
            }
            if (b.isBlockFree()) {
                LOG.info("Returning free block to animals: " + b.coordX + ", " + b.coordY);
                return b;
            }
        }
        return null;
    }

    /**
     * checks if block is local. if yes, deletes it locally and return true. if not, checks if network connection is up. If it is, it asks server to
     * delete animal at given block and waits for response.
     * @param block to delete animal on
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteAnimalAtBlock(Block block) {
        int blockX = block.coordX;
        int blockY = block.coordY;

        if (isOnMyMap(blockX, blockY)) {
            LOG.info("Deleting animal at local map");
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

    /**
     * this method gets called when request comes from server to set block on local map.
     * @param x coordinate of block
     * @param y coordinate of block
     * @param newBlock is the block to set on x and y coordinates
     * @return true if request is assessed positively. false otherwise
     */
    public boolean serverAsksToSetBlock (int x, int y, Block newBlock) {
        if (isOnMyMap(x, y)) {
            return map.setBlock(x, y, newBlock);
        }
        else {
            LOG.info("Server asks to set block that is not on my map");
            return false;
        }
    }
}
