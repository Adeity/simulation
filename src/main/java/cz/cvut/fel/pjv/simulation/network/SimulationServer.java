package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Animal;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SimulationServer {
    private static final Logger LOG = Logger.getLogger(SimulationServer.class.getName());

    ArrayList<Thread> simulationServerThreads = new ArrayList<>();
    Socket clientSocket = null;

    SimulationServerMap simulationServerMap = new SimulationServerMap();

    public SimulationServer(int port) {
        try (
                ServerSocket serverSocket = new ServerSocket(port)
        ) {
            int connectionNum = 0;
            while (true) {
                clientSocket = serverSocket.accept();
                SimulationServerThread sst = new SimulationServerThread(simulationServerMap, clientSocket);
                Thread t = new Thread(sst);
                t.setName(String.valueOf(connectionNum));
                simulationServerThreads.add(t);
                t.start();
                connectionNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (Thread t : simulationServerThreads) {
//
//        }
    }

}
