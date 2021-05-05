package cz.cvut.fel.pjv.simulation.utils;

import cz.cvut.fel.pjv.simulation.view.JFrameSimulation;

import java.util.logging.*;

public class Utilities {
    /**
     * get random whole number from to
     * @param min from number
     * @param max to to
     */
    public static int getRandomNumber (int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
