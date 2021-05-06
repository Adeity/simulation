package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.Simulation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Entity animal in simulation is an entity with attributes energy, energyForMating and age. Each animal gets evaluated each day in simulation.
 */
public abstract class Animal implements Serializable {
    private static final Logger LOG = Logger.getLogger(Animal.class.getName());


    /**
     * carries information about direction of animal
     */
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

    private int energy;
    private boolean evaluating;
    private int energyForMating;
    private int age;
    private boolean isDead;
    private boolean didEvaluate;
    private Block block;
    private Direction direction;

    /**
     * If animal looks around and finds another animal to interact with, it does so.
     * Otherwise the animal moves.
     * @param map map of simulation
     */
    public void evaluate(Map map, Simulation simulation) {
//        Utilities.addHandlerToLogger(LOG);
        this.setEvaluating(true);
        LOG.info("Evaluating now: " + this.toString());
        Block[] surroundingBlocks = simulation.getSurroundingBlocks(this.getBlock());

        for (Block block : surroundingBlocks) {
            if (block == null) {
                continue;
            }
            Animal otherAnimal = block.getAnimal();

            if (otherAnimal == null) {
                continue;
            }
            if (otherAnimal.isDidEvaluate()) {
                continue;
            }
            if(this.interact(simulation, otherAnimal)) {
                LOG.info(this + " interact with " + otherAnimal);
                this.setDidEvaluate(true);
                otherAnimal.setDidEvaluate(true);
                break;
            }
        }
        if(!this.isDidEvaluate()) {
            LOG.info("Animals moves");
            move(simulation);
            this.setDidEvaluate(true);
        }
        this.setEvaluating(false);
    }

    /**
     * Animals moves across the map
     */
    protected void move(Simulation simulation) {
        int changeDirectionCounter = 3;
        Block block = null;
        LOG.info("Animal " + this.toString() + " is looking to move");
        if (this.getDirection() == Direction.RIGHT) {
            block = simulation.getBlock(this.getBlock().getCoordX(), this.getBlock().getCoordY() + 1);
            LOG.info("Animal " + toString() + " tries to move right to block at " + this.getBlock().getCoordX() + ", " + (this.getBlock().getCoordY() + 1));
            if (block == null) {
                LOG.info("The block is however null");
            }
            else {
                LOG.info("The block is not null");
            }
            actualMove(simulation, block);
        }
        else if (this.getDirection() == Direction.DOWN) {
            block = simulation.getBlock(this.getBlock().getCoordX() + 1, this.getBlock().getCoordY());
            LOG.info("Animal " + toString() + " tries to move down to block at " + (this.getBlock().getCoordX() + 1) + ", " + this.getBlock().getCoordY());
            if (block == null) {
                LOG.info("The block is however null");
            }
            else {
                LOG.info("The block is not null");
            }
            actualMove(simulation, block);
        }
        else if (this.getDirection() == Direction.LEFT) {
            block = simulation.getBlock(this.getBlock().getCoordX(), this.getBlock().getCoordY() - 1);
            LOG.info("Animal " + toString() + " tries to move left to block at " + this.getBlock().getCoordX() + ", " + (this.getBlock().getCoordY() - 1));
            if (block == null) {
                LOG.info("The block is however null");
            }
            else {
                LOG.info("The block is not null");
            }
            actualMove(simulation, block);
        }
        else if (this.getDirection() == Direction.UP) {
            block = simulation.getBlock(this.getBlock().getCoordX() - 1, this.getBlock().getCoordY());
            LOG.info("Animal " + toString() + " tries to move up to block at " + (this.getBlock().getCoordX() - 1) + ", " + this.getBlock().getCoordY());
            if (block == null) {
                LOG.info("The block is however null");
            }
            else {
                LOG.info("The block is not null");
            }
            actualMove(simulation, block);
        }
    }

    /**
     *
     * this gets called by move method. checks wheter animal can move to desired block. Also checks wheter its on local map, if not,
     * checks if network connection is up. If it is, asks server to move across clients.
     * @param simulation is simulation
     * @param block is desired block animal wants to move to
     */
    protected void actualMove(Simulation simulation, Block block) {
        if (canMoveToBlock(block)) {
            LOG.info("Evaluated that animal " + this.toString() + " at block: " + block.getCoordX() + ", " + block.getCoordY() + " could move to block");
            if (simulation.isOnMyMap(block.getCoordX(), block.getCoordY())) {
                simulation.map.deleteAnimalAtBlock(this.getBlock());
                simulation.map.setAnimalAtCoord(this, block.getCoordX(), block.getCoordY());
            }
            else {
                if (simulation.simulationClient != null) {
                    Block blockCopy = null;
                    try {
                        blockCopy = (Block) block.clone();
                        blockCopy.setAnimal(this);
                        LOG.info("Animal " + this.toString() + " wants to move to block with coordinates: " + block.getCoordX() + ", " + block.getCoordY());
//                        LOG.info("Animal " + this.toString() + " wants to move to block with coordinates: " + block.coordX + ", " + block.coordY);
                        if (simulation.simulationClient.setBlock(blockCopy.getCoordX(), blockCopy.getCoordY(), blockCopy)) {
                            LOG.info("Animal moved to another map");
                            this.getBlock().setAnimal(null);
                            this.die(simulation);
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
            LOG.info("Evaluated that animal " + this.toString() + " could NOT move to desired block");
            changeDirection();
        }
    }

    /**
     * animal changes direciton. this gets called when he cant move in his original direction.
     */
    protected void changeDirection() {
        if (this.getDirection() == Direction.RIGHT) {
            LOG.info("Changing animals " + this.toString() + " direction, current direction: " + this.getDirection() + " next direction: " + Direction.DOWN);
            setDirection(Direction.DOWN);
            return;
        }
        else if (this.getDirection() == Direction.DOWN) {
            LOG.info("Changing animals " + this.toString() + " direction, current direction: " + this.getDirection() + " next direction: " + Direction.LEFT);
            setDirection(Direction.LEFT);
            return;
        }
        else if (this.getDirection() == Direction.LEFT) {
            LOG.info("Changing animals" + this.toString() + " direction, current direction: " + this.getDirection() + " next direction: " + Direction.UP);
            setDirection(Direction.UP);
            return;
        }
        else if (this.getDirection() == Direction.UP) {
            LOG.info("Changing animals " + this.toString() + " direction, current direction: " + this.getDirection() + " next direction: " + Direction.RIGHT);
            setDirection(Direction.RIGHT);
            return;
        }
        LOG.info("Changing animals " + this.toString() + " direction method where animal doesnt have direction");
    }

    /**
     * asserts wheter animal can move to desired block
     * @param block desired block
     * @return true if animal can move to desired block. false otherwise
     */
    private boolean canMoveToBlock(Block block) {
        LOG.info("Can move to block: " + block + "?");
        if (block != null) {
            LOG.info(block.toString());
        }
        return block != null && block.getTerrain() != Block.Terrain.WATER && block.getAnimal() == null;
    }

    /**
     * Animal dies.
     * Its parameters get set to negative values. And it gets deleted from map
     * @param simulation simulation
     */
    protected boolean die(Simulation simulation) {
        this.setDead(true);
        this.setEnergy(-10);
        this.setAge(-10);
        return simulation.deleteAnimalAtBlock(this.getBlock());
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

    /**
     * asserts wheter fox can see hare. fox cant see hare, if hare is in bushes and fox isnt.
     * @param fox is fox
     * @param hare is hare
     * @return true of fox can see hare
     */
    protected boolean foxSeesHare(Animal fox, Animal hare) {
        if (
                !(fox instanceof Fox)
                        ||
                        !(hare instanceof Hare)
        ) {
            LOG.severe("Wrong animal instances in method argument!");
            return false;
        }

        Block.Terrain foxBlockTerrain = fox.getBlock().getTerrain();
        Block.Terrain hareBlockTerrain = hare.getBlock().getTerrain();

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

        if (getEnergy() != animal.getEnergy()) return false;
        if (getAge() != animal.getAge()) return false;
        if (isDead() != animal.isDead()) return false;
        if (isDidEvaluate() != animal.isDidEvaluate()) return false;
        return getBlock().equals(animal.getBlock());
    }

    @Override
    public int hashCode() {
        int result = getEnergy();
        result = 31 * result + getAge();
        result = 31 * result + (isDead() ? 1 : 0);
        result = 31 * result + (isDidEvaluate() ? 1 : 0);
        result = 31 * result + getBlock().hashCode();
        return result;
    }

    @Override
    public String toString() {
        String res = "";
        String isDead = (this.isDead() ? "Dead" : "Alive");
        String didEvaluate = (this.isDidEvaluate() ? "Evaluated" : "notEvaluated");
        String block;
        try {
            block = " x,y:("+ this.getBlock().getCoordX() +", "+ this.getBlock().getCoordY() + ") ";
        }
        catch (NullPointerException e) {
            block = " nullBlock ";
        }
        res += getClass().getSimpleName() + block + isDead + " age: " + getAge() + " " +didEvaluate + " " + this.getDirection();
        return res;
    }

    /**
     * Animals need energy to live. Animal gets energy by eating. If animal reaches 0 energy, it dies.
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Animals need energy to live. Animal gets energy by eating. If animal reaches 0 energy, it dies. This value decrements daily.
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * Animal needs energy for mating. Based on set energy for mating value, animal can mate once every that value. This energy increments daily. This energy gets reset to 0 after mating.
     */
    public int getEnergyForMating() {
        return energyForMating;
    }

    /**
     * Animal needs energy for mating. Based on set energy for mating value, animal can mate once every that value. This energy increments daily. This energy gets reset to 0 after mating.
     */
    public void setEnergyForMating(int energyForMating) {
        this.energyForMating = energyForMating;
    }

    /**
     * Information about how many days has animal lived in simulation ( + what age it started). Animals can't die if age in this simulation. They however need minimum age to be able to mate with other animals.
     */
    public int getAge() {
        return age;
    }

    /**
     * Information about how many days has animal lived in simulation ( + what age it started). Animals can't die if age in this simulation. They however need minimum age to be able to mate with other animals.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * If animal gets killed by other animal or it dies of hunger, this attribute gets set to true. It is an indicator that this animal is to be deleted from the map.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * If animal gets killed by other animal or it dies of hunger, this attribute gets set to true. It is an indicator that this animal is to be deleted from the map.
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * This is an indicator that animal has been evaluated in a day of a simulation, so other animals wont interact with that evaluated animal anymore.
     */
    public boolean isDidEvaluate() {
        return didEvaluate;
    }

    /**
     * This is an indicator that animal has been evaluated in a day of a simulation, so other animals wont interact with that evaluated animal anymore.
     */
    public void setDidEvaluate(boolean didEvaluate) {
        this.didEvaluate = didEvaluate;
    }

    /**
     * The block animal is currently standing on.
     */
    public Block getBlock() {
        return block;
    }

    /**
     * The block animal is currently standing on.
     */
    public void setBlock(Block block) {
        this.block = block;
    }

    /**
     * The direction animal would be headed if it were to move.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * The direction animal would be headed if it were to move.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Boolean indicator that animal is currently being evaluated and so server shouldn't ask to modify this animal by request from another client.
     */
    public boolean isEvaluating() {
        return evaluating;
    }

    /**
     * Boolean indicator that animal is currently being evaluated and so server shouldn't ask to modify this animal by request from another client.
     */
    public void setEvaluating(boolean evaluating) {
        this.evaluating = evaluating;
    }
}
