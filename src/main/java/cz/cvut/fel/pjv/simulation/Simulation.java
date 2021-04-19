package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.model.Map;

import java.io.*;
import java.util.Observable;
import java.util.Scanner;

public class Simulation extends Observable {
    public Map map;
    public boolean isRunning;
    public int day = 0;

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
        this.map = new Map(filename);
    }

    public void simulateDay(){
        this.map.evaluate();
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
            System.out.println("Chyba při zápisu souboru : "+e);
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
            System.out.println("Nemohu najít definici třídy: "+e);
        } catch (IOException e) {
            System.out.println("Chyba při čtení souboru : "+e);
        }
    }
}
