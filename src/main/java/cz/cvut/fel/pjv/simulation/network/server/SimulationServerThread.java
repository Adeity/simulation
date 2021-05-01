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
            outWriter.println("MAP " + simulationServer.mapSize);
            System.out.println("S->C" + Thread.currentThread().getId() + " MAP " + simulationServer.mapSize);

            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

            while ((message = inReader.readLine()) != null) {
                System.out.println("C"+Thread.currentThread().getName()+"->S " + message);
                String[] columns = message.split(" ");

                String messageType = columns[0];

                if (messageType.equals("SET_BLOCK_RESULT")) {
                    String uuid = columns[1];

                    if (currentRequests.isEmpty()) {
                        continue;
                    }

                    for (Request request : currentRequests) {
                        if (request.getUuid().equals(uuid)) {
                            request.setResponse(message);
                            request.notify();
                        }
                    }
                }

                if (messageType.equals("SET_BLOCK")) {
                    String uuid = columns[1];
                    int messageX = Integer.parseInt(columns[2]);
                    int messageY = Integer.parseInt(columns[3]);
                    Block block = null;
                    try {
                        block = (Block) SerializationUtils.fromString(columns[4]);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    SimulationServerThread connection = this.simulationServer.getConnectionForCoordinates(this, messageX, messageY);

                    String response;
                    boolean result;

                    if (connection == null) {
                        result = false;
                        response = NetworkProtocol.buildSetBlockResultMessage(uuid, result);
                        outWriter.write(response);
                    }

                    int[] coords = this.simulationServer.getGlobalCoordinates(connection);

                    int targetX = coords[0] + messageX;
                    int targetY = coords[1] + messageY;

                    result = connection.setBlock(uuid, targetX, targetY, block);

                    response = NetworkProtocol.buildSetBlockResultMessage(uuid, result);
                    outWriter.write(response);

                }

                if (messageType.equals("STATE")) {
                    String state = columns[1];
                    this.simulationServer.setStateOfConnection(this, state);

                    if (state.equals("READY")) {
                        try {
                            Block[][] blocks = (Block[][]) SerializationUtils.fromString(columns[2]);
                            this.simulationServer.setBlocksOfConnection(this, blocks);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (messageType.equals("BLOCK")) {
                    String uuid = columns[1];

                    if (currentRequests.isEmpty()) {
                        continue;
                    }

                    for (Request request : currentRequests) {
                        if (request.getUuid().equals(uuid)) {
                            request.setResponse(message);
                            request.notify();
                        }
                    }
                }

                if (messageType.equals("GET_BLOCK")) {
                    //  GET_BLOCK UUID 10 10
                    int origX = Integer.parseInt(columns[2]);
                    int origY = Integer.parseInt(columns[3]);
                    String uuid = columns[1];

                    SimulationServerThread connection = this.simulationServer.getConnectionForCoordinates(this, origX, origY);


                    int[] coords = this.simulationServer.getGlobalCoordinates(connection);

                    int targetX = coords[0] + origX;
                    int targetY = coords[1] + origY;

                    Block block = connection.getBlock(uuid, targetX, targetY);

                    block.coordX = origX;
                    block.coordY = origY;

                    String response = NetworkProtocol.buildBlockMessage(uuid, block);

                    outWriter.println(response);
                    System.out.println("S->C" + Thread.currentThread().getId() + " " + response);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Block getBlock(String uuid, int targetX, int targetY) {
        Request currentRequest = new Request(
                NetworkProtocol.buildGetBlockMessage(targetX, targetY, uuid),
                uuid
        );
        outWriter.println(currentRequest.getRequest());
        System.out.println("S->C" + Thread.currentThread().getId() + " " + currentRequest.getRequest());
        try{
            currentRequest.wait(1000);
        } catch (InterruptedException e) {

        }

        if (currentRequest.getResponse() == null) {
            currentRequest = null;
            currentRequests.remove(currentRequest);
            return null;
        }

        String[] columns = currentRequest.getResponse().split(" ");
        Block block = null;
        try {
            block = (Block) SerializationUtils.fromString(columns[3]);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Trouble with getting block from client | deserialization");
        }

        currentRequests.remove(currentRequest);
        return block;
    }

    public boolean setBlock(String uuid, int x, int y, Block block) {
        Request currentRequest = new Request(
                NetworkProtocol.buildSetBlockMessage(x, y, uuid, block),
                uuid
        );
        currentRequests.add(currentRequest);
        outWriter.write(currentRequest.getRequest());

        try{
            currentRequest.wait(1000);
        } catch (InterruptedException e) {
        }

        if (currentRequest.getResponse() == null) {
            currentRequest = null;
            currentRequests.remove(currentRequest);
            return false;
        }

        String[] columns = currentRequest.getResponse().split(" ");

        boolean response = Boolean.parseBoolean(columns[2]);

        currentRequest = null;
        currentRequests.remove(currentRequest);

        return response;


    }

    public void go() {
        System.out.println("S->C" + Thread.currentThread().getId() + " GO");
        outWriter.println("GO");
    }
}
