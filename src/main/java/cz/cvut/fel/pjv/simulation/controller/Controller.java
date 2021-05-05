package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.view.JFrameSimulation;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

public class Controller {
    private final Simulation simulation;
    ControllerNetwork controllerNetwork;
    private static final Logger LOG = Logger.getLogger(Controller.class.getName());

    public Controller(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * command cycle for controlling simulation through command.
     * deprecated
     */
    public void command() {
        File mapTemplateDirectory = new File(CONF.MAP_SAVES_DIRECTORY);
        String[] fileNames = mapTemplateDirectory.list();

        Scanner sc = new Scanner(System.in);
        int i = 0;
        boolean isClient = false;

        while (true) {
            LOG.info("Initialize simulation: ");
            LOG.info("1 - from template");
            LOG.info("2 - generate from size");
            if (!isClient) {
                LOG.info("3 - create server");
                LOG.info("4 - create client");
            }
            if (fileNames != null){
                LOG.info("5 - load save");
            }
            String s = sc.nextLine();
            if(s.equals("1")) {
                LOG.info("Enter map template filename: ");
                s = sc.nextLine();
                this.run(s);
                LOG.info("Initializing map from template: " + s);
                break;
            }
            else if (s.equals("2")) {
                LOG.info("Enter size of map: ");
                s = sc.nextLine();
                int size = Integer.parseInt(s);
                this.run(size);
                break;
            }
//            else if (s.equals("3")) {
//                LOG.info("Enter port: ");
//                s = sc.nextLine();
//                controllerNetwork = new ControllerNetwork(simulation);
//                controllerNetwork.startServer(Integer.parseInt(s));
//            }
            else if (s.equals("4")) {
                LOG.info("Enter IP address of server: ");
                s = sc.nextLine();
                String ipAddress = s;
                LOG.info("Enter port: ");
                s = sc.nextLine();
                String port = s;
                controllerNetwork = new ControllerNetwork(simulation);
                controllerNetwork.createClient(ipAddress, Integer.parseInt(port));
                isClient = true;
                continue;
            }
            else if (s.equals("5")) {
                this.loadCMD();
                break;
            }
            i++;
            if(i == 3) {
                LOG.info("You entered invalid command 3 times, do you wish to try again? y/n");
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
            LOG.info("Available commands: next, end, show, stats, help");
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
                else if(s.equals("save")) {
                    save();
                    i = 0;
                    continue;
                }
                else if (s.equals("net")){

                }
                else if (s.equals("gui")) {
//                    this.runGUI();
                    i=0;
                    continue;
                }
                else {
                    LOG.info("Command: " + s + " not found. Type help to print available commands.");
                    i++;
                }
                if(i == 3) {
                    LOG.info("You entered invalid command 3 in a row times, do you wish to try again? y/n");
                    s = sc.nextLine();
                    if(s.equals("y") || s.equals("Y")) {
                        i = 0;
                        LOG.info("Available commands: next, end, show, stats, help");
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

    /**
     * commands simulation to run from string
     * @param s string, name of file
     */
    public void run(String s) {
        this.simulation.run(s);
    }

    /**
     * commands simulation to run from size
     * @param size int
     */
    public void run(int size) {
        this.simulation.run(size);
    }

    /**
     * commands simulation to simulate day
     */
    public void simulateDay() {
        simulation.simulateDay();
    }

    /**
     * sets simulation.isrunning parameter to false.
     */
    public void endSimulation() {
        simulation.isRunning = false;
        LOG.info("bye");
    }

    /**
     * logs map.tostring
     */
    private void showCurrent() {
        LOG.info(simulation.map.toString());
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
        LOG.info("Available commands: ");
        LOG.info("next - simulates next day");
        LOG.info("show - prints graphical representation of current state of simulation in command line");
        LOG.info("stats - prints stats of current state of simulation");
        LOG.info("end - end simulation and programme");
        LOG.info("help - prints this help section");
    }

    private void save() {
        File mapTemplateDirectory = new File(CONF.MAP_SAVES_DIRECTORY);
        String[] fileNames = mapTemplateDirectory.list();

        Scanner sc = new Scanner(System.in);
        LOG.info("Enter name of new save: ");
        String name = sc.nextLine();

        this.simulation.serializeWrite(name);
    }

    private void loadCMD() {
        File mapTemplateDirectory = new File(CONF.MAP_SAVES_DIRECTORY);
        String[] fileNames = mapTemplateDirectory.list();

        if(fileNames == null) {
            LOG.info("There are no save files.");
            System.exit(-3);
        }

        LOG.info("Available saves: ");
        for (String file : fileNames) {
            LOG.info(file);
        }

        Scanner sc = new Scanner(System.in);
        LOG.info("\nEnter name of save to load: ");
        String name;

        while (true) {
            name = sc.nextLine();
            boolean contains = Arrays.asList(fileNames).contains(name);
            if(!contains) {
                LOG.info("There is no save with name: " + name);
            }
            else {
                break;
            }
        }

        LOG.info("Loading save: " + name);
        this.simulation.serializeRead(name);
    }

    private void load(String saveName) {
        this.simulation.serializeRead(saveName);
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
