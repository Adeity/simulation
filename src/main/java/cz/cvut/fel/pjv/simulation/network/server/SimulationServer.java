package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.model.Block;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class SimulationServer {
    private static final Logger LOG = Logger.getLogger(SimulationServer.class.getName());

    int localMapSize;

    public void setLocalMapSize(int localMapSize) {
        this.localMapSize = localMapSize;
    }

    public int getLocalMapSize() {
        return localMapSize;
    }

    ArrayList<TableItem> table = new ArrayList<>();
    ArrayList<Socket> clientSockets = new ArrayList<>();
    ServerSocket serverSocket;

    boolean simulating = false;


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
                if(simulating) {
                    break;
                }
                Socket clientSocket = serverSocket.accept();
                serverSocket.getLocalSocketAddress();
                if (clientSockets.contains(clientSocket)) {
                    continue;
                }
                clientSockets.add(clientSocket);
                SimulationServerThread sst = new SimulationServerThread(this, clientSocket);
                Thread t = new Thread(sst);
                t.setName(String.valueOf(connectionNum));
                t.start();

                connectionNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    tableItem.minX <= targetX && tableItem.maxX > targetX
                    && tableItem.minY <= targetY && tableItem.maxY > targetY
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
    }

    public void setBlocksOfConnection(SimulationServerThread connection, Block[][] blocks) {
        for (TableItem tableItem : table) {
            if (tableItem.connection == connection) {
                tableItem.setBlocks(blocks);
            }
        }
    }

    private boolean everyConnectionIsReady() {

        boolean everyConnectionReady = true;
        for (TableItem tableItem : table) {
            if (tableItem.getStatus() == null) {
                continue;
            }
            else if (!tableItem.getStatus().equals("READY")) {
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

    public boolean isInMapBounds(int x, int y) {
        boolean boolX = x >= 0 && x < localMapSize;
        boolean boolY = y >= 0 && y < localMapSize;
        return boolX && boolY;
    }

    public void stopInitStartSimulating() {
//        simulating = true;
        while (true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {

            }
            go();
        }
    }

    public void addConnectionToTableIfThereIsntAlready(SimulationServerThread sst, String status) {
        this.table.add(
                TableItem.getInstance(localMapSize, sst, status)
        );
    }

    int[] translateRelativeCoordinatesToGlobal (SimulationServerThread connection, int relativeX, int relativeY) {
        int[] globalCoordinates = new int[2];

        Integer globalMinX = null;
        Integer globalMinY = null;

        for (TableItem tableItem : table) {
            if (tableItem.connection == connection) {
                globalMinX = tableItem.minX;
                globalMinY = tableItem.minY;
            }
        }

        if (globalMinX == null || globalMinY == null) {
            return null;
        }

        int globalX = globalMinX + relativeX;
        int globalY = globalMinY +  relativeY;

        globalCoordinates[0] = globalX;
        globalCoordinates[1] = globalY;

        return globalCoordinates;
    }

    int[] translateGlobalCoordinatesToTargetRelative (int globalX, int globalY) {
        int[] relativeToTargetCoords = new int[2];

        int relativeToTargetX = globalX % localMapSize;
        int relativeToTargetY = globalY % localMapSize;

        relativeToTargetCoords[0] = relativeToTargetX;
        relativeToTargetCoords[1] = relativeToTargetY;

        return relativeToTargetCoords;
    }
}
