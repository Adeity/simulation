package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;

import javax.swing.*;

/**
 * this is kind of a router for every jframe
 */
public class View {
    SimulationServer simulationServer;

    JFrameServerInit jFrameServerInit;

    JFrameSingleClientSimulation jFrameSingleClientSimulation;

    JFrameServerSimulation jFrameServerSimulation;

    public View(SimulationServer simulationServer) {
        this.simulationServer = simulationServer;
    }

    /**
     * open simulation map of a single client.
     * @param clientNumber is client number who's map is to be shown
     */
    public void openJFrameSingleClientSimulation(int clientNumber) {
        if (jFrameSingleClientSimulation == null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrameSingleClientSimulation = new JFrameSingleClientSimulation(simulationServer, clientNumber);
                    jFrameSingleClientSimulation.setClientNumber(clientNumber);
                    jFrameSingleClientSimulation.init();
                    jFrameSingleClientSimulation.setVisible(true);
                }
            });

        }
        else {
            jFrameSingleClientSimulation.setClientNumber(clientNumber);
            jFrameSingleClientSimulation.setVisible(true);
            jFrameSingleClientSimulation.repaint();
        }
    }

    /**
     * repaint single client window
     */
    public void repaintJFrameSingleClientSimulation() {
        if (jFrameSingleClientSimulation == null) {
            return;
        }
        jFrameSingleClientSimulation.repaint();
    }

    /**
     * open first windows of server app
     */
    public void openJFrameServerInit() {
        if (jFrameServerInit == null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrameServerInit = new JFrameServerInit(simulationServer);
                    jFrameServerInit.setVisible(true);
                }
            });
        }
        else {
            jFrameServerInit.setVisible(true);
        }
    }

    /**
     * Repaints jframe if it is not null
     */
    public void repaintJFrameServerInit() {
        if (jFrameServerInit == null) {
            return;
        }
        jFrameServerInit.repaint();
    }

    /**
     * Opens JFrame with global map simulation
     */
    public void openJFrameServerSimulation(SimulationServer simulationServer) {
        if (jFrameServerSimulation == null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrameServerSimulation = new JFrameServerSimulation(simulationServer);
                    jFrameServerSimulation.init();
                    jFrameServerSimulation.setVisible(true);
                }
            });
        }
        else {
            this.jFrameServerSimulation.setVisible(true);
            jFrameServerSimulation.repaint();
        }

    }

    /**
     * Repaint JFrame with global map of simulation
     */
    public void repaintJFrameServerSimulation () {
        if (jFrameServerSimulation == null) {
            return;
        }
//        this.jFrameServerSimulation.setVisible(true);
        jFrameServerSimulation.repaint();
    }


    /**
     * JFrame init is what you first see when you open server side application.
     */
    public JFrameServerInit getjFrameServerInit() {
        return jFrameServerInit;
    }

    /**
     * JFrameSingleClientSimulation represents windows where map of a single client is shown.
     */
    public JFrameSingleClientSimulation getjFrameSingleClientSimulation() {
        return jFrameSingleClientSimulation;
    }

    /**
     * JFrameServerSimulation is a window where global map of distributed simulation is drawn.
     * @return
     */
    public JFrameServerSimulation getjFrameServerSimulation() {
        return jFrameServerSimulation;
    }
}
