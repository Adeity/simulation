package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;

import java.io.File;

public class Main {
    public static void main (String[] args) {
        App app = new App();
        app.run();

        Controller c = app.controller;

        c.command();

    }
}
