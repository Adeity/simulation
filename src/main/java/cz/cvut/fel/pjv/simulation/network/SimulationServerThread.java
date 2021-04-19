package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Map;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimulationServerThread implements Runnable {
    Socket clientSocket;
    SimulationServerMap simulationServerMap;
    Map map;

    public SimulationServerThread(SimulationServerMap simulationServer, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.simulationServerMap = simulationServer;
    }

    @Override
    public void run() {
        try (
                PrintWriter outWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader inReader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                ObjectOutputStream outObject = new ObjectOutputStream(clientSocket.getOutputStream()); // get the output stream of client.
                ObjectInputStream inObject = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            String inputLine, outputLine;

            //  Initiate conversation with client
            SimulationProtocol simulationProtocol = new SimulationProtocol();
            outputLine = simulationProtocol.processInput(null);
            outWriter.println(outputLine);

            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

            while ((inputLine = inReader.readLine()) != null) {

                outputLine = simulationProtocol.processInput(inputLine);
                if (outputLine.equals("RECEIVING_MAP")) {
                    this.map = (Map) inObject.readObject();
                    this.simulationServerMap.setMap(Integer.parseInt(Thread.currentThread().getName()), map);
                    System.out.println(this.map);
                }
                else if(outputLine.equals("SENDING_STATS")) {
                    System.out.println(simulationServerMap.stats());
                    outWriter.write(outputLine);
                    outObject.writeObject(simulationServerMap.stats());
                }

                System.out.println(formatter.format(System.currentTimeMillis())+" THREAD: " + Thread.currentThread().getName() + " | Client: " + inputLine + " | Server: " + outputLine);

                outWriter.println(outputLine);
                if (outputLine.equals("Bye."))
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
