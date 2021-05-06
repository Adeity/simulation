package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;
import cz.cvut.fel.pjv.simulation.utils.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * this shows every client map on server side application
 */
public class JFrameServerSimulation extends JFrame implements ActionListener {
    private static final Logger LOG = Logger.getLogger(JFrameServerSimulation.class.getName());

    SimulationServer simulationServer;

    JButton btn1 = new JButton("Client1");
    JButton btn2 = new JButton("Client2");
    JButton btn3 = new JButton("Client3");
    JButton btn4 = new JButton("Client4");

    MapComponent mapComponent;

    public JFrameServerSimulation(SimulationServer simulationServer) {
        this.simulationServer = simulationServer;
    }

    public void init() {
        ArrayList<Block[][]> blocks = this.simulationServer.getBlocks();


        Box verticalBox = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();

        int numOfClients = this.simulationServer.getNumOfConnectedClients();

        if (numOfClients > 3) {
            buttons.add(btn1);
            buttons.add(btn2);
            buttons.add(btn3);
            buttons.add(btn4);
        }
        else if ( numOfClients > 2) {
            buttons.add(btn1);
            buttons.add(btn2);
            buttons.add(btn3);
        }
        else if (numOfClients > 1) {
            buttons.add(btn1);
            buttons.add(btn2);
        }
        else if (numOfClients > 0) {
            buttons.add(btn1);
        }
        //        buttons.add(btnShowStats);

        this.setTitle("Simulation client");

        this.addActionListeners();
        //  this windows options
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        this.setSize(new Dimension(1200, 1200));

        this.setVisible(true);

        JPanel mapPanel = new JPanel();

        int widthMultiplier = 1;
        if (simulationServer.getNumOfConnectedClients() > 1) {
            widthMultiplier = 2;
        }
        int heigthMultiplier = 1;
        if (simulationServer.getNumOfConnectedClients() > 2) {
            heigthMultiplier = 2;
        }

        int width = 300 * widthMultiplier;
        int heigth = 300 * heigthMultiplier;

        int widthDimension = widthMultiplier * simulationServer.getLocalMapSize();
        int heightDimension = heigthMultiplier * simulationServer.getLocalMapSize();

        mapPanel.setSize(new Dimension(width, heigth));
        this.mapComponent = new MapComponent(this.simulationServer, mapPanel.getWidth(), mapPanel.getHeight(), widthDimension, heightDimension);
        mapPanel.add(mapComponent);
        LOG.info("mapPanel width: " + mapPanel.getWidth());

        verticalBox.add(mapPanel);
        verticalBox.add(Box.createVerticalStrut(50));
        verticalBox.add(buttons);
        verticalBox.add(Box.createVerticalStrut(50));
        this.add(verticalBox);
        this.pack();
    }

    private void addActionListeners() {
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btn3.addActionListener(this);
        btn4.addActionListener(this);
    }

    public void repaint() {
        super.repaint();
        this.mapComponent.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Client1":
                LOG.info("Opening client1 JFrame");
                this.simulationServer.getView().openJFrameSingleClientSimulation(0);
                break;
            case "Client2":
                this.simulationServer.getView().openJFrameSingleClientSimulation(1);
                break;
            case "Client3":
                this.simulationServer.getView().openJFrameSingleClientSimulation(2);
                break;
            case "Client4":
                this.simulationServer.getView().openJFrameSingleClientSimulation(3);
                break;
        }
    }
}
