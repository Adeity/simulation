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
        boolean validInput = false;
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while (true) {
            System.out.println("Initialize simulation: ");
            System.out.println("1 - from template");
            System.out.println("2 - generate from size");
            String s = sc.nextLine();
            if(s.equals("1")) {
                System.out.println("Enter map template filename: ");
                s = sc.nextLine();
                this.run(s);
                System.out.println("Initializing map from teplate: " + s);
                break;
            }
            else if (s.equals("2")) {
                System.out.println("Enter size of map: ");
                s = sc.nextLine();
                int size = Integer.parseInt(s);
                this.run(size);
                break;
            }
            i++;
            if(i == 3) {
                System.out.println("You entered invalid command 3 times, do you wish to try again? y/n");
                s = sc.nextLine();
                if(s.equals("y") || s.equals("Y")) {
                    i = 0;
                    continue;
                }
                else {
                    break;
                }
            }
        }

        i = 0;
        if(simulation.isRunning) {
            System.out.println("Available commands: next, end, show, stats, help");
            while (true) {
                String s = sc.nextLine();
                if(s.equals("next")){
                    this.simulateDay();
                    i = 0;
                    continue;
                }
                else if(s.equals("end") || s.equals("quit")) {
                    this.endSimulation();
                    i=0;
                    break;
                }
                else if(s.equals("show")) {
                    this.showCurrent();
                    i = 0;
                    continue;
                }
                else if(s.equals("stats")) {
                    this.printStats();
                    i = 0;
                    continue;
                }
                else if(s.equals("help")) {
                    printHelp();
                    i = 0;
                    continue;
                }
                else if(s.equals("keepRunning")) {
                    keepRunning();
                    i = 0;
                    continue;
                }
                else {
                    System.out.println("Command: " + s + " not found. Type help to print available commands.");
                    i++;
                }
                if(i == 3) {
                    System.out.println("You entered invalid command 3 in a row times, do you wish to try again? y/n");
                    s = sc.nextLine();
                    if(s.equals("y") || s.equals("Y")) {
                        i = 0;
                        System.out.println("Available commands: next, end, show, stats, help");
                        continue;
                    }
                    else {
                        endSimulation();
                        break;
                    }
                }
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

    private void printStats() {
        simulation.printStats();
    }

    private void keepRunning() {
        while (true){
            simulateDay();
        }
    }

    private void printHelp() {
        System.out.println("Available commands: ");
        System.out.println("next - simulates next day");
        System.out.println("show - prints graphical representation of current state of simulation in command line");
        System.out.println("stats - prints stats of current state of simulation");
        System.out.println("end - end simulation and programme");
        System.out.println("help - prints this help section");
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
