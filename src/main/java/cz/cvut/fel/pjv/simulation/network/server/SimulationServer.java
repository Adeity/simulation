package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.Simulation;
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

    public TableItem findTableItemWithConnectionByGlobalCoordinates (int globalX, int globalY) {
        TableItem connectionTarget = null;
        for (TableItem tableItem : table) {
            if (
                    tableItem.minX <= globalX && tableItem.maxX > globalX
                            && tableItem.minY <= globalY && tableItem.maxY > globalY
            ) {
                connectionTarget = tableItem;
                break;
            }
        }
        return connectionTarget;
    }


    public TableItem findTableItemByConnection (SimulationServerThread sst) {
        TableItem connectionTarget = null;
        for (TableItem tableItem : table) {
            if (
                    tableItem.getConnection() == sst
            ) {
                connectionTarget = tableItem;
                break;
            }
        }
        return connectionTarget;
    }

    public SimulationServerThread findConnectionByGlobalCoordinates(int globalX, int globalY) {
        SimulationServerThread connectionTarget = null;
        for (TableItem tableItem : table) {
            if (
                    tableItem.minX <= globalX && tableItem.maxX > globalX
                    && tableItem.minY <= globalY && tableItem.maxY > globalY
            ) {
                connectionTarget = tableItem.connection;
                break;
            }
        }
        return connectionTarget;
    }

    /**
     * This method finds coordinates relative to target connection
     * In client communication message, there is information about global coordinates.
     * First connection must be found to get minX and minY
     * @param minX is global minimumX coordinate of target conneciton
     * @param minY is global minimumY coordinate of target conneciton
     * @param globalX is global x coordinate of block
     * @param globalY is global y coordinate of block
     * @return relativeX and relativeY
     */
    public int[] findCoordinatesRelativeToTarget (int minX, int minY, int globalX, int globalY) {
        int relativeToTargetX = globalX - minX;
        int relativeToTargetY = globalY - minY;

        int[] relativeCoords = new int[2];
        relativeCoords[0] = relativeToTargetX;
        relativeCoords[1] = relativeToTargetY;

        return relativeCoords;
    }

    public SimulationServerThread findConnectionByMinimumCoordinateValues (int minX, int minY) {
        SimulationServerThread connection = null;
        for (TableItem tableItem : table) {
            boolean xFits = tableItem.minX == minX;
            boolean yFits = tableItem.minY == minY;
            if (xFits && yFits) {
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
            if (!tableItem.getStatus().equals("READY")) {
                everyConnectionReady = false;
                break;
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
        boolean contains = false;
        for (TableItem tableItem : table) {
            if (tableItem.getConnection() == sst) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            this.table.add(
                    TableItem.getInstance(localMapSize, sst, status)
            );
        }
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

    int[] translateGlobalCoordinatesToTargetRelative (SimulationServerThread connection, int globalX, int globalY) {
        int[] relativeToTargetCoords = new int[2];

        int relativeToTargetX = globalX % localMapSize;
        int relativeToTargetY = globalY % localMapSize;

        relativeToTargetCoords[0] = relativeToTargetX;
        relativeToTargetCoords[1] = relativeToTargetY;

        return relativeToTargetCoords;
    }
}
