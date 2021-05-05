package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.Simulation;

import javax.swing.*;

public class View {
    Simulation simulation;

    JFrameInit jFrameInit;
    JFrameSimulation jFrameSimulation;
    JFrameStats jFrameStats;
    JFrameClientSimulation jFrameClientSimulation;

    public View(Simulation simulation) {
        this.simulation = simulation;
    }

    public void openJFrameInit() {
        if (jFrameInit == null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrameInit = new JFrameInit(simulation);
                    jFrameInit.setVisible(true);
                }
            });
        }
        else {
            jFrameInit.setVisible(true);
        }

    }

    public void closeJFrameInit() {

    }

    public void openJFrameSimulation() {
        if (jFrameSimulation == null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrameSimulation = new JFrameSimulation(simulation);
                    jFrameSimulation.init();
                    jFrameSimulation.setVisible(true);

                }
            });
        }
        else {
            jFrameSimulation.repaint();
        }
    }

    public void repaintJFrameSimulation() {
        if(jFrameSimulation != null) {
            this.jFrameSimulation.repaint();
        }
    }

    public void openJFrameClientSimulation() {
        if (jFrameClientSimulation == null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrameClientSimulation = new JFrameClientSimulation(simulation);
                    jFrameClientSimulation.init();
                    jFrameClientSimulation.setVisible(true);
                }
            });
        }
        else {
            this.jFrameClientSimulation.setVisible(true);
        }
    }

    public void repaintJFrameClientSimulation() {
        if (jFrameClientSimulation != null) {
            this.jFrameClientSimulation.repaint();
        }
    }

    public void closeJFrameSimulation() {

    }

    public void openJFrameStats() {
        if(jFrameStats == null) {
            jFrameStats = new JFrameStats(this.simulation);
        }
        jFrameStats.setVisible(true);
    }

    public void repaintJFrameStats() {
        if (jFrameStats == null) {
            return;
        }
        jFrameStats.updateLabels();
        jFrameStats.repaint();
    }

    public void closeJFrameStats() {

    }
}
