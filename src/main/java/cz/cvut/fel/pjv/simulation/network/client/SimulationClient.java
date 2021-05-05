package cz.cvut.fel.pjv.simulation.network.client;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.NetworkProtocol;
import cz.cvut.fel.pjv.simulation.network.SerializationUtils;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

public class SimulationClient implements Runnable{
    private static final Logger LOG = Logger.getLogger(SimulationClient.class.getName());
    Socket socket = null;
    PrintWriter outWriter = null;
    BufferedReader inReader = null;

    final Object lock = new Object();

    Simulation simulation;

    Request currentRequest;

    String host;
    int port;


    public SimulationClient(String host, int port, Simulation simulation) {
        this.host = host;
        this.port = port;
        this.simulation = simulation;

    }

    public boolean connect() {
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            System.out.println("Trouble connecting to server");
            return false;
        }

        try {
            outWriter = new PrintWriter(socket.getOutputStream(), true);
            inReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void close() {
        try{
            LOG.severe("Closing client connection");
            System.out.println("Closing client connection");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String in;
            String out;

            while ((in = inReader.readLine()) != null) {
                System.out.println("S->C " + in);
                String[] columns = in.split(" ");
                String messageType = columns[0];

                if (messageType.equals("GO")) {

                    outWriter.println("STATE GO");
                    System.out.println("C->S " + "STATE GO");

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            simulation.simulateDay();
                        }
                    });
                    t.start();
//                    this.simulation.simulateDay();
                }

                else if (messageType.equals("SET")) {
                    sendStateSet();
                }

                else if (messageType.equals("SET_BLOCK_RESULT")) {
                    String messageUUID = columns[1];
                    if (currentRequest == null) {
                        //  there is no active response, continue
                        continue;
                    }
                    if (currentRequest.uuid.equals(messageUUID)) {
                        System.out.println("UUID from set_block_result matches");
                        LOG.info("Received resposnse to SET_BLOCK request: " + in);
                        synchronized (lock) {
                            System.out.println("Setting response to my SET_BLOCK request and notifying");
                            this.currentRequest.setResponse(in);
                            lock.notify();
                        }
                    }
                }

                else if (messageType.equals("MAP")) {
                    try {
                        int sizeOfMap = Integer.parseInt(columns[1]);
                        simulation.run(sizeOfMap);
                    } catch (NumberFormatException e) {
                        simulation.run(columns[1]);
                    }
                    sendStateReady();
                }

                else if (messageType.equals("BLOCK")) {
                    String messageUUID = columns[1];

                    //  there is no active response, continue
                    if (currentRequest == null) {
                        continue;
                    }
                    if (currentRequest.uuid.equals(messageUUID)) {
                        System.out.println("UUID from block matches");
                        synchronized (lock) {
                            System.out.println("Setting response to my GET_BLOCK request and notifying");
                            this.currentRequest.setResponse(in);
                            lock.notify();
                        }
//                        currentRequest.notify();
                    }
                }
                else if (messageType.equals("GET_BLOCK")) {
                    String messageUUID = columns[1];

                    int coordX = Integer.parseInt(columns[2]);
                    int coordY = Integer.parseInt(columns[3]);

                    int globalX = Integer.parseInt(columns[4]);
                    int globalY = Integer.parseInt(columns[5]);

                    int requestorMinX = Integer.parseInt(columns[6]);
                    int requestorMinY = Integer.parseInt(columns[7]);

                    Block block = this.simulation.map.getBlock(coordX, coordY);
                    outWriter.println(NetworkProtocol.buildBlockMessage(messageUUID, coordX, coordY, globalX, globalY, requestorMinX, requestorMinY, block));
                    System.out.println("C->S " + NetworkProtocol.buildBlockMessage(messageUUID, coordX, coordY, globalX, globalY, requestorMinX, requestorMinY, block));
                }

                else if (messageType.equals("SET_BLOCK")) {
                    String uuid = columns[1];

                    int coordX = Integer.parseInt(columns[2]);
                    int coordY = Integer.parseInt(columns[3]);

                    int globalX = Integer.parseInt(columns[4]);
                    int globalY = Integer.parseInt(columns[5]);

                    int requestorMinX = Integer.parseInt(columns[6]);
                    int requestorMinY = Integer.parseInt(columns[7]);

                    try {
                        Block block = (Block) SerializationUtils.fromString(columns[8]);

                        boolean result = simulation.serverAsksToSetBlock(coordX, coordY, block);

                        String resultStr = (result) ? "TRUE" : "FALSE";

                        String response = NetworkProtocol.buildSetBlockResultMessage(uuid, coordX, coordY, globalX, globalY, requestorMinX, requestorMinY, resultStr);

                        outWriter.println(response);
                        System.out.println("C->S " + response);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.close();
        } catch (IOException ex) {
            // Nepodarilo se najit (DNS, NIS atp.) hostitele
            System.exit(-1);
        }
    }

    public Block getBlock(int x, int y) {

        String uuid = UUID.randomUUID().toString();
        boolean finished = false;
        currentRequest = new Request(
                NetworkProtocol.buildGetBlockMessage(x, y, uuid), // GET_BLOCK uuid x y
                uuid
        );

        synchronized(lock){
            outWriter.println(currentRequest.getRequest());
            LOG.info("My GET_BLOCK request to server: C->S " + currentRequest.getRequest());
//            System.out.println("C->S " + currentRequest.getRequest());

            try {
                System.out.println("Calling wait in getBlock method");
                lock.wait();
            } catch (InterruptedException e) {
                System.out.println("getBlock wait was interrupted");
            }
        }
//
//        synchronized (currentRequest) {
//            try {
//                currentRequest.wait(1000);
//            }
//            catch (InterruptedException e) {
//            }
//            if(currentRequest.response == null) {
//                currentRequest = null;
//                return null;
//            }
//        }
        LOG.info("Response to my GET_BLOCK request from server: " + "S->C " + currentRequest.getResponse());
//        System.out.println("S->C " + currentRequest.getResponse());

        String[] response = currentRequest.response.split(" ");
        Block block;
        try {
            block = (Block) SerializationUtils.fromString(response[8]);
        } catch (IOException  | ClassNotFoundException e) {
            System.out.println("Deserialization of block failed");
            return null;
        }
        currentRequest = null;
        if (block == null) {
            System.out.println("Networking received block: null and returning it to simulation " + x + " " + y);
        }
        else {
            System.out.println("Networking received block: " + block.toString() + " and returning it to simulation " + x + " " + y);
        }

        return block;
    }

    public boolean setBlock(int x, int y, Block block) {
        LOG.info("Trying to set block with relative coordinates to me: " + x + ", " + y + " block with x attribute: " + x + ", " + y + " Tostring: " + block.toString());
        String uuid = UUID.randomUUID().toString();
        currentRequest = new Request(
                NetworkProtocol.buildSetBlockMessage(x, y, uuid, block),
                uuid
        );


        synchronized(lock){
            outWriter.println(currentRequest.request);
            System.out.println("C->S " + currentRequest.getRequest());
            boolean finished = false;
            try {
                System.out.println("Calling wait in setBlock method");
                lock.wait();
            } catch (InterruptedException e) {
                System.out.println("setBlock wait was interrupted");
            }
        }
//        synchronized (currentRequest) {
//            try{
//
//                currentRequest.wait(1000);
//            } catch (InterruptedException e) {
//
//            }
//        }
        if(currentRequest.response == null) {
            currentRequest = null;
            return false;
        }

        System.out.println("S->C " + currentRequest.getResponse());
        String[] columns = currentRequest.response.split(" ");

        String booleanResponseStr = columns[8];

        boolean booleanResponse = booleanResponseStr.equals("TRUE") || booleanResponseStr.equals("true") || booleanResponseStr.equals("True");

        System.out.println("Received set block result: " + booleanResponse);

        currentRequest = null;

        return booleanResponse;
    }

    public void sendStateReady() {
        String response = NetworkProtocol.buildStateReadyMessage();
        outWriter.println(response);
        System.out.println("C->S " + response);
    }

    public void sendStateSet() {
        String response = NetworkProtocol.buildStateSetMessage(this.simulation.map.blocks);
        outWriter.println(response);
        LOG.info("C->S " + response);
        simulation.getView().repaintJFrameClientSimulation();
        simulation.getView().repaintJFrameStats();
    }
}
