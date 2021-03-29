package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;

import java.util.Scanner;

public class Controller {
    private final Simulation simulation;

    public Controller(Simulation simulation) {
        this.simulation = simulation;
    }

    public void command() {
        Scanner sc = new Scanner(System.in);
        if(!this.simulation.isRunning) {
            System.out.println("Choose and enter number:");
            System.out.println("Map from template: 1");
            System.out.println("Map base on size: 2");
            String s = sc.nextLine();
            if(s.equals("1")) {
                System.out.println("Enter map template filename: ");
                s = sc.nextLine();
                this.run(s);
                System.out.println("Initializing map from teplate: " + s);
            }
            else if (s.equals("2")) {
                System.out.println("Enter size of map: ");
                s = sc.nextLine();
                int size = Integer.parseInt(s);
                this.run(size);
            }
        }
        else {
            String s = sc.nextLine();
            if(s.equals("next")){
                    this.simulateDay();
            }
            else if(s.equals("end")) {
                this.endSimulation();
            }
            else if(s.equals("show")) {
                this.showCurrent();
            }
        }
    }

    public void restart() {

    }

    public void run(String s) {
        this.simulation.run(s);
    }

    public void run(int size) {
        this.simulation.run(size);
    }

    public void simulateDay() {
        simulation.simulateDay();
    }

    public void endSimulation() {
        simulation.isRunning = false;
        System.out.println("bye");
    }

    private void showCurrent() {
        System.out.println(simulation.map);
    }

//    public void changeTerrainAtCoord (Block.Terrain terrain, int row, int col) {
//        this.map.blocks[row][col].setTerrain(terrain);
//    }
//
//    public void changeAnimalAtCoord (Animal animal, int row, int col) {
//        this.map.blocks[row][col].setAnimal(animal);
//    }
//
//    public void changeTerrainAndAnimalAtCoord (Block.Terrain terrain, Animal animal, int row, int col) {
//        this.map.blocks[row][col].setTerrain(terrain);
//        this.map.blocks[row][col].setAnimal(animal);
//    }
//
//    public boolean addAnimalAtCoord (Animal animal, int row, int col) {
//        Animal currentAnimal = this.map.blocks[row][col].getAnimal();
//        if(currentAnimal != null) {
//            return false;
//        }
//        this.map.blocks[row][col].setAnimal(animal);
//        return true;
//    }
}
