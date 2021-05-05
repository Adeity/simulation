package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.utils.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JFrame window for when simulation is connected to server
 */
public class JFrameClientSimulation extends JFrame implements ActionListener {
    private static final Logger LOG = Logger.getLogger(JFrameClientSimulation.class.getName());
    Simulation simulation;
    MapComponent mapComponent;
    JButton btnShowStats = new JButton("Show stats");

    public JFrameClientSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void init() {
        LOG.setLevel(Level.OFF);

        Box verticalBox = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();
        buttons.add(btnShowStats);


        this.setTitle("Simulation client");

        this.addActionListeners();
        //  this windows options
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        this.setSize(new Dimension(1200, 1200));

        this.setVisible(true);

        JPanel mapPanel = new JPanel();
        mapPanel.setSize(new Dimension(500, 500));
        this.mapComponent = new MapComponent(this.simulation, mapPanel.getWidth());
        mapPanel.add(mapComponent);
        LOG.info("map panel width: " + mapPanel.getWidth());

        verticalBox.add(mapPanel);
        verticalBox.add(Box.createVerticalStrut(50));
        verticalBox.add(buttons);
        verticalBox.add(Box.createVerticalStrut(50));
        this.add(verticalBox);
        this.pack();
    }

    private void addActionListeners() {
        btnShowStats.addActionListener(this);
    }

    public void repaint() {
        super.repaint();
        if (mapComponent != null) {
            this.mapComponent.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Show stats":
                LOG.info("Opening stats");
                this.simulation.getView().openJFrameStats();
                break;
        }
    }
}