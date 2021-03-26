package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Map {;
    public Block[][] blocks;
    public int sizeOfMap;

    /**
     * the map has a square shape. this means, when
     * @param size is 10, map ends up beign 10x10
     */
    public Map(int size) {
        initMap(size);
    }

    public Map() {
        initMap(10);
    }

    /**
     * on initialization of the map I fill it with grass with no animals
     * @param size
     */
    private void initMap(int size) {
        this.sizeOfMap = size;
        this.blocks = new Block[size][size];
        initMapFillWithGrass();
        initMapAddBushes();
    }

    private void initMapFillWithGrass() {
        for (int i = 0; i < this.sizeOfMap; i++) {
            for (int k = 0; k < this.sizeOfMap; k++) {
                this.blocks[i][k] = new Block(Block.Terrain.GRASS);
            }
        }
    }

    private void initMapAddBushes() {
        int bushRow = 0;
        int bushCol = 0;
        int totalSize = this.sizeOfMap;
        while (bushRow < totalSize) {
            while (bushCol < totalSize) {
                this.blocks[bushRow][bushCol].setTerrain(Block.Terrain.BUSH);
                if (bushCol % 9 == 2) {
                    bushCol += 7;
                }
                else {
                    bushCol++;
                }
            }
            bushCol = 0;
            if (bushRow % 9 == 2) {
                bushRow += 7;
            }
            else {
                bushRow++;
            }
        }
    }

    private void initMapAddLakes() {

    }


    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void fillRandomly(Entity fox, int numOfFoxes, Entity hare, int numOfHares, Entity hunter, int numOfHunters) {

    }

    public void fillRandomly() {

    }

    public void animalAtCoordDies(int row, int col) {

    }

    public void animalDies(Animal animal) {

    }


//    @Override
//    public String toString() {
//        String res = "";
//        String part = "";
//        for (int i = 0; i < this.sizeOfMap; i++) {
//            for (int k = 0; k < this.sizeOfMap; k++) {
//                part += "|" + this.blocks[i][k];
//                res += part;
//                if(part.length() < 20) {
//                    res += String.join("", Collections.nCopies( 20 - part.length(), " "));
//                }
//                part = "";
//            }
//            res += "|\n";
//        }
//        return res;
//    }

    @Override
    public String toString() {
        String res = "";
        String part = "";
        for (int i = 0; i < this.sizeOfMap; i++) {
            res += cmdOneRowOfStraigthLines();
            res += cmdOneRowOfEmpty();
            res += cmdOneRowOfEmpty();
            for (int k = 0; k < this.sizeOfMap; k++) {
                part += "|" + this.blocks[i][k];
                res += part;
                if(part.length() < 20) {
                    res += String.join("", Collections.nCopies(20 - part.length(), " "));
                }
                part = "";
                if (k == this.sizeOfMap - 1) {
                    res += "|";
                }
            }
            res += "\n";
            res += cmdOneRowOfEmpty();
            res += cmdOneRowOfEmpty();
            if (i == this.sizeOfMap - 1) {
                res += cmdOneRowOfStraigthLines();
            }
        }
        return res;
    }

    /**
     * @return String something like "|        |         |" + "\n"
     */
    private String cmdOneRowOfEmpty() {
        String res = "";
        for (int j = 0; j < sizeOfMap; j ++) {
            res += "|";
            res+=String.join("", Collections.nCopies( 19, " "));
        }
        return res + "|\n";
    }

    /**
     * @return String something like "----------" + "\n"
     */
    private String cmdOneRowOfStraigthLines() {
        return String.join("", Collections.nCopies( 20 * sizeOfMap, ("_"))) + "\n";
    }
}
