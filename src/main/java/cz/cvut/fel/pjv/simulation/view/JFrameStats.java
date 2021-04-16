package cz.cvut.fel.pjv.simulation.view;

import cz.cvut.fel.pjv.simulation.model.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JFrameStats extends JFrame implements ActionListener {

    JLabel sizeOfMap = new JLabel();
    JLabel numOfGrassBlocks = new JLabel();
    JLabel numOfBushBlocks = new JLabel();
    JLabel numOfWaterBlocks = new JLabel();
    JLabel numOfAnimals = new JLabel();
    JLabel numOfFoxes = new JLabel();
    JLabel numOfHare = new JLabel();

    Map map;

    public JFrameStats(Map map) {
        this.map = map;


        Box verticalBox = Box.createVerticalBox();

        updateLabels();
//        numOfGrassBlocks = new JLabel("")
        verticalBox.add(sizeOfMap);
        verticalBox.add(numOfGrassBlocks);
        verticalBox.add(numOfBushBlocks);
        verticalBox.add(numOfWaterBlocks);
        verticalBox.add(numOfAnimals);
        verticalBox.add(numOfFoxes);
        verticalBox.add(numOfHare);

        this.add(verticalBox);
        this.setSize(new Dimension(200, 400));
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateLabels();
    }

    public void updateLabels() {
        this.sizeOfMap.setText("Size of map: " + map.sizeOfMap);
        this.numOfGrassBlocks.setText("Num of grass blocks: " + map.numOfGBlocks);
        this.numOfBushBlocks.setText("Num of bush blocks: " + map.numOfBBlocks);
        this.numOfWaterBlocks.setText("Num of water blocks: " + map.numOfWBlocks);
        this.numOfAnimals.setText("Num of animals: " + map.numOfAnimals);
        this.numOfFoxes.setText("Num of foxes: " + map.numOfFoxes);
        this.numOfHare.setText("Num of hare: " + map.numOfHare);
    }
}
