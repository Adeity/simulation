package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Builds server-client client-server communication messages.
 */
public class NetworkProtocol {
    private static final Logger LOG = Logger.getLogger(NetworkProtocol.class.getName());

    /**
     * e.g. GET_BLOCK [uuid] -1 -1
     * @param x is relative x coordinate
     * @param y is relative y coordinate
     * @param uuid is id of request
     * @return string request
     */
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

    /**
     * e.g. GET_BLOCK [uuid] 3 3 3 3 -1 -1
     * reroute GET_BLOCK from server to target client
     * @param x is x coordinate relative to target
     * @param y is y coordinate relative to target
     * @param globalX global coordinate
     * @param globalY global coordinate
     * @param minX identifier of requestor connection
     * @param minY identifier of requestor connection
     * @param uuid id of request
     * @return request
     */
    public static String buildGetBlockMessageFromServerToClient(int x, int y, int globalX, int globalY, int minX, int minY, String uuid) {
        String out = "";
        out = "GET_BLOCK";
        out += " ";
        out += uuid;
        out += " ";
        out += x;
        out += " ";
        out += y;
        out += " ";
        out += globalX;
        out += " ";
        out += globalY;
        out += " ";
        out += minX;
        out += " ";
        out += minY;
        return out;
    }

    /**
     * e.g. BLOCK [uuid] 3 3 3 3 -1 -1
     * BLOCK is reponse to GET_BLOCK message
     * @param x is x coordinate relative to target
     * @param y is y coordinate relative to target
     * @param globalX global coordinate
     * @param globalY global coordinate
     * @param minX identifier of requestor connection
     * @param minY identifier of requestor connection
     * @param uuid id of request
     * @return request
     */
    public static String buildBlockMessage (String uuid, int x, int y, int globalX, int globalY, int minX, int minY, Block block) {
        String out = "";
        out = "BLOCK";
        out += " ";
        out += uuid;
        out += " ";
        out += x;
        out += " ";
        out += y;
        out += " ";
        out += globalX;
        out += " ";
        out += globalY;
        out += " ";
        out += minX;
        out += " ";
        out += minY;
        out += " ";
        try {
            out += SerializationUtils.toString(block);
        } catch (IOException e) {
            LOG.info("BLOCK UUID serialized block failed, from server to client");
        }
        return out;
    }

    /**
     * e.g. SET_BLOCK [uuid] 3 3 3 3 -1 -1 [SERIALIZED_BLOCK]
     * reroute set_block to target client
     * @param x is x coordinate relative to target
     * @param y is y coordinate relative to target
     * @param globalX global coordinate
     * @param globalY global coordinate
     * @param minX identifier of requestor connection
     * @param minY identifier of requestor connection
     * @param uuid id of request
     * @param block is block to set
     * @return request
     */
    public static String buildSetBlockMessageFromServerToClient(int x, int y, int globalX, int globalY, int minX, int minY, String uuid, Block block) {
        String out = "";
        out += "SET_BLOCK";
        out += " ";
        out += uuid;
        out += " ";
        out += x;
        out += " ";
        out += y;
        out += " ";
        out += globalX;
        out += " ";
        out += globalY;
        out += " ";
        out += minX;
        out += " ";
        out += minY;
        out += " ";
        try {
            out += SerializationUtils.toString(block);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.severe("Trouble with serializing block in set Block");
        }
        return out;
    }

    /**
     * create SET_BLOCK message
     * @param x is x coordinate relative to target
     * @param y is y coordinate relative to target
     * @param uuid is identificator
     * @param block is block to set
     * @return the message
     */
    public static String buildSetBlockMessage(int x, int y, String uuid, Block block) {
        String out = "";
        out += "SET_BLOCK";
        out += " ";
        out += uuid;
        out += " ";
        out += x;
        out += " ";
        out += y;
        out += " ";
        try {
            out += SerializationUtils.toString(block);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Trouble with serializing block in set Block");
        }
        return out;
    }

    /**
     * create SET_BLOCK_RESULT message
     * @param uuid is identificator of the original request
     * @param x is x coordinate relative to target
     * @param y is y coordinate relative to target
     * @param globalX is the global x coordinate
     * @param globalY is the global y coordinate
     * @param minX is the minX identificator of original requestor
     * @param minY is the minY identificator or original requestor
     * @param result is boolean value
     * @return the message
     */
    public static String buildSetBlockResultMessage (String uuid, int x, int y, int globalX, int globalY, int minX, int minY, String result) {
        String out = "";
        out += "SET_BLOCK_RESULT";
        out += " ";
        out += uuid;
        out += " ";
        out += x;
        out += " ";
        out += y;
        out += " ";
        out += globalX;
        out += " ";
        out += globalY;
        out += " ";
        out += minX;
        out += " ";
        out += minY;
        out += " ";
        out += result;
        return out;
    }

    /**
     * build STATE READY message
     * @return
     */
    public static String buildStateReadyMessage() {
        String out = "";
        out += "STATE";
        out += " ";
        out += "READY";
        return out;
    }

    /**
     * build STATE SET message
     * @param blocks
     * @return
     */
    public static String buildStateSetMessage(Block[][] blocks) {
        String out = "";
        out += "STATE";
        out += " ";
        out += "SET";
        out += " ";
        try {
            out += SerializationUtils.toString(blocks);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.info("Build state message map serialization failed");
        }
        return out;
    }

}
