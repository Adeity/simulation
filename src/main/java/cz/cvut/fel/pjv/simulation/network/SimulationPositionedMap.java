package cz.cvut.fel.pjv.simulation.network;

import cz.cvut.fel.pjv.simulation.model.Map;

public class SimulationPositionedMap {
    Map map;
    int x;
    int y;

    public SimulationPositionedMap (int x, int y, Map map) {
        this.map = map;
    }

    public SimulationPositionedMap (Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
