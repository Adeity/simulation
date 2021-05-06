package cz.cvut.fel.pjv.simulation.network.server.view;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;
import cz.cvut.fel.pjv.simulation.utils.Utilities;
import cz.cvut.fel.pjv.simulation.view.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.*;

/**
 * Graphical representation of blocks of global simulation map.
 */
public class MapComponent extends JComponent {
    private static final Logger LOG = Logger.getLogger(MapComponent.class.getName());

    SimulationServer simulationServer;
    int heigthDimension;
    int widthDimension;

    int panelWidth;
    int panelHeight;

    private Tile[][] terrainGrid;


    public MapComponent(SimulationServer simulationServer, int panelWidth, int panelHeigth, int widthDimension, int heigthDimension){
//        Utilities.addHandlerToLogger(LOG);
        LOG.setLevel(Level.OFF);
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
        int num = 0;
        int localMapSize = this.simulationServer.getLocalMapSize();
        for (Block[][] blocks1 : blocks) {
            if (num == 0) {
                paintTheBlocks(0, 0, blocks1);
            }
            else if (num == 1) {
                paintTheBlocks(0, localMapSize, blocks1);

            }
            else if (num == 2){
                paintTheBlocks(localMapSize, 0, blocks1);
            }
            else if (num == 3) {
                paintTheBlocks(localMapSize, localMapSize, blocks1);
            }
            num++;
        }
    }

    @Override
    public void repaint() {
        updateTiles();
        super.repaint();
    }

    private void paintTheBlocks(int startX, int startY, Block[][] blocks) {
        Color terrainColor;
        Color animalColor;
        int tileWidth;
        LOG.info("Heigth dimension is: " + heigthDimension + " | Width dimension is: " + widthDimension);
        LOG.info("Panel heigth is: " + panelHeight + " | Panel width is: " + panelWidth);
        if (heigthDimension > widthDimension || heigthDimension == widthDimension) {
            LOG.info("Heigth dimension is bigger or they are the same.");
            tileWidth = panelHeight / heigthDimension;
            LOG.info("Tile width ended up beign: " + panelHeight + " / " + heigthDimension + " = " + tileWidth);
        }
        else {
            LOG.info("Heigth dimension is smaller than width dimension.");
            tileWidth = panelWidth / widthDimension;
            LOG.info("Tile width ended up beign: " + panelWidth + " / " + widthDimension + " = " + tileWidth);
        }
        int eachMapSize = this.simulationServer.getLocalMapSize();
        for (int i = 0; i < eachMapSize ; i++) {
            for (int j = 0; j < eachMapSize; j++) {
                if(blocks == null) {
                    if (this.terrainGrid[i + startX][j + startY] == null) {
                        this.terrainGrid[i + startX][j + startY] = new Tile(null, null, i + startX, j + startY, this.panelWidth / widthDimension);
                    } else {
                        this.terrainGrid[i + startX][j + startY].terrainColor = null;
                        this.terrainGrid[i + startX][j + startY].animalColor = null;
                    }
                    continue;
                }
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
                int finalI = i + startX;
                int finalJ = j + startY;
                LOG.info("StartX: " + startX + " | StartY: " +  startY);
                LOG.info("i index is: " + i + " | j index is: " + j + " and adding to terraint: " +finalI + ", "+finalJ);
                if (this.terrainGrid[i + startX][j + startY] == null) {
                    this.terrainGrid[i + startX][j + startY] = new Tile(terrainColor, animalColor, i + startX, j + startY, this.panelWidth / widthDimension);
                } else {
                    this.terrainGrid[i + startX][j + startY].terrainColor = terrainColor;
                    this.terrainGrid[i + startX][j + startY].animalColor = animalColor;
                }
            }
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
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
