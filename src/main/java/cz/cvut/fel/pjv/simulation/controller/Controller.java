package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * This is controller for MVC architecture. this class is mainly used for application running in command line mode.
 */
public class Controller {
    private final Simulation simulation;
    private ControllerNetwork controllerNetwork;
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
            LOG.fine("Initialize simulation: ");
            LOG.fine("1 - from template");
            LOG.fine("2 - generate from size");
            if (!isClient) {
                LOG.fine("3 - create server");
                LOG.fine("4 - create client");
            }
            if (fileNames != null){
                LOG.fine("5 - load save");
            }
            String s = sc.nextLine();
            if(s.equals("1")) {
                LOG.fine("Enter map template filename: ");
                s = sc.nextLine();
                this.run(s);
                LOG.fine("Initializing map from template: " + s);
                break;
            }
            else if (s.equals("2")) {
                LOG.fine("Enter size of map: ");
                s = sc.nextLine();
                int size = Integer.parseInt(s);
                this.run(size);
                break;
            }
//            else if (s.equals("3")) {
//                LOG.fine("Enter port: ");
//                s = sc.nextLine();
//                controllerNetwork = new ControllerNetwork(simulation);
//                controllerNetwork.startServer(Integer.parseInt(s));
//            }
            else if (s.equals("4")) {
                LOG.fine("Enter IP address of server: ");
                s = sc.nextLine();
                String ipAddress = s;
                LOG.fine("Enter port: ");
                s = sc.nextLine();
                String port = s;
                setControllerNetwork(new ControllerNetwork(getSimulation()));
                getControllerNetwork().createClient(ipAddress, Integer.parseInt(port));
                isClient = true;
                continue;
            }
            else if (s.equals("5")) {
                this.loadCMD();
                break;
            }
            i++;
            if(i == 3) {
                LOG.fine("You entered invalid command 3 times, do you wish to try again? y/n");
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
        if(getSimulation().isRunning) {
            LOG.fine("Available commands: next, end, show, stats, help");
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
                    LOG.fine("Command: " + s + " not found. Type help to print available commands.");
                    i++;
                }
                if(i == 3) {
                    LOG.fine("You entered invalid command 3 in a row times, do you wish to try again? y/n");
                    s = sc.nextLine();
                    if(s.equals("y") || s.equals("Y")) {
                        i = 0;
                        LOG.fine("Available commands: next, end, show, stats, help");
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
        this.getSimulation().run(s);
    }

    /**
     * commands simulation to run from size
     * @param size int
     */
    public void run(int size) {
        this.getSimulation().run(size);
    }

    /**
     * commands simulation to simulate day
     */
    public void simulateDay() {
        getSimulation().simulateDay();
    }

    /**
     * sets simulation.isrunning parameter to false.
     */
    public void endSimulation() {
        getSimulation().isRunning = false;
        LOG.fine("bye");
    }

    /**
     * logs map.tostring
     */
    private void showCurrent() {
        LOG.fine(getSimulation().map.toString());
    }

    private void printStats() {
        getSimulation().printStats();
    }

    private void keepRunning() {
        while (true){
            simulateDay();
        }
    }

    private void printHelp() {
        LOG.fine("Available commands: ");
        LOG.fine("next - simulates next day");
        LOG.fine("show - prints graphical representation of current state of simulation in command line");
        LOG.fine("stats - prints stats of current state of simulation");
        LOG.fine("end - end simulation and programme");
        LOG.fine("help - prints this help section");
    }

    private void save() {
        File mapTemplateDirectory = new File(CONF.MAP_SAVES_DIRECTORY);
        String[] fileNames = mapTemplateDirectory.list();

        Scanner sc = new Scanner(System.in);
        LOG.fine("Enter name of new save: ");
        String name = sc.nextLine();

        this.getSimulation().serializeWrite(name);
    }

    private void loadCMD() {
        File mapTemplateDirectory = new File(CONF.MAP_SAVES_DIRECTORY);
        String[] fileNames = mapTemplateDirectory.list();

        if(fileNames == null) {
            LOG.fine("There are no save files.");
            System.exit(-3);
        }

        LOG.fine("Available saves: ");
        for (String file : fileNames) {
            LOG.fine(file);
        }

        Scanner sc = new Scanner(System.in);
        LOG.fine("\nEnter name of save to load: ");
        String name;

        while (true) {
            name = sc.nextLine();
            boolean contains = Arrays.asList(fileNames).contains(name);
            if(!contains) {
                LOG.fine("There is no save with name: " + name);
            }
            else {
                break;
            }
        }

        LOG.fine("Loading save: " + name);
        this.getSimulation().serializeRead(name);
    }

    private void load(String saveName) {
        this.getSimulation().serializeRead(saveName);
    }

    protected Simulation getSimulation() {
        return simulation;
    }

    protected ControllerNetwork getControllerNetwork() {
        return controllerNetwork;
    }

    protected void setControllerNetwork(ControllerNetwork controllerNetwork) {
        this.controllerNetwork = controllerNetwork;
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
