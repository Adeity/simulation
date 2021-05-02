package cz.cvut.fel.pjv.simulation.network.server;

public class MainServer {
    public static void main(String[] args) {
        SimulationServer simulationServer = new SimulationServer(8888);
        simulationServer.setLocalMapSize(10);
        simulationServer.listen();


        Thread t2 = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        simulationServer.listen();
                    }
                }
        );

        Thread t1 = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        simulationServer.stopInitStartSimulating();
                    }
                }
        );

        t2.start();
        t1.start();
    }
}
