package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.network.client.SimulationClient;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Controller network used mainly for connecting to server when using application in command line.
 */
public class ControllerNetwork {
    private static final Logger LOG = Logger.getLogger(ControllerNetwork.class.getName());

    Simulation simulation;

    public ControllerNetwork(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * creates connection with server
     * @param ipAddress is the address
     * @param port is the port
     */
    public void createClient(String ipAddress, int port) {
        simulation.simulationClient = new SimulationClient(ipAddress, port, simulation);
        simulation.simulationClient.connect();
        Thread t = new Thread(simulation.simulationClient);
        t.start();
    }
}
