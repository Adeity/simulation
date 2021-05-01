package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;

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

    public static String buildSetBlockMessage (int x, int y, String uuid, Block block) {
        String out = "";
        out += "SET_BLOCK";
        out += " ";
        out += uuid;
        out += " ";
        try {
            out += SerializationUtils.toString(block);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Trouble with serializing block in set Block");
        }
        return out;
    }

    public static String buildSetBlockResultMessage (String uuid, boolean result) {
        String out = "";
        out += "SET_BLOCK_RESULT";
        out += " ";
        out += uuid;
        out += " ";
        out += result;
        return out;
    }

    public static String buildStateReadyMessage(Block[][] blocks) {
        String out = "";
        out += "STATE";
        out += " ";
        out += "READY";
        out += " ";
        try {
            out += SerializationUtils.toString(blocks);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Build state message map serialization failed");
        }
        return out;
    }
}
