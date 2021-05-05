package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.network.client.SimulationClient;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;

import java.util.Scanner;
import java.util.logging.Logger;

public class ControllerNetwork {
    private static final Logger LOG = Logger.getLogger(ControllerNetwork.class.getName());
    Scanner sc;

    SimulationServer simulationServer;

    Simulation simulation;

    public ControllerNetwork(Simulation simulation) {
        this.simulation = simulation;
    }

//    //  server
//    protected void startServer(int port) {
//        if (isClient) {
//            return;
//        }
//        this.simulationServer = new SimulationServer(port);
//        this.simulationServer.listen();
//    }
//
//
//    //  client
//    private void clientCMD(String ipAddress, int port) {
//        if (isServer) {
//            return;
//        }
//        this.createClient(ipAddress, port);
//    }

//    protected void update (Map map) {
//        simulationClient.sendMapToServer(map);
//    }

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
