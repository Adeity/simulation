package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.NetworkProtocol;
import cz.cvut.fel.pjv.simulation.network.SerializationUtils;
import cz.cvut.fel.pjv.simulation.network.client.Request;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SimulationServerThread implements Runnable {
    public Socket clientSocket;
    public SimulationServer simulationServer;

    ArrayList<Request> currentRequests = new ArrayList<>();

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
            System.out.println("S->C" + Thread.currentThread().getName() + " MAP " + simulationServer.localMapSize);

            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

            while ((message = inReader.readLine()) != null) {
                System.out.println("C"+Thread.currentThread().getName()+"->S " + message);
                String[] columns = message.split(" ");

                String messageType = columns[0];

                if (messageType.equals("SET_BLOCK_RESULT")) {
                    String uuid = columns[1];
                    int relativeX = Integer.parseInt(columns[2]);
                    int relativeY = Integer.parseInt(columns[3]);

                    String result = columns[4];

                    int[] globaCoords = simulationServer.translateRelativeCoordinatesToGlobal(this, relativeX, relativeY);

                    int globalX = globaCoords[0];
                    int globalY = globaCoords[1];

                    SimulationServerThread connection = this.simulationServer.findConnectionByGlobalCoordinates(globalX, globalY);

                    int[] crTT = simulationServer.translateGlobalCoordinatesToTargetRelative(globalX, globalY);

                    if (connection == null) {
                        connection.setBlockResult(uuid, crTT[0], crTT[1], globalX, globalY,"FALSE");
                        continue;
                    }
                    connection.setBlockResult(uuid, crTT[0], crTT[1], globalX, globalY, result);
                }

                else if (messageType.equals("SET_BLOCK")) { // SET_BLOCK [UUID2] 12 12 [SERIALIZED_BLOCK]
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

                    SimulationServerThread connection = this.simulationServer.findConnectionByGlobalCoordinates(globalX, globalY);



                    String response;
                    boolean result;

                    if (connection == null) {
                        result = false;
                        response = NetworkProtocol.buildSetBlockResultMessage(uuid, relativeX, relativeY, globalX, globalY, "FALSE");
                        outWriter.println(response);
                        System.out.println("S->C" + Thread.currentThread().getName() + " " + response);
                        continue;
                    }

                    //  coords relative to target
                    int[] crTT = simulationServer.translateGlobalCoordinatesToTargetRelative(globalX, globalY);

                    if (block != null) {
                        block.coordX = crTT[0];
                        block.coordY = crTT[1];
                    }

                    connection.setBlock(uuid, crTT[0], crTT[1], globalX, globalY, block);
                }

                else if (messageType.equals("STATE")) {
                    String state = columns[1];
                    this.simulationServer.setStateOfConnection(this, state);

                    if (state.equals("READY")) {
                        try {
                            Block[][] blocks = (Block[][]) SerializationUtils.fromString(columns[2]);

                            simulationServer.addConnectionToTableIfThereIsntAlready(this, "READY");
                            this.simulationServer.setBlocksOfConnection(this, blocks);
                            this.simulationServer.setStateOfConnection(this, "READY");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (state.equals("GO")) {
                        this.simulationServer.setStateOfConnection(this, "GO");
                    }
                }

                else if (messageType.equals("BLOCK")) {
                    //  BLOCK UUID [SERIALIZED_BLOCK]
                    String uuid = columns[1];
                    int relativeX = Integer.parseInt(columns[2]);
                    int relativeY = Integer.parseInt(columns[3]);

                    int globalX = Integer.parseInt(columns[4]);
                    int globalY = Integer.parseInt(columns[5]);

                    Block block = null;

                    try{
                        block = (Block) SerializationUtils.fromString(columns[6]);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    SimulationServerThread connection = this.simulationServer.findConnectionByGlobalCoordinates(globalX, globalY);

                    int[] crTT = simulationServer.translateGlobalCoordinatesToTargetRelative(globalX, globalY);

                    if (block != null) {
                        block.coordX = crTT[0];
                        block.coordY = crTT[1];
                    }

                    if (connection == null) {
                        connection.block(uuid, crTT[0], crTT[1], globalX, globalY,null);
                        continue;
                    }
                    connection.block(uuid, crTT[0], crTT[1], globalX, globalY, block);
                }

                else if (messageType.equals("GET_BLOCK")) {
                    //  GET_BLOCK UUID -1 -1
                    String uuid = columns[1];
                    int relativeX = Integer.parseInt(columns[2]);
                    int relativeY = Integer.parseInt(columns[3]);

                    int[] globaCoords = simulationServer.translateRelativeCoordinatesToGlobal(this, relativeX, relativeY);

                    int globalX = globaCoords[0];
                    int globalY = globaCoords[1];

                    SimulationServerThread connection = this.simulationServer.findConnectionByGlobalCoordinates(globalX, globalY);


                    //  coords relative to target
                    int[] crTT = simulationServer.translateGlobalCoordinatesToTargetRelative(globalX, globalY);

                    if (connection == null) {
                        String response = NetworkProtocol.buildBlockMessage(uuid, crTT[0], crTT[1], globalX, globalY,null);
                        outWriter.println(response);
                        System.out.println("S->C" + Thread.currentThread().getName() + " " + response);
                        continue;
                    }
                    connection.getBlock(uuid, crTT[0], crTT[1], globalX, globalY);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBlock(String uuid, int targetX, int targetY, int globalX, int globalY) {
        Request currentRequest = new Request(
                NetworkProtocol.buildGetBlockMessageFromServerToClient(targetX, targetY, globalX, globalY,uuid),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
        System.out.println("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
    }

    public void setBlock(String uuid, int x, int y, int globalX, int globalY, Block block) {
        Request currentRequest = new Request(
                NetworkProtocol.buildSetBlockMessageFromServerToClient(x, y, globalX, globalY, uuid, block),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
        System.out.println("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
    }

    public void go() {
        System.out.println("S->C" + Thread.currentThread().getName() + " GO");
        outWriter.println("GO");
    }

    public void block(String uuid, int x, int y, int globalX, int globalY, Block block) {
        Request currentRequest = new Request(
                NetworkProtocol.buildBlockMessage(uuid, x, y, globalX, globalY, block),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
        System.out.println("S->C" + Thread.currentThread().getName() + " " + currentRequest.getRequest());
    }

    public void setBlockResult(String uuid, int x, int y, int globalX, int globalY, String result) {
        String answerToClient = NetworkProtocol.buildSetBlockResultMessage(uuid, x, y, globalX, globalY, result);
        outWriter.println(answerToClient);
        System.out.println("S->C" + Thread.currentThread().getName() + " " + answerToClient);
    }
}
