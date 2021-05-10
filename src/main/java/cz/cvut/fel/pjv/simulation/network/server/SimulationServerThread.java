package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.NetworkProtocol;
import cz.cvut.fel.pjv.simulation.network.SerializationUtils;
import cz.cvut.fel.pjv.simulation.network.client.Request;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This represents single connection with a client. It receives requests from server and reroutes them with help of SimulationServer.
 */
public class SimulationServerThread implements Runnable {
    private static final Logger LOG = Logger.getLogger(SimulationServerThread.class.getName());
    protected Socket clientSocket;
    protected SimulationServer simulationServer;

    PrintWriter outWriter;
    BufferedReader inReader;

    public SimulationServerThread(SimulationServer simulationServer, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.simulationServer = simulationServer;
        try{
            this.outWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            this.inReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message, outputLine;

            //  Initiate conversation with client
            outWriter.println("MAP " + simulationServer.localMapSize);
            LOG.fine("S->C" + Thread.currentThread().getName() + " MAP " + simulationServer.localMapSize);

//              SHOWCASE
//            outWriter.println("MAP showcase2.txt");
//            LOG.fine("S->C" + Thread.currentThread().getName() + " MAP map10.txt");


//            LOG.fine("S->C" + Thread.currentThread().getName() + " MAP " + simulationServer.localMapSize);

            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

            while ((message = inReader.readLine()) != null) {
//                LOG.fine("C"+Thread.currentThread().getName()+"->S " + message);
                LOG.fine("C"+Thread.currentThread().getName()+"->S " + message);
                String[] columns = message.split(" ");

                String messageType = columns[0];

                 if (messageType.equals("STATE")) { // client sends back its state
                    String state = columns[1];
                    this.simulationServer.setStateOfConnection(this, state);

                    if (state.equals("READY")) { // if it is ready, it is ready to simulate anothjer day
                        simulationServer.addNewItemToTable(this, "READY");
                        this.simulationServer.setStateOfConnection(this, "READY");
                    }
                    else if (state.equals("SET")) { // if it is set, client also sends back blocks for server to paint
                        try {
                            Block[][] blocks = (Block[][]) SerializationUtils.fromString(columns[2]);
                            this.simulationServer.setBlocksOfConnection(this, blocks);
                            this.simulationServer.setStateOfConnection(this, "SET");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (state.equals("GO")) { // client started evaluating its map
                        this.simulationServer.setStateOfConnection(this, "GO");
                    }
                }
                else if (messageType.equals("GET_BLOCK")) { // client sends GET_BLOCK request, server reroutes to another connection, or sends response straigth back, if there is no fitting connection which owns clients desired block
                    //  GET_BLOCK UUID -1 -1
                     LOG.fine("Received GET_BLOCK request");
                    String uuid = columns[1];
                    int relativeX = Integer.parseInt(columns[2]);
                    int relativeY = Integer.parseInt(columns[3]);

                    int[] globaCoords = simulationServer.translateRelativeCoordinatesToGlobal(this, relativeX, relativeY);

                    int globalX = globaCoords[0];
                    int globalY = globaCoords[1];

                    TableItem thisConnection = this.simulationServer.findTableItemByConnection(this);
                    LOG.fine("Found that connection asking for this blocks has global coordinates: [minX, maxX][minY, maxY]: " + thisConnection.minX + ", " + thisConnection.maxX + " | " + thisConnection.minY + ", " + thisConnection.maxY);

                    TableItem targetConnection = this.simulationServer.findTableItemWithConnectionByGlobalCoordinates(globalX, globalY);


                    if (targetConnection == null) {
                        LOG.fine("Found that connection who was that block doesnt exist. Sending back null block");

                        String response = NetworkProtocol.buildBlockMessage(uuid, relativeX, relativeY, globalX, globalY, thisConnection.minX, thisConnection.minY,null);
                        outWriter.println(response);
//                        LOG.fine("S->C" + Thread.currentThread().getName() + " " + response);
                        LOG.fine("S->C" + Thread.currentThread().getName() + " " + response);
                        continue;
                    }

                     LOG.fine("Found that connection who was that block is connection with global coordinates: [minX, maxX][minY, maxY]: " + targetConnection.minX + ", " + targetConnection.maxX + " | " + targetConnection.minY + ", " + targetConnection.maxY);


                     SimulationServerThread connection = targetConnection.getConnection();
                    //  coords relative to target
                     int[] crTT = simulationServer.findCoordinatesRelativeToTarget(targetConnection.minX, targetConnection.minY, globalX, globalY);


                    connection.getBlock(uuid, crTT[0], crTT[1], globalX, globalY, thisConnection.minX, thisConnection.minY);
                }

                else if (messageType.equals("SET_BLOCK")) { // SET_BLOCK [UUID2] 12 12 [SERIALIZED_BLOCK] // client sends SET_BLOCK request, server reroutes to another connection, or sends response straigth back, if there is no fitting connection which owns clients desired block
                    String uuid = columns[1];
                    int relativeX = Integer.parseInt(columns[2]);
                    int relativeY = Integer.parseInt(columns[3]);
                    Block block = null;
                    try {
                        block = (Block) SerializationUtils.fromString(columns[4]);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    int[] globaCoords = simulationServer.translateRelativeCoordinatesToGlobal(this, relativeX, relativeY);

                    int globalX = globaCoords[0];
                    int globalY = globaCoords[1];

                    TableItem thisConnection = this.simulationServer.findTableItemByConnection(this);

                    TableItem targetConnection = this.simulationServer.findTableItemWithConnectionByGlobalCoordinates(globalX, globalY);


                    String response;
                    boolean result;

                    if (targetConnection == null) {
                        result = false;
                        response = NetworkProtocol.buildSetBlockResultMessage(uuid, relativeX, relativeY, globalX, globalY, thisConnection.minX, thisConnection.minY, "FALSE");
                        outWriter.println(response);
//                        LOG.fine("S->C" + Thread.currentThread().getName() + " " + response);
                        LOG.fine("S->C" + Thread.currentThread().getName() + " " + response);
                        continue;
                    }
                     SimulationServerThread connection = targetConnection.getConnection();
                    //  coords relative to target
                    int[] crTT = simulationServer.findCoordinatesRelativeToTarget(targetConnection.minX, targetConnection.minY, globalX, globalY);

                    if (block != null) {
                        block.setCoordX(crTT[0]);
                        block.setCoordY(crTT[1]);
                    }
                    connection.setBlock(uuid, crTT[0], crTT[1], globalX, globalY, thisConnection.minX, thisConnection.minY, block);
                }

                 else if (messageType.equals("BLOCK")) { //  client responds to servers GET_BLOCK request. server sends BLOCK back to original client requestor
                     //  BLOCK UUID [SERIALIZED_BLOCK]
                     String uuid = columns[1];
                     int relativeX = Integer.parseInt(columns[2]);
                     int relativeY = Integer.parseInt(columns[3]);

                     int globalX = Integer.parseInt(columns[4]);
                     int globalY = Integer.parseInt(columns[5]);

                     int requestorMinX = Integer.parseInt(columns[6]);
                     int requestorMinY = Integer.parseInt(columns[7]);


                     Block block = null;

                     try{
                         block = (Block) SerializationUtils.fromString(columns[8]);
                     } catch (ClassNotFoundException e) {
                         e.printStackTrace();
                     }

                     SimulationServerThread targetConnection = this.simulationServer.findConnectionByMinimumCoordinateValues(requestorMinX, requestorMinY);

                     int[] crTT = simulationServer.findCoordinatesRelativeToTarget(requestorMinX, requestorMinY, globalX, globalY);

                     if (block != null) {
                         block.setCoordX(crTT[0]);
                         block.setCoordY(crTT[1]);
                     }

                     if (targetConnection == null) {
                         continue;
                     }
                     targetConnection.block(uuid, crTT[0], crTT[1], globalX, globalY, requestorMinX, requestorMinY, block);
                 }

                else if (messageType.equals("SET_BLOCK_RESULT")) { //  client responds to servers SET_BLOCK request. server sends result back to original client requestor
                     String uuid = columns[1];
                     int relativeX = Integer.parseInt(columns[2]);
                     int relativeY = Integer.parseInt(columns[3]);

                     int globalX = Integer.parseInt(columns[4]);
                     int globalY = Integer.parseInt(columns[5]);

                     int requestorMinX = Integer.parseInt(columns[6]);
                     int requestorMinY = Integer.parseInt(columns[7]);
                     String result = columns[8];

                     SimulationServerThread targetConnection = this.simulationServer.findConnectionByMinimumCoordinateValues(requestorMinX, requestorMinY);

                     int[] crTT = simulationServer.findCoordinatesRelativeToTarget(requestorMinX, requestorMinY, globalX, globalY);

                     if (targetConnection == null) {
                         continue;
                     }
                     targetConnection.setBlockResult(uuid, crTT[0], crTT[1], globalX, globalY, requestorMinX, requestorMinY, result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (simulationServer.isRunning()) {
                this.simulationServer.deleteBlocksOfConnectionFromTable(this);
            }
            else {
                this.simulationServer.deleteConnectionFromTable(this);
            }
        }
    }

    /**
     * send GET_BLOCK request to client
     * @param uuid is uuid of request
     * @param targetX is x coordinate relative to target connection
     * @param targetY is y coordinate relative to target connection
     * @param globalX is x coordinate in global perspective
     * @param globalY is y coordinate in global perspective
     * @param minX is minimum X coordinate of original requestor connection
     * @param minY is minimumY coordinate of original requestor connection
     */
    synchronized public void getBlock(String uuid, int targetX, int targetY, int globalX, int globalY, int minX, int minY) {
        Request currentRequest = new Request(
                NetworkProtocol.buildGetBlockMessageFromServerToClient(targetX, targetY, globalX, globalY, minX, minY,uuid),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
//        LOG.fine("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
        LOG.fine("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
    }

    /**
     * send SET_BLOCK request to client
     * @param uuid is uuid of request
     * @param targetX is x coordinate relative to target connection
     * @param targetY is y coordinate relative to target connection
     * @param globalX is x coordinate in global perspective
     * @param globalY is y coordinate in global perspective
     * @param minX is minimum X coordinate of original requestor connection
     * @param minY is minimumY coordinate of original requestor connection
     */
    synchronized public void setBlock(String uuid, int targetX, int targetY, int globalX, int globalY, int minX, int minY, Block block) {
        Request currentRequest = new Request(
                NetworkProtocol.buildSetBlockMessageFromServerToClient(targetX, targetY, globalX, globalY, minX, minY, uuid, block),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
//        LOG.fine("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
        LOG.fine("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
    }

    /**
     * send GO command to client
     */
    synchronized public void go() {
//        LOG.fine("S->C" + Thread.currentThread().getName() + " GO");
        LOG.fine("S->C" + Thread.currentThread().getName() + " GO");
        outWriter.println("GO");
    }

    /**
     * send SET command to client.
     */
    synchronized public void set() {
        outWriter.println("SET");
    }

    /**
     * send BLOCK response to clients GET_BLOCK request
     * @param uuid is uuid of request
     * @param targetX is x coordinate relative to target connection
     * @param targetY is y coordinate relative to target connection
     * @param globalX is x coordinate in global perspective
     * @param globalY is y coordinate in global perspective
     * @param minX is minimum X coordinate of original requestor connection
     * @param minY is minimumY coordinate of original requestor connection
     */
    synchronized public void block(String uuid, int targetX, int targetY, int globalX, int globalY, int minX, int minY, Block block) {
        Request currentRequest = new Request(
                NetworkProtocol.buildBlockMessage(uuid, targetX, targetY, globalX, globalY, minX, minY, block),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
//        LOG.fine("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
        LOG.fine("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
    }

    /**
     * send SET_BLOCK_RESULT response to clients SET_BLOCK request
     * @param uuid is uuid of request
     * @param targetX is x coordinate relative to target connection
     * @param targetY is y coordinate relative to target connection
     * @param globalX is x coordinate in global perspective
     * @param globalY is y coordinate in global perspective
     * @param minX is minimum X coordinate of original requestor connection
     * @param minY is minimumY coordinate of original requestor connection
     */
    synchronized public void setBlockResult(String uuid, int targetX, int targetY, int globalX, int globalY, int minX, int minY, String result) {
        String answerToClient = NetworkProtocol.buildSetBlockResultMessage(uuid, targetX, targetY, globalX, globalY, minX, minY, result);
        outWriter.println(answerToClient);
        LOG.fine("S->C" + Thread.currentThread().getName() + " " + answerToClient);
        LOG.fine("S->C" + Thread.currentThread().getName() + " " + answerToClient);
    }
}
