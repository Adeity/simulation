package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Map;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class SimulationClient implements Runnable{
    private static final Logger LOG = Logger.getLogger(SimulationClient.class.getName());
    Socket socket = null;
    ObjectOutputStream outObject = null;
    ObjectInputStream inObject = null;
    PrintWriter outWriter = null;
    BufferedReader inReader = null;

    BufferedReader systemIn;

    String host;
    int port;

    Map map;

    public SimulationClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendMapToServer(Map map) {
        this.map = map;
        try {
            outWriter.write("SENDING_MAP");
            outObject.writeObject(this.map);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try{
            inObject.close();
            outObject.close();
            socket.close();
            systemIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            outObject = new ObjectOutputStream(socket.getOutputStream()); // get the output stream of client.
            inObject = new ObjectInputStream(socket.getInputStream());

            outWriter = new PrintWriter(socket.getOutputStream(), true);
            inReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String fromServer;
            String fromUser;

            Scanner sc = new Scanner(System.in);
            while ((fromServer = inReader.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                else if (fromServer.equals("SENDING_STATS")) {
                    try {
                        String stats = (String) inObject.readObject();
                        System.out.println(stats);
                    }
                    catch (ClassNotFoundException e) {
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
}
