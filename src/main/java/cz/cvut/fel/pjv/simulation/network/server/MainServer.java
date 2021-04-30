package cz.cvut.fel.pjv.simulation.network.server;

import java.util.Timer;

public class MainServer {
    public static void main(String[] args) {
        SimulationServer simulationServer = new SimulationServer(8888);
        simulationServer.setMapSize(50);
        simulationServer.listen();

    }
}
