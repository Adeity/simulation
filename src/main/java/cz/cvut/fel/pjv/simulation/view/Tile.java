package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.model.Block;

import javax.swing.*;
import java.awt.*;

public class Tile extends JComponent {
    public Color terrainColor;
    public Color animalColor;

    public int x;
    public int y;

    private int squareSideLen = 20;



    public Tile(Color terrainColor, Color animalColor, int x, int y){
        this.terrainColor = terrainColor;
        this.animalColor = animalColor;
        this.x = x;
        this.y = y;
        setPreferredSize(new Dimension(squareSideLen, squareSideLen));
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        int dimension = 100;

        int x = this.y * squareSideLen;
        int y = this.x * squareSideLen;

        //  paint terrain
        g.setColor(terrainColor);
        g.fillRect(x, y, squareSideLen, squareSideLen);

        //  paint animal
        g.setColor(animalColor);
        g.fillOval(x, y, squareSideLen, squareSideLen);

        //  paint square
        g.setColor(new Color(128, 128, 128));
        g.drawRect(x, y, squareSideLen, squareSideLen);

    }


}

