package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.controller.NetworkController;

public class App {
    Simulation simulation;
    Controller controller;
    NetworkController networkController;

    public App() {
        simulation = new Simulation();
        controller = new Controller(simulation);
        networkController = new NetworkController(simulation);
    }

    public void run() {
        System.out.println("Fox and hare simulator");
        System.out.println("____________________________________");
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Controller getController() {
        return controller;
    }

}
