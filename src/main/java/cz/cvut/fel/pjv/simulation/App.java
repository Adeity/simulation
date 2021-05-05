package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.controller.ControllerNetwork;
import cz.cvut.fel.pjv.simulation.view.View;

/**
 * Application has simulation, controllers and view
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

    public Simulation getSimulation() {
        return simulation;
    }

    public Controller getController() {
        return controller;
    }

}
