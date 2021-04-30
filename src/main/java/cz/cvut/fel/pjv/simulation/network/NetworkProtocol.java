package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Block;

import java.io.IOException;

public class NetworkProtocol {

    public static String buildGetBlockMessage (int x, int y, String uuid) {
        String out = "";
        out = "GET_BLOCK";
        out += " ";
        out += uuid;
        out += " ";
        out += x;
        out += " ";
        out += y;
        return out;
    }

    public static String buildBlockMessage (String uuid, Block block) {
        String out = "";
        out += uuid;
        out += " ";
        out = "BLOCK";
        out += " ";
        try {
            out += SerializationUtils.toString(block);
        } catch (IOException e) {
            System.out.println("BLOCK UUID serialized block failed, from server to client");
        }
        return out;
    }
}
