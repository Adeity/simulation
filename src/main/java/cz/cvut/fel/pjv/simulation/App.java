package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.controller.ControllerNetwork;
import cz.cvut.fel.pjv.simulation.view.View;

/**
 * Application has simulation model, controllers and view
 */
public class App {
    Simulation simulation;
    Controller controller;
    ControllerNetwork controllerNetwork;
    View view;

    public App() {
        simulation = new Simulation();
        view = new View(simulation);
        controller = new Controller(simulation);
        this.controllerNetwork = new ControllerNetwork(simulation);
    }

    /**
     * Simulation with a map. There are fox and hare on the map interacting with each other. Fox eat hare and die of old age. They mate. As well as hare do.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * This is controller for MVC architecture. this class is mainly used for application running in command line mode.
     */
    public Controller getController() {
        return controller;
    }

}
