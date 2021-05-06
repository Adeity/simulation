package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.network.server.view.View;

/**
 * Run this to start server side simulation application.
 */
public class MainServer {
    public static void main(String[] args) {
        SimulationServer simulationServer = new SimulationServer(8888);
        View view = new View(simulationServer);
        simulationServer.setView(view);
        view.openJFrameServerInit();
//        simulationServer.setLocalMapSize(4);
//        simulationServer.listen();

//
//        Thread t2 = new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        simulationServer.listen();
//                    }
//                }
//        );
//        simulationServer.stopInitStartSimulating();
//        Thread t1 = new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        simulationServer.stopInitStartSimulating();
//                    }
//                }
//        );
//
//        t2.start();
//        t1.start();
    }
}
