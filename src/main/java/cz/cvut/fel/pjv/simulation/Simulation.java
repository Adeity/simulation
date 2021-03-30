package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.model.Map;

public class Simulation {
    public Map map;
    public boolean isRunning;

    public Simulation() {
    }

    /**
     * starts simulation
     * finishes when complete
     */
    public void run(int size) {
        this.isRunning = true;
        this.map = new Map(size);
    }

    public void run(String filename){
        this.isRunning = true;
        this.map = new Map(CONF.folderDirectory + "/mapTemplates/" + filename);
    }

    public void simulateDay(){
        this.map.evaluate();
    }

    public void printStats() {
        if(isRunning) {
            System.out.println("Simulation is running");
            map.printStats();
        }
    }
}
