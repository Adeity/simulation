package cz.cvut.fel.pjv.simulation.network.client;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.network.NetworkProtocol;
import cz.cvut.fel.pjv.simulation.network.SerializationUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

public class SimulationClient implements Runnable{
    private static final Logger LOG = Logger.getLogger(SimulationClient.class.getName());
    Socket socket = null;
    PrintWriter outWriter = null;
    BufferedReader inReader = null;

    Simulation simulation;

    Request currentRequest;

    String host;
    int port;

    Map map;

    public SimulationClient(String host, int port, Simulation simulation) {
        this.host = host;
        this.port = port;
        this.simulation = simulation;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            System.out.println("Trouble connecting to server");
        }

        try {
            outWriter = new PrintWriter(socket.getOutputStream(), true);
            inReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

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
            socket = new Socket(host, port);
            String in;
            String out;

            while ((in = inReader.readLine()) != null) {
                System.out.println(in);
                String[] columns = in.split(" ");
                String messageType = columns[0];

                if (messageType.equals("GO")) {
                    outWriter.write("STATE GO");
                    this.simulation.simulateDay();
                    outWriter.write("STATE READY");
                }

                else if (messageType.equals("MAP")) {
                    int sizeOfMap = Integer.parseInt(columns[1]);

                    this.simulation.run(sizeOfMap);
                    outWriter.write("STATE READY");
                    System.out.println("WROTE STATE READY");
                }

                else if (messageType.equals("BLOCK")) {
                    String messageUUID = columns[1];

                    //  there is no active response, continue
                    if (currentRequest == null) {
                        continue;
                    }
                    if (currentRequest.uuid.equals(messageUUID)) {
                        this.currentRequest.setResponse(in);
                        currentRequest.notify();
                    }
                }
                else if (messageType.equals("GET_BLOCK")) {
                    String messageUUID = columns[1];

                    int x = Integer.parseInt(columns[2]);
                    int y = Integer.parseInt(columns[3]);

                    Block block = this.simulation.map.getBlock(x, y);
                    outWriter.write(NetworkProtocol.buildBlockMessage(messageUUID, block));
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
        currentRequest = new Request(
                NetworkProtocol.buildGetBlockMessage(x, y, uuid),
                uuid
        );
        outWriter.write(currentRequest.getRequest());

        try {
            currentRequest.wait(1000);
        }
        catch (InterruptedException e) {
        }
        if(currentRequest.response == null) {
            currentRequest = null;
            return null;
        }
        String[] response = currentRequest.response.split(" ");
        Block block;
        try {
            block = (Block) SerializationUtils.fromString(response[3]);
        } catch (IOException  | ClassNotFoundException e) {
            System.out.println("Deserialization on client failed");
            return null;
        }
        currentRequest = null;
        return block;
    }
}
