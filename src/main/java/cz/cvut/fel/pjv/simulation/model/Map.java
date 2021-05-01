package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.utils.Utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Map implements Serializable{
    private static final Logger LOG = Logger.getLogger(Map.class.getName());
    public Block[][] blocks;
    public List<Animal> animals = new ArrayList<>();
    public int sizeOfMap;
    public int numOfFoxes = 0;
    public int numOfHare = 0;
    public int numOfAnimals = 0;
    //  G block = Grass block
    public int numOfGBlocks = 0;
    //  B block = Bush block
    public int numOfBBlocks = 0;
    //  W block = Water block
    public int numOfWBlocks = 0;
    //  R lock = Grass with grain block
    public int numOfRBlocks = 0;

    Simulation simulation;



    /**
     * the map has a square shape.
     * @param size is dimension, map is then Dim(size x size)
     */
    public Map(int size, Simulation simulation) {
        this.simulation = simulation;
        initMap(size);
    }

    /**
     * constructor with default dimension x dimension of map
     */
    public Map(Simulation simulation) {
        this.simulation = simulation;
        initMap(10);
    }

    /**
     * create simulation map based on maptemplate.txt template
     * @param filename is text file, which contains predefined map in appropriate format
     */
    public Map(String filename, Simulation simulation) {
        this.simulation = simulation;
        System.out.println(CONF.MAP_TEMPLATE_DIRECTORY + CONF.fS + filename);
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CONF.MAP_TEMPLATE_DIRECTORY + CONF.fS + filename), StandardCharsets.UTF_8));
                )
        {

            String line;
            //  read metadata
            while ((line = br.readLine()) != null) {
                String[] metaData = line.split(" ");
                try {
                    if(metaData[0].equals("size:")) {
                        this.sizeOfMap = Integer.parseInt(metaData[1]);
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("Template is invalid!");
                }
                if(line.equals("----------")) {
                    break;
                }
                else if(line.isEmpty()) {
                    break;
                }
            }

            this.blocks = new Block[this.sizeOfMap][this.sizeOfMap];

            //  read and init map
            for (int i = 0; i < this.sizeOfMap; i++) {
                line = br.readLine();
                String[] blocks = line.split(" ");
                if(blocks.length != this.sizeOfMap) {
                    System.out.println("Wrong amount of blocks in map template.");
                    return;
                }
                for (int k = 0; k < blocks.length; k++) {
                    Block.Terrain t;
                    Animal a;
                    switch (Character.toString(blocks[k].charAt(0))) {
                        case "G":
                            t = Block.Terrain.GRASS;
                            numOfGBlocks++;
                            break;
                        case "B":
                            t = Block.Terrain.BUSH;
                            numOfBBlocks++;
                            break;
                        case "W":
                            t = Block.Terrain.WATER;
                            numOfWBlocks++;
                            break;
                        case "R":
                            t = Block.Terrain.GRASS_WITH_GRAIN;
                            numOfRBlocks++;
                            break;
                        default:
                            t = null;
                            break;
                    }
                    if(t == Block.Terrain.WATER) {
                        a = null;
                    }
                    else {
                        switch (Character.toString(blocks[k].charAt(1))) {
                            case "N":
                                a = null;
                                break;
                            case "F":
                                a = new Fox();
                                break;
                            case "H":
                                a = new Hare();
                                break;
                            default:
                                a = null;
                                break;
                        }
                    }
                    this.blocks[i][k] = new Block(t, a, i, k);
                    if (a != null) {
                        if(a instanceof Hare) {
                            numOfHare++;
                        }
                        else if (a instanceof Fox) {
                            numOfFoxes++;
                        }
                        this.animals.add(a);
                        numOfAnimals++;
                        a.block = this.blocks[i][k];
                    }
                }
            }
            br.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("map not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluate the state of map for the next day.
     * Evaluates each animal. It gets rid of dead animals and shuffles the list at the end.
     */
    public void evaluate() {
        Utilities.addHandlerToLogger(LOG);
        LOG.setLevel(Level.INFO);
        for (int i = 0; i < animals.size(); i++) {
            Animal a = animals.get(i);
            LOG.finest("Checking animal: " + a);
            if (a.didEvaluate) {
                LOG.finest(a + " already evaluated");
                continue;
            }
            else {
                LOG.finest(a + " evaluating");
                a.evaluate(this, simulation);
            }
            a.nextDayChangeStats();
            if (a.energy <= 0 && !a.isDead) {
                a.isDead = true;
                this.deleteAnimalAtBlock(a.getBlock());
            }
        }
        //  change stats of each animal
        //  change stats of map also
        for (Animal a : this.animals) {
            if (a.isDead) {
                LOG.finest("Found dead animal in list: " + a.getClass().getSimpleName() + " at: (" + a.block.coordX + ", " + a.block.coordY + ")");
                if(a instanceof Fox) {
                    numOfFoxes--;
                }
                else if (a instanceof Hare) {
                    numOfHare--;
                }
                numOfAnimals--;
                continue;
            }
            a.didEvaluate = false;
        }
        //  get rid of dead animals now that they have been accounted for
        this.animals.removeIf(a -> a.isDead);

        Collections.shuffle(animals);
    }

    public boolean setBlock(int x, int y, Block newBlock) {
        Block currentBlock = blocks[x][y];

        if (currentBlock.getAnimal().didEvaluate) {
            return false;
        }
        else {
            blocks[x][y] = newBlock;
            return true;
        }
    }

    /**
     * Add a new born animal on the map at block
     * @param a is newborn animal
     * @param block is block for newborn animal
     */
    public void addNewBornOnBlock(Animal a, Block block) {
        if (this.blocks[block.coordX][block.coordY].animal != null) {
            return;
        }
        a.age = 0;
        a.block = this.blocks[block.coordX][block.coordY];
        this.blocks[block.coordX][block.coordY].setAnimal(a);
    }


    private Block[] concatSurroundingBlocks (Block[] b1, Block[] b2) {
        return Stream.concat(Arrays.stream(b1), Arrays.stream(b2)).toArray(Block[]::new);
    }

    public Block findFreeBlockForMating (Animal a1, Animal a2) {
        Block[] a1Sb = simulation.getSurroundingBlocks(a1.block);
        Block[] a2Sb = simulation.getSurroundingBlocks(a2.block);
        Block[] surroundingBlocks = concatSurroundingBlocks(a1Sb, a2Sb);
        for (Block b : surroundingBlocks) {
            if (b == null) {
                continue;
            }
            if (b.isBlockFree()) {
                return b;
            }
        }
        return null;
    }

    /**
     * gets specific block based on coordinates on map
     * @param coordX is row parameter
     * @param coordY is column parameter
     * @return block[coordX][coordY]
     */
    public Block getBlock (
            int coordX,
            int coordY
    ){
        try {
            return blocks[coordX][coordY];
        }
        catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println(e.getMessage());
            return null;
        }
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
        initMapAddGrain();
    }

    /**
     * fills whole map with grass
     */
    private void initMapFillWithGrass() {
        for (int i = 0; i < this.sizeOfMap; i++) {
            for (int k = 0; k < this.sizeOfMap; k++) {
                this.blocks[i][k] = new Block(Block.Terrain.GRASS, i, k);
            }
        }
        this.numOfGBlocks += this.sizeOfMap * this.sizeOfMap;
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
                this.numOfGBlocks--;
                this.numOfBBlocks++;
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
     * @param centerCol is column index of where center of circle is
     */
    private void addLake(int centerRow, int centerCol) {
        int r = 2;
        for (int i = centerRow - r; i <= centerRow + r; i++) {
            for (int k = centerCol - r; k <= centerCol + r; k++) {
                if((i - centerRow) * (i - centerRow) + (k - centerCol) * (k - centerCol) <= r*r) {
                    try {
                        this.blocks[i][k].setTerrain(Block.Terrain.WATER);
                        this.numOfGBlocks--;
                        this.numOfWBlocks++;
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * every three rows iterates over a diagonal starting at that row and adds grain to grass if block is indeed grass
     */
    private void initMapAddGrain() {
        int dimension = this.sizeOfMap;
        for (int i = 0; i < dimension; i += 3) {
            for (int k = 0; k <= i; k += 2) {
                int j = i - k;
                if (this.blocks[j][k].getTerrain() == Block.Terrain.GRASS) {
                    this.blocks[j][k].setTerrain(Block.Terrain.GRASS_WITH_GRAIN);
                    this.numOfGBlocks--;
                    this.numOfRBlocks++;
                }
            }
        }
        for (int i = dimension - 2; i >= 0; i -= 3) {
            for (int k = 0; k <= i; k += 2) {
                int j = i - k;
                if (this.blocks[dimension - k - 1][dimension - j - 1].getTerrain() == Block.Terrain.GRASS) {
                    this.blocks[dimension - k - 1][dimension - j - 1].setTerrain(Block.Terrain.GRASS_WITH_GRAIN);
                    this.numOfGBlocks--;
                    this.numOfRBlocks++;
                }
            }
        }
    }

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
            }
            res += "\n";
        }
        return res;
    }

    /**
     * Sets animal at coordinations of map
     * @param a is animal
     * @param coordX is x coordinate of map
     * @param coordY is y coordinate of map
     */
    public void setAnimalAtCoord (Animal a, int coordX, int coordY) {
        Block block = blocks[coordX][coordY];
        if (block.animal != null) {
            deleteAnimalAtBlock(this.blocks[coordX][coordY]);
        }
        a.block = block;
        block.animal = a;

        if(a instanceof  Hare) {
            numOfHare++;
        }
        else if(a instanceof Fox) {
            numOfFoxes++;
        }
        this.animals.add(a);
        numOfAnimals++;
    }

    public void deleteAnimalAtBlock(Block block) {
        if (block.animal != null) {
            this.animals.remove(block.animal);
            if(block.animal instanceof Hare) {
                numOfHare--;
            }
            else if(block.animal instanceof Fox){
                numOfFoxes--;
            }
            numOfAnimals--;
            this.blocks[block.coordX][block.coordY].animal = null;
        }
        else {
            System.out.println("There is no animal on this block");
        }
    }

    public void moveAnimal(Animal animal, Block block) {
        Block currentBlockOfAnimal = animal.block;
        currentBlockOfAnimal.animal = null;
        block.animal = animal;
        animal.block = block;
    }

    /**
     * Print stats of current state of map
     */
    public void printStats() {
        System.out.println("Size of map: " + this.sizeOfMap);
        System.out.println("Num of grass blocks: " + numOfGBlocks + " | Num of grass with grains blocks: " + numOfRBlocks + " | Num of grass with water blocks: " + numOfWBlocks + " | Num of bush blocks: " + numOfBBlocks);
        System.out.println("Number of animals in animals list: " + this.animals.size());
        System.out.println("Animals in animals list: ");
        for (Animal a : this.animals) {
            System.out.println(a);
        }
        System.out.println("Num of foxes: " + numOfFoxes);
        System.out.println("Num of hare: " + numOfHare);

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
