package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.controller.ControllerNetwork;

public class App {
    Simulation simulation;
    Controller controller;
    ControllerNetwork controllerNetwork;

    public App() {
        simulation = new Simulation();
        controller = new Controller(simulation);
        this.controllerNetwork = new ControllerNetwork(simulation);
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Controller getController() {
        return controller;
    }

}
