package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.Simulation;

import javax.swing.*;
import java.awt.*;

/**
 * Stats of simulation can be seen here
 */
public class JFrameStats extends JFrame {

    JLabel day = new JLabel();
    JLabel sizeOfMap = new JLabel();
    JLabel numOfGrassBlocks = new JLabel();
    JLabel numOfBushBlocks = new JLabel();
    JLabel numOfWaterBlocks = new JLabel();
    JLabel numOfAnimals = new JLabel();
    JLabel numOfFoxes = new JLabel();
    JLabel numOfHare = new JLabel();

    Simulation simulation;

    public JFrameStats(Simulation simulation) {
        this.simulation = simulation;
        this.setTitle("Stats");


        Box verticalBox = Box.createVerticalBox();

        updateLabels();
//        numOfGrassBlocks = new JLabel("")
        verticalBox.add(day);
        verticalBox.add(sizeOfMap);
        verticalBox.add(numOfGrassBlocks);
        verticalBox.add(numOfBushBlocks);
        verticalBox.add(numOfWaterBlocks);
        verticalBox.add(numOfAnimals);
        verticalBox.add(numOfFoxes);
        verticalBox.add(numOfHare);

        this.add(verticalBox);
        this.setSize(new Dimension(200, 500));
        this.setVisible(true);
    }

    public void updateLabels() {
        this.day.setText("Day: " + this.simulation.day);
        this.sizeOfMap.setText("Size of map: " + this.simulation.map.getSizeOfMap() +"x"+ this.simulation.map.getSizeOfMap());
        this.numOfGrassBlocks.setText("Num of grass blocks: " + this.simulation.map.getNumOfGBlocks());
        this.numOfBushBlocks.setText("Num of bush blocks: " + this.simulation.map.getNumOfBBlocks());
        this.numOfWaterBlocks.setText("Num of water blocks: " + this.simulation.map.getNumOfWBlocks());
        this.numOfAnimals.setText("Num of animals: " + this.simulation.map.getNumOfAnimals());
        this.numOfFoxes.setText("Num of foxes: " + this.simulation.map.getNumOfFoxes());
        this.numOfHare.setText("Num of hare: " + this.simulation.map.getNumOfHare());
    }
}
