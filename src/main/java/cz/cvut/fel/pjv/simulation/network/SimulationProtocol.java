package cz.cvut.fel.pjv.simulation.network;

public class SimulationProtocol {
    int hovnoCounter = 0;

    public SimulationProtocol() {
    }

    public String processInput (String inputLine) {
        if (inputLine == null) {
            return "Connected";
        }
        else if(inputLine.equals("SENDING_MAP")) {
            //expect map
            return "RECEIVING_MAP";
        }
        else if(inputLine.equals("PRINT_STATS")) {
            return "SENDING_STATS";
        }
        else {
            return inputLine + " ok beru";
        }
    }
}
