package cz.cvut.fel.pjv.simulation.network.client;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.NetworkProtocol;
import cz.cvut.fel.pjv.simulation.network.SerializationUtils;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Simulation client is able to receive and send messages to server using network protocol messages.
 */
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

    /**
     * connects to server
     * @return true if connection was successful, false otherwise
     */
    public boolean connect() {
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            LOG.info("Trouble connecting to server");
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

    /**
     * close connection
     */
    public void close() {
        try{
            LOG.severe("Closing client connection");
            LOG.info("Closing client connection");
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
                LOG.info("S->C " + in);
                String[] columns = in.split(" ");
                String messageType = columns[0];

                if (messageType.equals("GO")) { // command from server came to simulate day

                    outWriter.println("STATE GO");
                    LOG.info("C->S " + "STATE GO");

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            simulation.simulateDay();
                        }
                    });
                    t.start();
//                    this.simulation.simulateDay();
                }

                else if (messageType.equals("SET")) { // command from server came to send blocks of local map
                    sendStateSet();
                }

                else if (messageType.equals("SET_BLOCK_RESULT")) { // response to SET_BLOCK request came
                    String messageUUID = columns[1];
                    if (currentRequest == null) {
                        //  there is no active response, continue
                        continue;
                    }
                    if (currentRequest.uuid.equals(messageUUID)) {
                        LOG.info("UUID from set_block_result matches");
                        LOG.info("Received resposnse to SET_BLOCK request: " + in);
                        synchronized (lock) {
                            LOG.info("Setting response to my SET_BLOCK request and notifying");
                            this.currentRequest.setResponse(in);
                            lock.notify();
                        }
                    }
                }

                else if (messageType.equals("MAP")) { // first message from server to client. second information is size of map
                    try {
                        int sizeOfMap = Integer.parseInt(columns[1]);
                        simulation.run(sizeOfMap);
                    } catch (NumberFormatException e) {
                        simulation.run(columns[1]);
                    }
                    sendStateReady();
                }

                else if (messageType.equals("BLOCK")) { //  response to get_block request
                    String messageUUID = columns[1];

                    //  there is no active response, continue
                    if (currentRequest == null) {
                        continue;
                    }
                    if (currentRequest.uuid.equals(messageUUID)) {
                        LOG.info("UUID from block matches");
                        synchronized (lock) {
                            LOG.info("Setting response to my GET_BLOCK request and notifying");
                            this.currentRequest.setResponse(in);
                            lock.notify();
                        }
//                        currentRequest.notify();
                    }
                }
                else if (messageType.equals("GET_BLOCK")) { //  server asks to send local block
                    String messageUUID = columns[1];

                    int coordX = Integer.parseInt(columns[2]);
                    int coordY = Integer.parseInt(columns[3]);

                    int globalX = Integer.parseInt(columns[4]);
                    int globalY = Integer.parseInt(columns[5]);

                    int requestorMinX = Integer.parseInt(columns[6]);
                    int requestorMinY = Integer.parseInt(columns[7]);

                    Block block = this.simulation.map.getBlock(coordX, coordY);
                    outWriter.println(NetworkProtocol.buildBlockMessage(messageUUID, coordX, coordY, globalX, globalY, requestorMinX, requestorMinY, block));
                    LOG.info("C->S " + NetworkProtocol.buildBlockMessage(messageUUID, coordX, coordY, globalX, globalY, requestorMinX, requestorMinY, block));
                }

                else if (messageType.equals("SET_BLOCK")) { //  server asks to set local block, includes serialized version of new block to be set on current local block
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
                        LOG.info("C->S " + response);
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

    /**
     * ask server for block on x and y coordinates. create request and wait for response
     * @param x coordinate of desired block
     * @param y coordinate of desired block
     * @return desired block that came as a response from server
     */
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
//            LOG.info("C->S " + currentRequest.getRequest());

            try {
                LOG.info("Calling wait in getBlock method");
                lock.wait();
            } catch (InterruptedException e) {
                LOG.info("getBlock wait was interrupted");
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
//        LOG.info("S->C " + currentRequest.getResponse());

        String[] response = currentRequest.response.split(" ");
        Block block;
        try {
            block = (Block) SerializationUtils.fromString(response[8]);
        } catch (IOException  | ClassNotFoundException e) {
            LOG.info("Deserialization of block failed");
            return null;
        }
        currentRequest = null;
        if (block == null) {
            LOG.info("Networking received block: null and returning it to simulation " + x + " " + y);
        }
        else {
            LOG.info("Networking received block: " + block.toString() + " and returning it to simulation " + x + " " + y);
        }

        return block;
    }

    /**
     * ask server to set modify block on x and y coordinate.
     * @param x coordinate
     * @param y coordinate
     * @param block new version of block to be changed on some other client
     * @return true if server sends back positive response. false otherwise
     */
    public boolean setBlock(int x, int y, Block block) {
        LOG.info("Trying to set block with relative coordinates to me: " + x + ", " + y + " block with x attribute: " + x + ", " + y + " Tostring: " + block.toString());
        String uuid = UUID.randomUUID().toString();
        currentRequest = new Request(
                NetworkProtocol.buildSetBlockMessage(x, y, uuid, block),
                uuid
        );


        synchronized(lock){
            outWriter.println(currentRequest.request);
            LOG.info("C->S " + currentRequest.getRequest());
            boolean finished = false;
            try {
                LOG.info("Calling wait in setBlock method");
                lock.wait();
            } catch (InterruptedException e) {
                LOG.info("setBlock wait was interrupted");
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

        LOG.info("S->C " + currentRequest.getResponse());
        String[] columns = currentRequest.response.split(" ");

        String booleanResponseStr = columns[8];

        boolean booleanResponse = booleanResponseStr.equals("TRUE") || booleanResponseStr.equals("true") || booleanResponseStr.equals("True");

        LOG.info("Received set block result: " + booleanResponse);

        currentRequest = null;

        return booleanResponse;
    }

    /**
     * local simulation finished evaluating a day. send STATE READY to server
     */
    public void sendStateReady() {
        String response = NetworkProtocol.buildStateReadyMessage();
        outWriter.println(response);
        LOG.info("C->S " + response);
    }

    /**
     * send current version of map blocks to server and be in STATE SET
     */
    public void sendStateSet() {
        String response = NetworkProtocol.buildStateSetMessage(this.simulation.map.getBlocks());
        outWriter.println(response);
        LOG.info("C->S " + response);
        simulation.getView().repaintJFrameClientSimulation();
        simulation.getView().repaintJFrameStats();
    }
}
