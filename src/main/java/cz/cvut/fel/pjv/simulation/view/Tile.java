package cz.cvut.fel.pjv.simulation.view;

import javax.swing.*;
import java.awt.*;

public class Tile extends JComponent {
    public Color terrainColor;
    public Color animalColor;

    public int x;
    public int y;

    private int tileWidth;



    public Tile(Color terrainColor, Color animalColor, int x, int y, int tileWidth){
        this.terrainColor = terrainColor;
        this.animalColor = animalColor;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        setPreferredSize(new Dimension(tileWidth, tileWidth));
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        int dimension = 100;

        int x = this.y * tileWidth;
        int y = this.x * tileWidth;

        //  paint terrain
        g.setColor(terrainColor);
        g.fillRect(x, y, tileWidth, tileWidth);

        //  paint animal
        g.setColor(animalColor);
        g.fillOval(x, y, tileWidth, tileWidth);

        //  paint square
        g.setColor(new Color(128, 128, 128));
        g.drawRect(x, y, tileWidth, tileWidth);

    }


}

