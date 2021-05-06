package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.Simulation;

import javax.swing.*;

/**
 * This is a view router with all JFrames.
 */
public class View {
    Simulation simulation;

    JFrameInit jFrameInit;
    JFrameSimulation jFrameSimulation;
    JFrameStats jFrameStats;
    JFrameClientSimulation jFrameClientSimulation;

    public View(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * JFrameInit is what you first see when you open simulation application on client side.
     */
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

    /**
     * JFrameInit is what you first see when you open simulation application on client side.
     */
    public void closeJFrameInit() {

    }

    /**
     * JFrameSimulation is a window with local client simulation map.
     */
    protected void openJFrameSimulation() {
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

    /**
     * JFrameSimulation is a window with local client simulation map.
     */
    public void repaintJFrameSimulation() {
        if(jFrameSimulation != null) {
            this.jFrameSimulation.repaint();
        }
    }

    /**
     * JFrameClientSimulation is a window with local client simulation map for when client is connected to the server.
     */
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

    /**
     * JFrameClientSimulation is a window with local client simulation map for when client is connected to the server.
     */
    public void repaintJFrameClientSimulation() {
        if (jFrameClientSimulation != null) {
            this.jFrameClientSimulation.repaint();
        }
    }


    /**
     * JFrameStats is a windows with information about current state of the map. Such as number of foxes and hare.
     */
    public void openJFrameStats() {
        if(jFrameStats == null) {
            jFrameStats = new JFrameStats(this.simulation);
        }
        jFrameStats.setVisible(true);
    }

    /**
     * JFrameStats is a windows with information about current state of the map. Such as number of foxes and hare.
     */
    public void repaintJFrameStats() {
        if (jFrameStats == null) {
            return;
        }
        jFrameStats.updateLabels();
        jFrameStats.repaint();
    }

    /**
     * JFrameInit is what you first see when you open simulation application on client side.
     */
    public JFrameInit getjFrameInit() {
        return jFrameInit;
    }

    /**
     * JFrameSimulation is a window with local client simulation map.
     */
    public JFrameSimulation getjFrameSimulation() {
        return jFrameSimulation;
    }

    /**
     * JFrameStats is a windows with information about current state of the map. Such as number of foxes and hare.
     */
    public JFrameStats getjFrameStats() {
        return jFrameStats;
    }

    /**
     * JFrameClientSimulation is a window with local client simulation map for when client is connected to the server.
     */
    public JFrameClientSimulation getjFrameClientSimulation() {
        return jFrameClientSimulation;
    }
}
