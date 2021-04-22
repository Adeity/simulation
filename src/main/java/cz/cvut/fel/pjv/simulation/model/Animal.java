package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;

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
    public void evaluate(Map map) {
        Block[] surroundingBlocks = map.getSurroundingBlocks(this.block);

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
            if(this.interact(map, otherAnimal)) {
                this.didEvaluate = true;
                otherAnimal.didEvaluate = true;
                break;
            }
        }
        if(!this.didEvaluate) {
            //  TODO: implement moving
            move(map, CONF.MOVES_PER_ROUND);
            this.didEvaluate = true;
        }
        nextDayChangeStats();
    }

    /**
     * Animals moves across the map
     */
    protected void move(Map map, int movesPerRound) {

        while (movesPerRound > 0) {
            Block currentBlock = this.block;
            Block down;
            Block right;
            Block left;
            Block up;
            Block blockToMoveTo = null;
            try {
                down = map.blocks[this.block.coordX + 1][this.block.coordY];
            } catch (ArrayIndexOutOfBoundsException e) {
                down = null;
            }
            try {
                up = map.blocks[this.block.coordX - 1][this.block.coordY];
            } catch (ArrayIndexOutOfBoundsException e) {
                up = null;
            }
            try {
                right = map.blocks[this.block.coordX][this.block.coordY + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                right = null;
            }
            try {
                left = map.blocks[this.block.coordX][this.block.coordY - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                left = null;
            }

            if (this.direction == Direction.DOWN) {
            }
            else if (this.direction == Direction.UP) {
            }
            movesPerRound--;
        }
    }

    /**
     * Animal dies.
     * Its parameters get set to negative values. And it gets deleted from map
     * @param map map of simulation
     */
    protected void die(Map map) {
        this.isDead = true;
        this.energy = -10;
        this.age = -10;
        map.deleteAnimalAtBlock(this.block);
//        this.block = null;
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
     * @param map is map of simulation
     * @param otherAnimal is animal animal is mating with
     * @return true if there is a newborn animal on the map, false if there isn't
     */
    protected abstract boolean mate(Map map, Animal otherAnimal);

    /**
     * One letter code of animal
     * @return code of animal
     */
    protected abstract String animalCode();

    /**
     * Animal interacts with another animal on the map
     * @param map is map of simulation
     * @param otherAnimal is animal animal is interacting with
     * @return true if animals were able to interact with each other, false otherwise
     */
    protected abstract boolean interact(Map map, Animal otherAnimal);

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
}
