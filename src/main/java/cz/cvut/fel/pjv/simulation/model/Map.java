package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.Entity;

import java.util.Collections;

public class Map {;
    public Block[][] blocks;
    public int sizeOfMap;

    /**
     * the map has a square shape.
     * @param size is dimension, map is then Dim(size x size)
     */
    public Map(int size) {
        initMap(size);
    }

    /**
     * constructor with default dimension x dimension of map
     */
    public Map() {
        initMap(10);
    }

    /**
     * first map gets filled with grass
     * then bushes are added
     * then lakes are added
     * @param size is size of one dimension, map ends up being Dim(size x size)
     */
    private void initMap(int size) {
        this.sizeOfMap = size;
        this.blocks = new Block[size][size];
        initMapFillWithGrass();
        initMapAddBushes();
        initMapAddLakes();
    }

    /**
     * fills whole map with grass
     */
    private void initMapFillWithGrass() {
        for (int i = 0; i < this.sizeOfMap; i++) {
            for (int k = 0; k < this.sizeOfMap; k++) {
                this.blocks[i][k] = new Block(Block.Terrain.GRASS);
            }
        }
    }

    /**
     * adds squares of bushes periodically throughout map
     */
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
            if (bushRow % 10 == 2) {
                bushRow += 8;
            }
            else {
                bushRow++;
            }
        }
    }

    /**
     * adds lakes of water to map periodically
     */
    private void initMapAddLakes() {
        int row = 6, col = 0;
        while (row < this.sizeOfMap) {
            col += 17;
            if (col / this.sizeOfMap > 1) {
                col %= sizeOfMap;
                row += 10;
            }
            addLake(row, col);
        }
    }

    /**
     * prints circular lake
     * using formula: ((x1 - start_X) * (x1 - start_X) + (y1 - start_Y) * (y1 - start_Y)) <= r * r
     * @param centerRow is row index of where center is of circle
     * @param centerCol is column idnex of where center of circle is
     */
    private void addLake(int centerRow, int centerCol) {
        int r = 2;
        for (int i = centerRow - r; i <= centerRow + r; i++) {
            for (int k = centerCol - r; k <= centerCol + r; k++) {
                if((i - centerRow) * (i - centerRow) + (k - centerCol) * (k - centerCol) <= r*r) {
                    try {
                        this.blocks[i][k].setTerrain(Block.Terrain.WATER);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }
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
            for (int k = 0; k < this.sizeOfMap; k++) {
                part += " ";
                part += "|" + this.blocks[i][k];
                part += "| ";
                res += part;
                part = "";
                if (k == this.sizeOfMap - 1) {
                    res += "|";
                }
            }
            res += "\n";
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
            res+=String.join("", Collections.nCopies( 3, " "));
        }
        return res + "|\n";
    }

    /**
     * @return String something like "----------" + "\n"
     */
    private String cmdOneRowOfStraigthLines() {
        return String.join("", Collections.nCopies( 4 * sizeOfMap, ("_"))) + "\n";
    }
}
