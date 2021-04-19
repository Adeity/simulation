package cz.cvut.fel.pjv.simulation.controller;

import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.network.SimulationClient;
import cz.cvut.fel.pjv.simulation.network.SimulationServer;

import java.util.Scanner;
import java.util.logging.Logger;

public class ControllerNetwork {
    private static final Logger LOG = Logger.getLogger(ControllerNetwork.class.getName());
    Scanner sc;

    SimulationClient simulationClient;
    SimulationServer simulationServer;

    boolean isClient;
    boolean isServer;

    public ControllerNetwork(boolean isServer) {
        this.isServer = isServer;
        this.isClient = !isServer;
    }

    //  server
    protected void startServer(int port) {
        if (isClient) {
            return;
        }
        this.simulationServer = new SimulationServer(port);
    }


    //  client
    private void clientCMD(String ipAddress, int port) {
        if (isServer) {
            return;
        }
        this.createClient(ipAddress, port);
    }

    protected void update (Map map) {
        simulationClient.sendMapToServer(map);
    }

    protected void createClient(String ipAddress, int port) {
        if (isServer) {
            return;
        }
        simulationClient = new SimulationClient(ipAddress, port);
        Thread t = new Thread(simulationClient);
        t.start();
    }
}
