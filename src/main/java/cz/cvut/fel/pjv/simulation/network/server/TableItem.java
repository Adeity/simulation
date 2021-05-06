package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;

import java.util.Comparator;

/**
 * Table item has information about connection, its minX, minY, maxX, maxY global map identifying coordinates. Also it's state that can be READY, SET or GO. Also after each round, it stores updated version of their block[][] representation fo map.
 */
public class TableItem {
    SimulationServerThread connection;
    String status;
    Block[][] blocks;
    int position;
    int minX;
    int maxX;
    int minY;
    int maxY;

    public TableItem(int position, SimulationServerThread connection, String status) {
        this.connection = connection;
        this.status = status;
        this.position = position;
    }

    /**
     * Position indicates where is connection placed on global simulation map.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Position indicates where is connection placed on global simulation map.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * gets new table entry
     * @param position is position of client on map
     * @param sizeOfMap is each client local size of map
     * @param connection is connection for entry
     * @param status is status of entry
     * @return instance of tableItem
     */
    public static TableItem getInstance(int position, int sizeOfMap, SimulationServerThread connection, String status) {
        TableItem tableItem = new TableItem(position, connection, status);
        if (position == 0) {
            tableItem.setMinX(0);
            tableItem.setMaxX(sizeOfMap - 1);
            tableItem.setMinY(0);
            tableItem.setMaxY(sizeOfMap - 1);
        }
        else if (position == 1) {
            tableItem.setMinX(0);
            tableItem.setMaxX(
                    sizeOfMap - 1
            );
            tableItem.setMinY(
                    sizeOfMap
            );
            tableItem.setMaxY(
                    (2 * sizeOfMap) - 1
            );
        }
        else if (position == 2) {
            tableItem.setMinX(
                    sizeOfMap
            );
            tableItem.setMaxX(
                    (2 * sizeOfMap) - 1
            );
            tableItem.setMinY(0);
            tableItem.setMaxY(
                    sizeOfMap - 1
            );
        }
        else if (position == 3) {
            tableItem.setMinX(
                    sizeOfMap
            );
            tableItem.setMaxX(
                    (2 * sizeOfMap) - 1)
            ;
            tableItem.setMinY(
                    sizeOfMap
            );
            tableItem.setMaxY(
                    (2 * sizeOfMap) - 1
            );
        }
        return tableItem;
    }


    private void setMinX(int minX) {
        this.minX = minX;
    }


    private void setMaxX(int maxX) {
        this.maxX = maxX;
    }


    private void setMinY(int minY) {
        this.minY = minY;
    }


    private void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public SimulationServerThread getConnection() {
        return connection;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Block[][] getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

}
