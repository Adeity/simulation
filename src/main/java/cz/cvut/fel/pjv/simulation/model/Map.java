package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Map representation of simulation. This carries information about size of map, number of animals on the map and each block. Map is always square.
 */
public class Map implements Serializable{
    private static final Logger LOG = Logger.getLogger(Map.class.getName());
    private Block[][] blocks;
    private List<Animal> animals = new ArrayList<>();
    private List<Animal> animalQueue = new ArrayList<>();
    private int sizeOfMap;
    private int numOfFoxes = 0;
    private int numOfHare = 0;
    private int numOfAnimals = 0;
    //  G block = Grass block
    private int numOfGBlocks = 0;
    //  B block = Bush block
    private int numOfBBlocks = 0;
    //  W block = Water block
    private int numOfWBlocks = 0;
    //  R lock = Grass with grain block
    private int numOfRBlocks = 0;

    private Simulation simulation;



    /**
     * the map has a square shape.
     * @param size is dimension, map is then Dim(size x size)
     */
    public Map(int size, Simulation simulation) {
        this.setSimulation(simulation);
//        initMap(size);
    }

    /**
     * constructor with default dimension x dimension of map
     */
    public Map(Simulation simulation) {
        this.setSimulation(simulation);
        initMap(10);
    }

    /**
     * create simulation map based on maptemplate.txt template
     * @param filename is text file, which contains predefined map in appropriate format
     */
    public Map(String filename, Simulation simulation) {
        this.setSimulation(simulation);
        LOG.fine(CONF.MAP_TEMPLATE_DIRECTORY + CONF.fS + filename);
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
                        this.setSizeOfMap(Integer.parseInt(metaData[1]));
                    }
                }
                catch (NumberFormatException e) {
                    LOG.fine("Template is invalid!");
                }
                if(line.equals("----------")) {
                    break;
                }
                else if(line.isEmpty()) {
                    break;
                }
            }

            this.setBlocks(new Block[this.getSizeOfMap()][this.getSizeOfMap()]);

            //  read and init map
            for (int i = 0; i < this.getSizeOfMap(); i++) {
                line = br.readLine();
                String[] blocks = line.split(" ");
                if(blocks.length != this.getSizeOfMap()) {
                    LOG.fine("Wrong amount of blocks in map template.");
                    return;
                }
                for (int k = 0; k < blocks.length; k++) {
                    Block.Terrain t;
                    Animal a;
                    switch (Character.toString(blocks[k].charAt(0))) {
                        case "G":
                            t = Block.Terrain.GRASS;
                            setNumOfGBlocks(getNumOfGBlocks() + 1);
                            break;
                        case "B":
                            t = Block.Terrain.BUSH;
                            setNumOfBBlocks(getNumOfBBlocks() + 1);
                            break;
                        case "W":
                            t = Block.Terrain.WATER;
                            setNumOfWBlocks(getNumOfWBlocks() + 1);
                            break;
                        case "R":
                            t = Block.Terrain.GRASS_WITH_GRAIN;
                            setNumOfRBlocks(getNumOfRBlocks() + 1);
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
                    this.getBlocks()[i][k] = new Block(t, a, i, k);
                    if (a != null) {
                        if(a instanceof Hare) {
                            setNumOfHare(getNumOfHare() + 1);
                        }
                        else if (a instanceof Fox) {
                            setNumOfFoxes(getNumOfFoxes() + 1);
                        }
                        this.getAnimals().add(a);
                        setNumOfAnimals(getNumOfAnimals() + 1);
                        a.setBlock(this.getBlocks()[i][k]);
                    }
                }
            }
            br.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
            LOG.fine("map not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluate the state of map for the next day.
     * Evaluates each animal. It gets rid of dead animals and shuffles the list at the end.
     */
    public void evaluate() {
        LOG.fine("Starting evaluation of day. There are: " + this.getAnimals().size() + " animals.");
        LOG.setLevel(Level.INFO);
        for (int i = 0; i < getAnimals().size(); i++) {
            Animal a = getAnimals().get(i);
            LOG.finest("Checking animal: " + a);
            if (a.isDidEvaluate()) {
                LOG.finest(a + " already evaluated");
                continue;
            }
            else {
                LOG.finest(a + " evaluating");
                a.evaluate(this, getSimulation());
            }
            a.nextDayChangeStats();
            if (a.getEnergy() <= 0 && !a.isDead()) {
                a.setDead(true);
                this.deleteAnimalAtBlock(a.getBlock());
            }
        }

        this.getAnimals().addAll(getAnimalQueue());
        this.getAnimalQueue().clear();

        //  change stats of each animal
        //  change stats of map also
        for (Animal a : this.getAnimals()) {
            if (a.isDead()) {
                LOG.finest("Found dead animal in list: " + a.getClass().getSimpleName() + " at: (" + a.getBlock().getCoordX() + ", " + a.getBlock().getCoordY() + ")");
                if(a instanceof Fox) {
                    setNumOfFoxes(getNumOfFoxes() - 1);
                }
                else if (a instanceof Hare) {
                    setNumOfHare(getNumOfHare() - 1);
                }
                setNumOfAnimals(getNumOfAnimals() - 1);
                continue;
            }
            a.setDidEvaluate(false);
        }
        //  get rid of dead animals now that they have been accounted for
        this.getAnimals().removeIf(a -> a.isDead());
        LOG.fine("Done evaluating");

        Collections.shuffle(getAnimals());
    }

    /**
     * This method is used only when server asks to set block
     * @param x coord
     * @param y coord
     * @param newBlock
     * @return true if this method set the block as asked. false otherwise
     */
    public boolean setBlock(int x, int y, Block newBlock) {
        Block currentBlock = getBlocks()[x][y];

        if (newBlock.getAnimal() != null) {
            if (currentBlock.getAnimal() == null) {
                this.serverAskstoSetAnimalAtCoord(newBlock.getAnimal(), x, y);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if (currentBlock.getAnimal() != null) {
                if (currentBlock.getAnimal().isDidEvaluate() || currentBlock.getAnimal().isEvaluating()) {
                    return false;
                }
                else {
                    this.deleteAnimalAtBlock(currentBlock);
                    return true;
                }
            }
            else {
                return false;
            }
        }
    }

    /**
     * Add a new born animal on the map at block
     * @param a is newborn animal
     * @param block is block for newborn animal
     */
    public void addNewBornOnBlock(Animal a, Block block) {
        if (this.getBlocks()[block.getCoordX()][block.getCoordY()].getAnimal() != null) {
            return;
        }
        a.setAge(0);
        a.setBlock(this.getBlocks()[block.getCoordX()][block.getCoordY()]);
        this.getBlocks()[block.getCoordX()][block.getCoordY()].setAnimal(a);
    }


    private Block[] concatSurroundingBlocks (Block[] b1, Block[] b2) {
        return Stream.concat(Arrays.stream(b1), Arrays.stream(b2)).toArray(Block[]::new);
    }

    public Block findFreeBlockForMating (Animal a1, Animal a2) {
        Block[] a1Sb = getSimulation().getSurroundingBlocks(a1.getBlock());
        Block[] a2Sb = getSimulation().getSurroundingBlocks(a2.getBlock());
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
            return getBlocks()[coordX][coordY];
        }
        catch (ArrayIndexOutOfBoundsException e) {
//            LOG.fine(e.getMessage());
            return null;
        }
    }

    /**
     * first map gets filled with grass
     * then bushes are added
     * then lakes are added
     * @param size is size of one dimension, map ends up being Dim(size x size)
     */
    public void initMap(int size) {
        this.setSizeOfMap(size);
        this.setBlocks(new Block[size][size]);
        initMapFillWithGrass();
        initMapAddBushes();
        initMapAddLakes();
        initMapAddAnimals();
    }

    /**
     * fills whole map with grass
     */
    private void initMapFillWithGrass() {
        for (int i = 0; i < this.getSizeOfMap(); i++) {
            for (int k = 0; k < this.getSizeOfMap(); k++) {
                this.getBlocks()[i][k] = new Block(Block.Terrain.GRASS, i, k);
            }
        }
        this.setNumOfGBlocks(this.getNumOfGBlocks() + this.getSizeOfMap() * this.getSizeOfMap());
    }

    /**
     * adds squares of bushes periodically throughout map
     */
    private void initMapAddBushes() {
        int bushRow = 0;
        int bushCol = 0;
        int totalSize = this.getSizeOfMap();
        while (bushRow < totalSize) {
            while (bushCol < totalSize) {
                this.getBlocks()[bushRow][bushCol].setTerrain(Block.Terrain.BUSH);
                this.setNumOfGBlocks(this.getNumOfGBlocks() - 1);
                this.setNumOfBBlocks(this.getNumOfBBlocks() + 1);
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
        while (row < this.getSizeOfMap()) {
            col += 17;
            if (col / this.getSizeOfMap() > 1) {
                col %= getSizeOfMap();
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
                        this.getBlocks()[i][k].setTerrain(Block.Terrain.WATER);
                        this.setNumOfGBlocks(this.getNumOfGBlocks() - 1);
                        this.setNumOfWBlocks(this.getNumOfWBlocks() + 1);
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * every x rows iterates over a diagonal starting at that row and adds grain to grass if block is indeed grass
     */
    private void initMapAddAnimals() {
        int x = 7;
        int y = 3;
        int dimension = this.getSizeOfMap();
        boolean odd = true;
        for (int i = 0; i < dimension; i += x) {
            for (int k = 0; k <= i; k += y) {
                int j = i - k;
                if (this.getBlocks()[j][k].getTerrain() != Block.Terrain.WATER) {
                    if(odd) {
                        setAnimalAtCoord(new Fox(this.getBlocks()[j][k]) ,j, k);
                        odd = false;
                    }
                    else {
                        setAnimalAtCoord(new Hare(this.getBlocks()[j][k]) ,j, k);
                        odd = true;
                    }
                }
            }
        }
        for (int i = dimension - 2; i >= 0; i -= x) {
            for (int k = 0; k <= i; k += y) {
                int j = i - k;
                if (this.getBlocks()[dimension - k - 1][dimension - j - 1].getTerrain() != Block.Terrain.WATER) {
                    if(odd) {
                        setAnimalAtCoord(new Fox(this.getBlocks()[dimension - k - 1][dimension - j - 1]) ,dimension - k - 1, dimension - j - 1);
                        odd = false;
                    }
                    else {
                        setAnimalAtCoord(new Hare(this.getBlocks()[dimension - k - 1][dimension - j - 1]) ,dimension - k - 1, dimension - j - 1);
                        odd = true;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        String res = "";
        String part = "";
        for (int i = 0; i < this.getSizeOfMap(); i++) {
            for (int k = 0; k < this.getSizeOfMap(); k++) {
                part += " ";
                part += "|" + this.getBlocks()[i][k];
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
        Block block = getBlocks()[coordX][coordY];
        if (block.getAnimal() != null) {
            deleteAnimalAtBlock(this.getBlocks()[coordX][coordY]);
        }
        a.setBlock(block);
        block.setAnimal(a);

        if(a instanceof  Hare) {
            setNumOfHare(getNumOfHare() + 1);
        }
        else if(a instanceof Fox) {
            setNumOfFoxes(getNumOfFoxes() + 1);
        }
        LOG.fine("Adding animal: " + a.toString() + " to animals list");
        this.getAnimals().add(a);
        setNumOfAnimals(getNumOfAnimals() + 1);
    }

    /**
     * this method gets called when a request comes from server to set block. this method adds animal to coordinates desired by server
     * @param a is animal
     * @param coordX is coordinate of block
     * @param coordY is coordinate of block
     */
    public void serverAskstoSetAnimalAtCoord(Animal a, int coordX, int coordY) {
        Block block = getBlocks()[coordX][coordY];
        a.setBlock(block);
        block.setAnimal(a);

        a.setDidEvaluate(true);

        if (!this.getAnimals().contains(a)) { // this is very likely
            LOG.fine("Animal came from server, so I must put it to queue before adding it straigth to animals list.");
            getAnimalQueue().add(a);
        }
        if(a instanceof  Hare) {
            setNumOfHare(getNumOfHare() + 1);
        }
        else if(a instanceof Fox) {
            setNumOfFoxes(getNumOfFoxes() + 1);
        }
        setNumOfAnimals(getNumOfAnimals() + 1);
    }

    /**
     * this method deletes animal at block and changes stats of map
     * @param block where animal is to be deleted
     */
    public void deleteAnimalAtBlock(Block block) {
        if (block.getAnimal() != null) {
            this.getAnimals().remove(block.getAnimal());
            if(block.getAnimal() instanceof Hare) {
                setNumOfHare(getNumOfHare() - 1);
            }
            else if(block.getAnimal() instanceof Fox){
                setNumOfFoxes(getNumOfFoxes() - 1);
            }
            setNumOfAnimals(getNumOfAnimals() - 1);
            this.getBlocks()[block.getCoordX()][block.getCoordY()].setAnimal(null);
        }
        else {
            LOG.fine("There is no animal on this block");
        }
    }

    /**
     * Print stats of current state of map
     */
    public void printStats() {
        LOG.fine("Size of map: " + this.getSizeOfMap());
        LOG.fine("Num of grass blocks: " + getNumOfGBlocks() + " | Num of grass with grains blocks: " + getNumOfRBlocks() + " | Num of grass with water blocks: " + getNumOfWBlocks() + " | Num of bush blocks: " + getNumOfBBlocks());
        LOG.fine("Number of animals in animals list: " + this.getAnimals().size());
        LOG.fine("Animals in animals list: ");
        for (Animal a : this.getAnimals()) {
            LOG.fine(a.toString());
        }
        LOG.fine("Num of foxes: " + getNumOfFoxes());
        LOG.fine("Num of hare: " + getNumOfHare());

    }

    /**
     * @return String something like "|        |         |" + "\n"
     */
    private String cmdOneRowOfEmpty() {
        String res = "";
        for (int j = 0; j < getSizeOfMap(); j ++) {
            res += "|";
            res+=String.join("", Collections.nCopies( 3, " "));
        }
        return res + "|\n";
    }

    /**
     * @return String something like "----------" + "\n"
     */
    private String cmdOneRowOfStraigthLines() {
        return String.join("", Collections.nCopies( 4 * getSizeOfMap(), ("_"))) + "\n";
    }


    /**
     * Two dimensional array of blocks each representing one block where different terrain and animals could be. First dimension of this array is sizeOfMap big and each second dimension is also sizeOfMap big. Map is always square.
     */
    public Block[][] getBlocks() {
        return blocks;
    }

    /**
     * Two dimensional array of blocks each representing one block where different terrain and animals could be. First dimension of this array is sizeOfMap big and each second dimension is also sizeOfMap big. Map is always square.
     */
    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

    /**
     * List of animals currently on the map.
     */
    public List<Animal> getAnimals() {
        return animals;
    }

    /**
     * List of animals currently on the map.
     */
    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    /**
     * List of animals that came from distributed simulation from another client. These animals are to be added to animals list after evaluation of a day is finished.
     */
    public List<Animal> getAnimalQueue() {
        return animalQueue;
    }

    /**
     * If size of map is 10, then number of blocks on map is 10*10 = 100.
     */
    public int getSizeOfMap() {
        return sizeOfMap;
    }

    /**
     * If size of map is 10, then number of blocks on map is 10*10 = 100.
     */
    public void setSizeOfMap(int sizeOfMap) {
        this.sizeOfMap = sizeOfMap;
    }

    /**
     * Number of foxes currently on map.
     */
    public int getNumOfFoxes() {
        return numOfFoxes;
    }

    /**
     * Number of foxes currently on map.
     */
    public void setNumOfFoxes(int numOfFoxes) {
        this.numOfFoxes = numOfFoxes;
    }

    /**
     * Number of hare currently on map.
     */
    public int getNumOfHare() {
        return numOfHare;
    }

    /**
     * Number of hare currently on map.
     */
    public void setNumOfHare(int numOfHare) {
        this.numOfHare = numOfHare;
    }

    /**
     * Number of animals currently on map
     */
    public int getNumOfAnimals() {
        return numOfAnimals;
    }

    /**
     * Number of animals currently on map
     */
    public void setNumOfAnimals(int numOfAnimals) {
        this.numOfAnimals = numOfAnimals;
    }

    /**
     * Number of grass blocks currently on map
     */
    public int getNumOfGBlocks() {
        return numOfGBlocks;
    }

    /**
     * Number of grass blocks currently on map
     */
    public void setNumOfGBlocks(int numOfGBlocks) {
        this.numOfGBlocks = numOfGBlocks;
    }

    /**
     * Number of bush blocks currently on map
     */
    public int getNumOfBBlocks() {
        return numOfBBlocks;
    }

    /**
     * Number of bush blocks currently on map
     */
    public void setNumOfBBlocks(int numOfBBlocks) {
        this.numOfBBlocks = numOfBBlocks;
    }

    /**
     * Number of water blocks currently on map
     */
    public int getNumOfWBlocks() {
        return numOfWBlocks;
    }

    /**
     * Number of water blocks currently on map
     */
    public void setNumOfWBlocks(int numOfWBlocks) {
        this.numOfWBlocks = numOfWBlocks;
    }

    /**
     * Number of grass with grain blocks currently on map
     */
    public int getNumOfRBlocks() {
        return numOfRBlocks;
    }

    /**
     * Number of grass with grain blocks currently on map
     */
    public void setNumOfRBlocks(int numOfRBlocks) {
        this.numOfRBlocks = numOfRBlocks;
    }

    /**
     * Simulation this map is representing
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * Simulation this map is representing
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
}
