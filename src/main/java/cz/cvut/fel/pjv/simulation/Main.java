package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.view.View;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main (String[] args) {
        App app = new App();
        app.run();

        Controller c = app.controller;

//        c.command();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                View view = new View(app);
                view.setVisible(true);
            }
        });



    }
}
