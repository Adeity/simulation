package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.App;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.controller.Controller;
import cz.cvut.fel.pjv.simulation.model.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.*;

public class JFrameSimulation extends JFrame implements ActionListener {
    private static final Logger LOG = Logger.getLogger(JFrameSimulation.class.getName());
    Simulation simulation;
    Controller controller;
    Map map;
    MapComponent mapComponent;

    JFrameStats jFrameStats = null;

    JButton btnNext = new JButton("Next");
    JButton btnShowStats = new JButton("Show stats");

    /**
     * generate map of size
     * @param size of map, n*n dimension
     */
    public JFrameSimulation(App app, int size) {
        this.controller = app.getController();
        this.simulation = app.getSimulation();
        controller.run(size);
        this.init();
    }

    /**
     * init map from saveName
     * @param saveName
     */
    public JFrameSimulation(App app, String saveName) {
        this.controller = app.getController();
        this.simulation = app.getSimulation();
        controller.load(saveName);
        this.init();
    }

    /**
     *
     * @param templateName valid name of template
     * @param t boolean value that shows, that init is from template
     */
    public JFrameSimulation(App app, String templateName, boolean t) {
        this.controller = app.getController();
        this.simulation = app.getSimulation();
        controller.run(templateName);
        this.init();
    }

    private void init() {
        LOG.setUseParentHandlers(false);
        Handler stdout = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOG.addHandler(stdout);
        stdout.setLevel(Level.FINEST);

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
        this.mapComponent = new MapComponent(this.simulation.map, mapPanel.getWidth());
        mapPanel.add(mapComponent);
        System.out.println(mapPanel.getWidth());


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
                this.controller.simulateDay();
                this.mapComponent.repaint();
                break;
            case "Show stats":
                LOG.fine("Opening stats");
                // TODO: open new window with stats
                if(jFrameStats == null) {
                    jFrameStats = new JFrameStats(this.simulation.map);
                }
                break;
        }
        if(jFrameStats != null) {
            jFrameStats.updateLabels();
            jFrameStats.repaint();
        }
        this.repaint();
    }
}
