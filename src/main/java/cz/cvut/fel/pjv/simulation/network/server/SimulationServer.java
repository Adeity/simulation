package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.server.view.View;
import cz.cvut.fel.pjv.simulation.utils.Utilities;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class SimulationServer {
    private static final Logger LOG = Logger.getLogger(SimulationServer.class.getName());

    int localMapSize;
    static int connectionNum = 0;

    boolean running = false;

    View view;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setLocalMapSize(int localMapSize) {
        this.localMapSize = localMapSize;
    }

    public int getLocalMapSize() {
        return localMapSize;
    }

    ArrayList<TableItem> table = new ArrayList<>();
    ArrayList<Socket> clientSockets = new ArrayList<>();
    ServerSocket serverSocket;


    private int maxNumOfClients;
    private int numOfConnectedClients = 0;

    public int getMaxNumOfClients() {
        return maxNumOfClients;
    }

    public void setMaxNumOfClients(int maxNumOfClients) {
        this.maxNumOfClients = maxNumOfClients;
        if (view != null) {
            view.repaintJFrameServerInit();
        }
    }

    public SimulationServer(int port) {
        this.port = port;
//        Utilities.addHandlerToLogger(LOG);
        try  {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfConnectedClients() {
        return numOfConnectedClients;
    }

    public void setNumOfConnectedClients(int numOfConnectedClients) {
        this.numOfConnectedClients = numOfConnectedClients;
        if(this.view != null) {
            view.repaintJFrameServerInit();
        }
    }

    public void listen() {
        try {
            while (true) {
                if (running) {

                    System.out.println("Running is true so I stop listening");
                    break;
                }
                System.out.println("Connection num is: " + connectionNum);
                Socket clientSocket = serverSocket.accept();
                if (running) {
                    LOG.info("Simulation is running so server stops listening for client connections");
                    break;
                }
                if (numOfConnectedClients >= getMaxNumOfClients()) {
                    LOG.info("There are already maximum of: "+ getMaxNumOfClients() + "  clients connected. Declining connection.");
                    continue;
                }
                serverSocket.getLocalSocketAddress();
                if (clientSockets.contains(clientSocket)) {
                    continue;
                }
                clientSockets.add(clientSocket);
                SimulationServerThread sst = new SimulationServerThread(this, clientSocket);
                Thread t = new Thread(sst);
                t.setName(String.valueOf(connectionNum));
                connectionNum++;
                t.start();
            }
            LOG.info("Server stopped listening for client connections");
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


    public void addNewItemToTable(SimulationServerThread sst, String status) {
        boolean contains = false;
        for (TableItem tableItem : table) {
            if (tableItem.getConnection() == sst) {
                contains = true;
                LOG.fine("Connection is already in table.");
                return;
            }
        }

        boolean upLeft = false;
        boolean upRight = false;
        boolean downLeft = false;
        boolean downRight = false;

        boolean[] positions = new boolean[4];
        positions[0] = upLeft;
        positions[1] = upRight;
        positions[2] = downLeft;
        positions[3] = downRight;

        for (TableItem tableItem : table) {
            if (tableItem.minX == 0 && tableItem.minY == 0) {
                LOG.info("Position 0 is taken");
                positions[0] = true;
            }
            else if (tableItem.minX == 0 && tableItem.minY == localMapSize) {
                LOG.info("Position 1 is taken");
                positions[1] = true;
            }
            else if (tableItem.minX == localMapSize && tableItem.minY == 0) {
                LOG.info("Position 2 is taken");
                positions[2] = true;
            }
            else if (tableItem.minX == localMapSize && tableItem.minY == localMapSize) {
                LOG.info("Position 3 is taken");
                positions[3] = true;
            }
        }

        for (int i = 0; i < positions.length; i++) {
            if (!positions[i]) {
                LOG.info("Adding client to position: " + i);
                this.table.add(TableItem.getInstance(i, this.localMapSize, sst, "READY"));
                this.setNumOfConnectedClients(table.size());
                break;
            }
        }
    }

    synchronized public ArrayList<Block[][]> getBlocks() {
        this.table.sort(new TableItemComparator());
        ArrayList<Block[][]> blocks = new ArrayList<>();
        for (TableItem tableItem : table) {
            LOG.info("Adding blocks from tableItem with position: " + tableItem.getPosition());
            blocks.add(tableItem.getBlocks());
        }
        return blocks;
    }

    public void deleteConnectionFromTable(SimulationServerThread sst) {
        TableItem tableItemToRemove = null;
        for (TableItem tableItem : table) {
            if (tableItem.getConnection() == sst) {
                tableItemToRemove = tableItem;
                break;
            }
        }
        if (tableItemToRemove == null) {
            return;
        }
        this.table.remove(tableItemToRemove);
        this.setNumOfConnectedClients(table.size());
        LOG.info("Connection was removed from table");
    }

//    public void addConnectionToTableIfThereIsntAlready(SimulationServerThread sst, String status) {
//        boolean contains = false;
//        for (TableItem tableItem : table) {
//            if (tableItem.getConnection() == sst) {
//                contains = true;
//                break;
//            }
//        }
//        if (!contains) {
//            this.table.add(
//                    TableItem.getInstance(localMapSize, sst, status)
//            );
//        }
//    }

    public TableItem findTableItemWithConnectionByGlobalCoordinates (int globalX, int globalY) {
        LOG.info("Finding connection by global coordinates: " + globalX + ", " + globalY);
        TableItem connectionTarget = null;
        for (TableItem tableItem : table) {
            LOG.info("This one has: [minX, globalX, maxX] (" + tableItem.minX + ", " + globalX + ", " + tableItem.maxX + ") [minY, globalY, maxY] (" + tableItem.minY + ", " + globalY + ", " + tableItem.maxY + ")");
            if (
                            globalX >= tableItem.minX
                            && globalX <= tableItem.maxX
                            && globalY >= tableItem.minY
                            && globalY <= tableItem.maxY
            ) {
                LOG.info("This is the connection I was looking for. Breaking cycle.");
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

    private boolean everyConnectionIsSet() {
        boolean everyConnectionSet = true;
        for (TableItem tableItem : table) {
            if (!tableItem.getStatus().equals("SET")) {
                everyConnectionSet = false;
                break;
            }
        }
        return everyConnectionSet;
    }

    public void set() {
        if (everyConnectionIsReady()) {
            for (TableItem tableItem : table) {
                tableItem.connection.set();
            }
        }
    }

    public void go() {
        if (everyConnectionIsSet()) {
            view.repaintJFrameSingleClientSimulation();
            view.repaintJFrameServerSimulation();
            for (TableItem tableItem : table) {
                tableItem.connection.go();
            }
        }
        else {
            set();
        }
    }

    public void stopInitStartSimulating() {
//        simulating = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {

                    }
                    go();
                }
            }
        });
        t.start();
    }

    public boolean isInMapBounds(int x, int y) {
        boolean boolX = x >= 0 && x < localMapSize;
        boolean boolY = y >= 0 && y < localMapSize;
        return boolX && boolY;
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

    public void deleteBlocksOfConnectionFromTable(SimulationServerThread sst) {
        for (TableItem tableItem : table) {
            if (tableItem.getConnection() == sst) {
                tableItem.setBlocks(null);
            }
        }
    }
}
