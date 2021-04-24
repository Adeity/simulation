package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.controller.ControllerNetwork;
import cz.cvut.fel.pjv.simulation.view.JFrameInit;

import javax.swing.*;

public class Main {
    public static void main (String[] args) {
        App app = new App();
        app.run();

        Controller c = app.controller;
        ControllerNetwork nc = app.controllerNetwork;

//        c.command();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrameInit JFrameInit = new JFrameInit(app);
                JFrameInit.setVisible(true);
            }
        });
    }
}
