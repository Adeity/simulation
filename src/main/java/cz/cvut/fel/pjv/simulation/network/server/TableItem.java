package cz.cvut.fel.pjv.simulation.network.server;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Map;

public class TableItem {
    SimulationServerThread connection;
    String status;
    Block[][] blocks;
    int minX;
    int maxX;
    int minY;
    int maxY;

    public TableItem(SimulationServerThread connection, String status) {
        this.connection = connection;
        this.status = status;
    }

    public static TableItem getInstance(int connectionNum, int sizeOfMap, SimulationServerThread connection, String status) {
        TableItem tableItem = new TableItem(connection, status);

        if (connectionNum == 0) {
            tableItem.setMinX(0);
            tableItem.setMaxX(sizeOfMap - 1);
            tableItem.setMinY(0);
            tableItem.setMaxY(sizeOfMap - 1);
        }
        else if (connectionNum == 1) {
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
        else if (connectionNum == 2) {
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
        else if (connectionNum == 3) {
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

    private int getMinX() {
        return minX;
    }

    private void setMinX(int minX) {
        this.minX = minX;
    }

    private int getMaxX() {
        return maxX;
    }

    private void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    private int getMinY() {
        return minY;
    }

    private void setMinY(int minY) {
        this.minY = minY;
    }

    private int getMaxY() {
        return maxY;
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
