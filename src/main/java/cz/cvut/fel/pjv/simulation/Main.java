package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;

public class Main {
    public static void main (String[] args) {
        App app = new App();
        app.run();

        Controller c = app.controller;

        c.command();
        while(app.simulation.isRunning) {
            c.command();
        }
    }
}
