package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.network.SimulationServerMap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SimulationServer {
    private static final Logger LOG = Logger.getLogger(SimulationServer.class.getName());

    int mapSize;

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public int getMapSize() {
        return mapSize;
    }

    ArrayList<TableItem> table = new ArrayList<>();
    Socket clientSocket = null;
    ServerSocket serverSocket;


    public SimulationServer(int port) {
        try  {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            int connectionNum = 0;
            while (true) {
                if(connectionNum >= 4) {
                    break;
                }
                clientSocket = serverSocket.accept();
                SimulationServerThread sst = new SimulationServerThread(this, clientSocket);
                Thread t = new Thread(sst);
                t.setName(String.valueOf(connectionNum));
                int a = this.mapSize;

                table.add(
                        TableItem.getInstance(connectionNum, getMapSize(), sst,  null)
                );

                t.start();
                connectionNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SimulationServerThread getConnectionForCoordinates(SimulationServerThread clientConnection, int x, int y) {
        int[] coords = getGlobalCoordinates(clientConnection);

        int targetX = coords[0] + x;
        int targetY = coords[1] + y;

        return findConnectionByGlobalCoordinates(targetX, targetY);
    }

    /**
     * Returns where on what coordinates does connection start
     * @param clientConnection is client connection
     * @return x and y
     */
    public int[] getGlobalCoordinates(SimulationServerThread clientConnection) {
        int[] coords = null;
        for (TableItem tableItem : table) {
            if (tableItem.getConnection().equals(clientConnection)) {
                coords = new int[2];
                coords[0] = tableItem.minX;
                coords[1] = tableItem.maxY;
                break;
            }
        }
        return coords;
    }

    public SimulationServerThread findConnectionByGlobalCoordinates(int targetX, int targetY) {
        SimulationServerThread connection = null;
        for (TableItem tableItem : table) {
            if (
                    tableItem.minX <= targetX && tableItem.maxX >= targetX
                    && tableItem.minY <= targetY && tableItem.maxY >= targetY
            ) {
                connection = tableItem.connection;
                break;
            }
        }
        return connection;
    }

    public void setStateOfConnection(SimulationServerThread connection, String state) {
        for (TableItem tableItem : table) {
            if (tableItem.connection == connection) {
                tableItem.setStatus(state);
            }
        }
        go();
    }

    private boolean everyConnectionIsReady() {

        boolean everyConnectionReady = true;
        for (TableItem tableItem : table) {
            if (!tableItem.getStatus().equals("READY")) {
                everyConnectionReady = false;
            }
        }
        return everyConnectionReady;
    }

    public void go() {
        if (everyConnectionIsReady()) {
            for (TableItem tableItem : table) {
                tableItem.connection.go();
            }
        }
    }
}
