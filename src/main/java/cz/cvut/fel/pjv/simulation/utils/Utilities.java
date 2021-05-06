package cz.cvut.fel.pjv.simulation.utils;

import cz.cvut.fel.pjv.simulation.view.JFrameSimulation;

import java.util.logging.*;

/**
 * Utilities class with static getRandomNumber method. Used for generating animals on map.
 */
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
