package cz.cvut.fel.pjv.simulation.model;

import java.util.Objects;

public abstract class Animal {

    public int energy;

    @Override
    public String toString() {
        String res = "";
        res += getClass().getSimpleName();
        return res;

    }

}
