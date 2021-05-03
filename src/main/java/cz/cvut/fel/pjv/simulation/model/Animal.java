package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public abstract class Animal implements Serializable {
    private static final Logger LOG = Logger.getLogger(Animal.class.getName());
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        private static final List<Direction> DIRECTIONS =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = DIRECTIONS.size();
        private static final Random RANDOM = new Random();

        public static Direction randomDirection()  {
            return DIRECTIONS.get(RANDOM.nextInt(SIZE));
        }
    };

    public int energy;
    public int energyForMating;
    public int age;
    public boolean isDead;
    public boolean didEvaluate;
    public Block block;
    public Direction direction;

    /**
     * If animal looks around and finds another animal to interact with, it does so.
     * Otherwise the animal moves.
     * @param map map of simulation
     */
    public void evaluate(Map map, Simulation simulation) {
        System.out.println("Evaluating now: " + this.toString());
        Block[] surroundingBlocks = simulation.getSurroundingBlocks(this.block);

        for (Block block : surroundingBlocks) {
            if (block == null) {
                continue;
            }
            Animal otherAnimal = block.animal;

            if (otherAnimal == null) {
                continue;
            }
            if (otherAnimal.didEvaluate) {
                continue;
            }
            if(this.interact(simulation, otherAnimal)) {
                System.out.println(this + " interact with " + otherAnimal);
                this.didEvaluate = true;
                otherAnimal.didEvaluate = true;
                break;
            }
        }
        if(!this.didEvaluate) {
            System.out.println("Animals moves");
            move(simulation);
            this.didEvaluate = true;
        }
    }

    /**
     * Animals moves across the map
     */
    protected void move(Simulation simulation) {
        int changeDirectionCounter = 3;
        Block block = null;
        if (this.getDirection() == Direction.RIGHT) {
            block = simulation.getBlock(this.block.coordX, this.block.coordY + 1);
            actualMove(simulation, block);
        }
        else if (this.getDirection() == Direction.DOWN) {
            block = simulation.getBlock(this.block.coordX + 1, this.block.coordY);
            actualMove(simulation, block);
        }
        else if (this.getDirection() == Direction.LEFT) {
            block = simulation.getBlock(this.block.coordX, this.block.coordY - 1);
            actualMove(simulation, block);
        }
        else if (this.getDirection() == Direction.UP) {
            block = simulation.getBlock(this.block.coordX - 1, this.block.coordY);
            actualMove(simulation, block);
        }
    }

    protected void actualMove(Simulation simulation, Block block) {
        if (canMoveToBlock(block)) {
            if (simulation.isOnMyMap(block.coordX, block.coordY)) {
                simulation.map.deleteAnimalAtBlock(this.block);
                simulation.map.setAnimalAtCoord(this, block.coordX, block.coordY);
            }
            else {
                if (simulation.simulationClient != null) {
                    Block blockCopy = null;
                    try {
                        blockCopy = (Block) block.clone();
                        blockCopy.setAnimal(this);
                        if (simulation.simulationClient.setBlock(blockCopy.coordX, blockCopy.coordY, blockCopy)) {
                            this.block.setAnimal(null);
                        }
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    changeDirection();
                }
            }
        }
        else {
            changeDirection();
        }
    }

    protected void changeDirection() {
        if (this.getDirection() == Direction.RIGHT) {
            setDirection(Direction.DOWN);
        }
        else if (this.getDirection() == Direction.DOWN) {
            setDirection(Direction.LEFT);
        }
        else if (this.getDirection() == Direction.LEFT) {
            setDirection(Direction.UP);
        }
        else if (this.getDirection() == Direction.UP) {
            setDirection(Direction.RIGHT);
        }
    }

    private boolean canMoveToBlock(Block block) {
        return block != null && block.getTerrain() != Block.Terrain.WATER && block.getAnimal() == null;
    }

    /**
     * Animal dies.
     * Its parameters get set to negative values. And it gets deleted from map
     * @param simulation simulation
     */
    protected boolean die(Simulation simulation) {
        this.isDead = true;
        this.energy = -10;
        this.age = -10;
        return simulation.deleteAnimalAtBlock(this.block);
    }

    /**
     * Does the animal meet the requirements to be able to kill another animal
     * @return true if the animal meets the requirements to kill another animal
     */
    protected abstract boolean isReadyToKill();

    /**
     * Do both animals meet the requirements for mating
     * @param otherAnimal is animal to mate with
     * @return true if both animals meet the requirements for mating
     */
    protected abstract boolean areReadyForMating (Animal otherAnimal);

    /**
     * Two animals mate with each other
     * @param simulation simulation
     * @param otherAnimal is animal animal is mating with
     * @return true if there is a newborn animal on the map, false if there isn't
     */
    protected abstract boolean mate(Simulation simulation, Animal otherAnimal);

    /**
     * Create an animal with parameters of a newborn.
     * @param blockForNewBorn is block on which newborn is born
     * @return
     */
    protected abstract Animal createNewBorn(Block blockForNewBorn);

    /**
     * One letter code of animal
     * @return code of animal
     */
    protected abstract String animalCode();

    /**
     * Animal interacts with another animal on the map
     * @param simulation
     * @param otherAnimal is animal animal is interacting with
     * @return true if animals were able to interact with each other, false otherwise
     */
    protected abstract boolean interact(Simulation simulation, Animal otherAnimal);

    /**
     * After each day set new attribute values for an animal.
     */
    protected abstract void nextDayChangeStats();

    /**
     * Change stats of animals after mating.
     * @param otherAnimal the animal animal mated with.
     */
    protected abstract void mateChangeStats(Animal otherAnimal);

//    protected void moveDown(Map map, int movesPerRound) {
//    }
//    protected void moveUp(Map map) {
//    }
//    protected void moveLeft(Map map) {
//    }
//    protected void moveRight(Map map) {
//    }

    protected boolean foxSeesHare(Animal fox, Animal hare) {
        if (
                !(fox instanceof Fox)
                        ||
                        !(hare instanceof Hare)
        ) {
            LOG.severe("Wrong animal instances in method argument!");
            return false;
        }

        Block.Terrain foxBlockTerrain = fox.block.getTerrain();
        Block.Terrain hareBlockTerrain = hare.block.getTerrain();

        if (foxBlockTerrain == hareBlockTerrain) {
            return true;
        }
        else if (
                foxBlockTerrain == Block.Terrain.BUSH
                &&
                        hareBlockTerrain == Block.Terrain.GRASS
        ) {
            return true;
        }
        else if (
                foxBlockTerrain == Block.Terrain.GRASS
                &&
                        hareBlockTerrain == Block.Terrain.BUSH
        ){
            return false;
        }
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Animal animal = (Animal) o;

        if (energy != animal.energy) return false;
        if (age != animal.age) return false;
        if (isDead != animal.isDead) return false;
        if (didEvaluate != animal.didEvaluate) return false;
        return block.equals(animal.block);
    }

    @Override
    public int hashCode() {
        int result = energy;
        result = 31 * result + age;
        result = 31 * result + (isDead ? 1 : 0);
        result = 31 * result + (didEvaluate ? 1 : 0);
        result = 31 * result + block.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String res = "";
        String isDead = (this.isDead ? "Dead" : "Alive");
        String didEvaluate = (this.didEvaluate ? "Evaluated" : "notEvaluated");
        String block;
        try {
            block = " x,y:("+this.block.coordX+", "+this.block.coordY + ") ";
        }
        catch (NullPointerException e) {
            block = " nullBlock ";
        }
        res += getClass().getSimpleName() + block + isDead + " age: " + age + " " +didEvaluate;
        return res;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergyForMating() {
        return energyForMating;
    }

    public void setEnergyForMating(int energyForMating) {
        this.energyForMating = energyForMating;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isDidEvaluate() {
        return didEvaluate;
    }

    public void setDidEvaluate(boolean didEvaluate) {
        this.didEvaluate = didEvaluate;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
