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



//    private static final Logger LOG = Logger.getLogger(JFrameSimulation.class.getName());
    /**
     * Sets system out handler for logger
     * @param LOG is Logger that gets a handler set
     */
    public static void addHandlerToLogger(Logger LOG) {
        LOG.setUseParentHandlers(false);
        Handler stdout = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOG.addHandler(stdout);
        stdout.setLevel(Level.FINEST);
    }
}
