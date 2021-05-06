package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.controller.ControllerNetwork;
import cz.cvut.fel.pjv.simulation.view.JFrameInit;

import javax.swing.*;

/**
 * Run this to start client side simulation application.
 */
public class Main {
    public static void main (String[] args) {
        App app = new App();
        app.simulation.setView(app.view);
//
//        Controller c = app.controller;
//        ControllerNetwork nc = app.controllerNetwork;

//        c.run(40);

//        nc.createClient("127.0.0.1", 8888);
//        app.simulation.connectToServer("localhost", 8888);
//        c.command();

//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        app.simulation.getView().openJFrameInit();
    }
}
