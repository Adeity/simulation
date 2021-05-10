package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.App;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.Map;
import cz.cvut.fel.pjv.simulation.utils.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.*;

/**
 * This shows map of simulation in local mode.
 */
public class JFrameSimulation extends JFrame implements ActionListener {
    private static final Logger LOG = Logger.getLogger(JFrameSimulation.class.getName());
    Simulation simulation;
    MapComponent mapComponent;

    JButton btnNext = new JButton("Next");
    JButton btnShowStats = new JButton("Show stats");

    public JFrameSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void init() {
        LOG.setLevel(Level.OFF);
        Box verticalBox = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();
        buttons.add(btnNext);
        buttons.add(btnShowStats);


        this.setTitle("Simulation");

        this.addActionListeners();
        //  this windows options
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        this.setSize(new Dimension(1200, 1200));

        this.setVisible(true);

        JPanel mapPanel = new JPanel();
        mapPanel.setSize(new Dimension(800, 800));
        this.mapComponent = new MapComponent(this.simulation, mapPanel.getWidth());
        mapPanel.add(mapComponent);
        LOG.fine("mapPanel width: " + mapPanel.getWidth());

        verticalBox.add(mapPanel);
        verticalBox.add(Box.createVerticalStrut(50));
        verticalBox.add(buttons);
        verticalBox.add(Box.createVerticalStrut(50));
        this.add(verticalBox);
        this.pack();
    }

    private void addActionListeners() {
        btnNext.addActionListener(this);
        btnShowStats.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Next":
                LOG.fine("Simulating next day");
                this.simulation.simulateDay();
                break;
            case "Show stats":
                LOG.fine("Opening stats");
                this.simulation.getView().openJFrameStats();
                break;
        }
    }

    public void repaint() {
        super.repaint();
        if (mapComponent != null) {
            this.mapComponent.repaint();
        }
    }
}
