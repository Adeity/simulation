package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;

import java.io.File;

public class Main {
    public static void main (String[] args) {
        App app = new App();
        app.run();

//        File file = new File(CONF.MAP_TEMPLATE_DIRECTORY+"map1.txt");
//        System.out.println("hovno"+file.getName());

        Controller c = app.controller;

        c.command();

    }
}
