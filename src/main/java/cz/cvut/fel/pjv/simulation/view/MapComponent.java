package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.model.Block;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.model.Map;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.*;
import javax.swing.*;

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


    private Map map;

    private Tile[][] terrainGrid;

    public MapComponent(Map mapIn, int panelWidth) {
        this.map = mapIn;
        this.dimension = map.sizeOfMap;
        this.terrainGrid = new Tile[dimension][dimension];
        Color terrainColor;
        Color animalColor;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                terrainColor = null;
                animalColor = null;
                if(map.blocks[i][j].getTerrain() == Block.Terrain.GRASS) {
                    terrainColor = GRASS;
                }
                else if (map.blocks[i][j].getTerrain() == Block.Terrain.BUSH) {
                    terrainColor = BUSH;
                }
                else if (map.blocks[i][j].getTerrain() == Block.Terrain.WATER) {
                    terrainColor = WATER;
                }
                else if (map.blocks[i][j].getTerrain() == Block.Terrain.GRASS_WITH_GRAIN) {
                    terrainColor = GRASS_WITH_GRAIN;
                }
                if(map.blocks[i][j].getAnimal() == null) {
                    animalColor = null;
                }
                else if(map.blocks[i][j].getAnimal() instanceof Fox) {
                    animalColor = FOX;
                }
                else if(map.blocks[i][j].getAnimal() instanceof Hare) {
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
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                Color terrainColor = null;
                Color animalColor = null;
                if(map.blocks[i][j].getTerrain() == Block.Terrain.GRASS) {
                    terrainColor = GRASS;
                }
                else if (map.blocks[i][j].getTerrain() == Block.Terrain.BUSH) {
                    terrainColor = BUSH;
                }
                else if (map.blocks[i][j].getTerrain() == Block.Terrain.WATER) {
                    terrainColor = WATER;
                }
                else if (map.blocks[i][j].getTerrain() == Block.Terrain.GRASS_WITH_GRAIN) {
                    terrainColor = GRASS_WITH_GRAIN;
                }
                if(map.blocks[i][j].getAnimal() == null) {
                    animalColor = null;
                }
                else if(map.blocks[i][j].getAnimal() instanceof Fox) {
                    animalColor = FOX;
                }
                else if(map.blocks[i][j].getAnimal() instanceof Hare) {
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
            System.out.println(e.paramString());
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