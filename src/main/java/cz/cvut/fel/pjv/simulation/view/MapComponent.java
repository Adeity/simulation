package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.*;
import javax.swing.*;

/**
 * Graphical representation of map of simulation on client side.
 */
public class MapComponent extends JComponent {
    public static final int PREFERRED_GRID_SIZE_PIXELS = 30;

    private static final Logger LOG = Logger.getLogger(MapComponent.class.getName());

    public static final Color GRASS = new Color(124,252,0);
    public static final Color BUSH = new Color(107,142,35);
    public static final Color WATER = new Color(0,191,255);
    public static final Color GRASS_WITH_GRAIN = new Color(255,255,153);

    public static final Color FOX = new Color(255,140,0);
    public static final Color HARE = new Color(200,200,200);

    public int dimension;


    private Simulation simulation;

    private Tile[][] terrainGrid;

    public MapComponent(Simulation simulation, int panelWidth) {
        this.simulation = simulation;
        this.dimension = this.simulation.map.getSizeOfMap();
        this.terrainGrid = new Tile[dimension][dimension];
        Block[][] blocks = this.simulation.map.getBlocks();
        Color terrainColor;
        Color animalColor;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                terrainColor = null;
                animalColor = null;
                if(blocks[i][j].getTerrain() == Block.Terrain.GRASS) {
                    terrainColor = GRASS;
                }
                else if (blocks[i][j].getTerrain() == Block.Terrain.BUSH) {
                    terrainColor = BUSH;
                }
                else if (blocks[i][j].getTerrain() == Block.Terrain.WATER) {
                    terrainColor = WATER;
                }
                else if (blocks[i][j].getTerrain() == Block.Terrain.GRASS_WITH_GRAIN) {
                    terrainColor = GRASS_WITH_GRAIN;
                }
                if(blocks[i][j].getAnimal() == null) {
                    animalColor = null;
                }
                else if(blocks[i][j].getAnimal() instanceof Fox) {
                    animalColor = FOX;
                }
                else if(blocks[i][j].getAnimal() instanceof Hare) {
                    animalColor = HARE;
                }
                this.terrainGrid[i][j] = new Tile(terrainColor, animalColor, i, j, panelWidth/dimension);
            }
        }
        setPreferredSize(new Dimension(panelWidth, panelWidth));

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        LOG.setUseParentHandlers(false);
        Handler stdout = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOG.addHandler(stdout);
        LOG.setLevel(Level.ALL);
        stdout.setLevel(Level.ALL);

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                terrainGrid[i][j].paintComponent(g);
            }
        }
    }

    @Override
    public void repaint() {
        this.repaintTiles();
    }

    private void repaintTiles() {
        this.updateTiles();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                terrainGrid[i][j].repaint();
            }
        }
    }

    private void updateTiles() {
        Block[][] blocks = simulation.map.getBlocks();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                Color terrainColor = null;
                Color animalColor = null;
                if(blocks[i][j].getTerrain() == Block.Terrain.GRASS) {
                    terrainColor = GRASS;
                }
                else if (blocks[i][j].getTerrain() == Block.Terrain.BUSH) {
                    terrainColor = BUSH;
                }
                else if (blocks[i][j].getTerrain() == Block.Terrain.WATER) {
                    terrainColor = WATER;
                }
                else if (blocks[i][j].getTerrain() == Block.Terrain.GRASS_WITH_GRAIN) {
                    terrainColor = GRASS_WITH_GRAIN;
                }
                if(blocks[i][j].getAnimal() == null) {
                    animalColor = null;
                }
                else if(blocks[i][j].getAnimal() instanceof Fox) {
                    animalColor = FOX;
                }
                else if(blocks[i][j].getAnimal() instanceof Hare) {
                    animalColor = HARE;
                }
                this.terrainGrid[i][j].terrainColor = terrainColor;
                this.terrainGrid[i][j].animalColor = animalColor;
//                        = new Tile(terrainColor, animalColor, i, j);
            }
        }
    }

    private class MouseHandler implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            LOG.fine(e.paramString());
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}