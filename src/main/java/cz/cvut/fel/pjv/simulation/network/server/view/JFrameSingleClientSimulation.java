package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;
import cz.cvut.fel.pjv.simulation.utils.Utilities;
import cz.cvut.fel.pjv.simulation.view.JFrameClientSimulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class JFrameSingleClientSimulation extends JFrame {
    private static final Logger LOG = Logger.getLogger(JFrameSingleClientSimulation.class.getName());

    SingleMapComponent mapComponent;
    SimulationServer simulationServer;
    int clientNumber;

    public JFrameSingleClientSimulation(SimulationServer simulationServer, int clientNumber) {
        this.simulationServer = simulationServer;
        this.clientNumber = clientNumber;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public void init() {
        ArrayList<Block[][]> blocks = this.simulationServer.getBlocks();


        Box verticalBox = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();

        //        buttons.add(btnShowStats);

        this.setTitle("Simulation single client map");
        //  this windows options
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
//        this.setSize(new Dimension(1200, 1200));

        this.setVisible(true);

        JPanel mapPanel = new JPanel();

        int width = 600;
        int heigth = 600;

        int widthDimension = simulationServer.getLocalMapSize();
        int heightDimension = simulationServer.getLocalMapSize();

        mapPanel.setSize(new Dimension(width, heigth));
        this.mapComponent = new SingleMapComponent(this.simulationServer, mapPanel.getWidth(), mapPanel.getHeight(), widthDimension, heightDimension);
        mapPanel.add(mapComponent);
        LOG.info("map panel width: " + mapPanel.getWidth());

        verticalBox.add(mapPanel);
        verticalBox.add(Box.createVerticalStrut(50));
        verticalBox.add(buttons);
        verticalBox.add(Box.createVerticalStrut(50));
        this.add(verticalBox);
        this.pack();
    }

    public void repaint() {
        super.repaint();
        this.mapComponent.repaint();
    }
}
