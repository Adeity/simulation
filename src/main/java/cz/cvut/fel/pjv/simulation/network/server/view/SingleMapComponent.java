package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;
import cz.cvut.fel.pjv.simulation.utils.Utilities;
import cz.cvut.fel.pjv.simulation.view.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Graphical representation of map of simulation for one specific client.
 */
public class SingleMapComponent extends JComponent {
    SimulationServer simulationServer;

    private static final Logger LOG = Logger.getLogger(SingleMapComponent.class.getName());
    int heigthDimension;
    int widthDimension;

    int panelWidth;
    int panelHeight;

    private Tile[][] terrainGrid;

    public SingleMapComponent(SimulationServer simulationServer, int panelWidth, int panelHeigth, int widthDimension, int heigthDimension){
        this.simulationServer = simulationServer;
        this.heigthDimension = heigthDimension;
        this.widthDimension = widthDimension;

        this.panelWidth = panelWidth;
        this.panelHeight = panelHeigth;
        this.terrainGrid = new Tile[heigthDimension][widthDimension];
        this.updateTiles();
        setPreferredSize(new Dimension(panelWidth, panelHeigth));
    }

    private void updateTiles() {
        ArrayList<Block[][]> blocks = this.simulationServer.getBlocks();

        Block[][] blocksToPaint = null;
        int clientNumber = this.simulationServer.getView().getjFrameSingleClientSimulation().getClientNumber();

        if (clientNumber == 0) {
            blocksToPaint = blocks.get(0);
        }
        else if (clientNumber == 1) {
            blocksToPaint = blocks.get(1);
        }
        else if (clientNumber == 2) {
            blocksToPaint = blocks.get(2);
        }
        else if (clientNumber == 3) {
            blocksToPaint = blocks.get(3);
        }

        if (blocksToPaint == null) {
            return;
        }

        paintTheBlocks(blocksToPaint);

    }

    @Override
    public void repaint() {
        updateTiles();
        super.repaint();
    }

    private void paintTheBlocks(Block[][] blocks) {
        Color terrainColor;
        Color animalColor;
        int tileWidth = panelWidth / widthDimension;
        int eachMapSize = this.simulationServer.getLocalMapSize();
        for (int i = 0; i < eachMapSize; i++) {
            for (int j = 0; j < eachMapSize; j++) {
                terrainColor = null;
                animalColor = null;
                if (blocks[i][j].getTerrain() == Block.Terrain.GRASS) {
                    terrainColor = Colors.GRASS;
                } else if (blocks[i][j].getTerrain() == Block.Terrain.BUSH) {
                    terrainColor = Colors.BUSH;
                } else if (blocks[i][j].getTerrain() == Block.Terrain.WATER) {
                    terrainColor = Colors.WATER;
                } else if (blocks[i][j].getTerrain() == Block.Terrain.GRASS_WITH_GRAIN) {
                    terrainColor = Colors.GRASS_WITH_GRAIN;
                }
                if (blocks[i][j].getAnimal() == null) {
                    animalColor = null;
                } else if (blocks[i][j].getAnimal() instanceof Fox) {
                    animalColor = Colors.FOX;
                } else if (blocks[i][j].getAnimal() instanceof Hare) {
                    animalColor = Colors.HARE;
                }

                LOG.info("i index is: " + i + " | j index is: " + j);
                if (this.terrainGrid[i][j] == null) {
                    this.terrainGrid[i][j] = new Tile(terrainColor, animalColor, i, j, this.panelWidth / widthDimension);
                } else {
                    this.terrainGrid[i][j].terrainColor = terrainColor;
                    this.terrainGrid[i][j].animalColor = animalColor;
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.updateTiles();
        g.clearRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < heigthDimension; i++) {
            for (int j = 0; j < widthDimension; j++) {
                if (terrainGrid[i][j] == null) {
                    continue;
                }
                terrainGrid[i][j].paintComponent(g);
            }
        }
    }
}
