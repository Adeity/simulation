package cz.cvut.fel.pjv.simulation.network.server;

import java.util.Comparator;

/**
 * Comparator so that table can be sorted by position.
 */
public class TableItemComparator implements Comparator<TableItem> {
    @Override
    public int compare(TableItem o1, TableItem o2) {
        if (o1.getPosition() > o2.getPosition()) {
            return 1;
        }
        else if (o1.getPosition() == o2.getPosition()) {
            return 0;
        }
        else if (o1.getPosition() < o2.getPosition()) {
            return -1;
        }
        return  0;
    }
}
