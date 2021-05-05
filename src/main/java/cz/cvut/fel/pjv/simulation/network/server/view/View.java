package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;

import javax.swing.*;

public class View {
    SimulationServer simulationServer;

    JFrameServerInit jFrameServerInit;

    JFrameSingleClientSimulation jFrameSingleClientSimulation;

    JFrameServerSimulation jFrameServerSimulation;

    public View(SimulationServer simulationServer) {
        this.simulationServer = simulationServer;
    }

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

    public void repaintJFrameSingleClientSimulation() {
        if (jFrameSingleClientSimulation == null) {
            return;
        }
        jFrameSingleClientSimulation.repaint();
    }

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

    public void repaintJFrameServerInit() {
        if (jFrameServerInit == null) {
            return;
        }
        jFrameServerInit.repaint();
    }

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

    public void repaintJFrameServerSimulation () {
        if (jFrameServerSimulation == null) {
            return;
        }
//        this.jFrameServerSimulation.setVisible(true);
        jFrameServerSimulation.repaint();
    }


    public JFrameServerInit getjFrameServerInit() {
        return jFrameServerInit;
    }

    public JFrameSingleClientSimulation getjFrameSingleClientSimulation() {
        return jFrameSingleClientSimulation;
    }

    public JFrameServerSimulation getjFrameServerSimulation() {
        return jFrameServerSimulation;
    }
}
