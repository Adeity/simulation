package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest.Killer;

import java.util.logging.Logger;

import static cz.cvut.fel.pjv.simulation.CONF.*;
import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

/**
 * Fox is an animal that kills hare, mates with other foxes and dies of hunger.
 */
public class Fox extends Animal implements Killer{
    private static final Logger LOG = Logger.getLogger(Fox.class.getName());
    public Fox() {
        this(null);
    }
    
    public Fox(Block block) {
        this.setEnergyForMating(getRandomNumber(CONF.ENERGY_FOR_MATING_MIN, CONF.ENERGY_FOR_MATING_MAX));
        this.setBlock(block);
        this.setAge(getRandomNumber(CONF.FOX_INIT_MIN_AGE, CONF.FOX_INIT_MAX_AGE));
        this.setEnergy(getRandomNumber(CONF.FOX_INIT_MIN_ENERGY, CONF.FOX_INIT_MAX_ENERGY));
        this.setDirection(FOX_INIT_DIRECTION);
    }

    @Override
    public String animalCode() {
        return "F";
    }

    @Override
    protected boolean interact(Simulation simulation, Animal otherAnimal) {

        if (otherAnimal instanceof Fox && areReadyForMating(otherAnimal)) {
            mate(simulation, otherAnimal);
        }

        else if (
                otherAnimal instanceof Hare
                && isReadyToKill() && foxSeesHare(this, otherAnimal)
        ) {
            kill(simulation, otherAnimal);
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public boolean isReadyToKill() {
        return true;
    }

    @Override
    protected boolean die(Simulation simulation) {
        return super.die(simulation);
    }

    @Override
    protected boolean areReadyForMating(Animal otherAnimal) {
        if(
                this.getAge() < CONF.FOX_MATING_MIN_AGE
                        ||
                        otherAnimal.getAge() < CONF.FOX_MATING_MIN_AGE
                ||
                        this.getEnergyForMating() < CONF.ENERGY_FOR_MATING
                ||
                        otherAnimal.getEnergyForMating() < CONF.ENERGY_FOR_MATING
        ){
            return false;
        }
        return true;
    }

    @Override
    protected boolean mate(Simulation simulation, Animal otherAnimal) {
        LOG.info(this.toString() + " and " + otherAnimal.toString() + " are trying to mate.");
        mateChangeStats(otherAnimal);
        LOG.info("Asking simulation to find free block for mating.");
        Block freeBlockForNewBorn = simulation.findFreeBlockForMating(this, otherAnimal);
        if (freeBlockForNewBorn == null) {
            LOG.info("No space for mating");
            return false;
        }

        Fox newBorn = (Fox) createNewBorn(freeBlockForNewBorn);


        if (simulation.isOnMyMap(freeBlockForNewBorn.getCoordX(), freeBlockForNewBorn.getCoordY())) {
            simulation.map.getAnimals().add(newBorn);
            simulation.map.setNumOfFoxes(simulation.map.getNumOfFoxes() + 1);
            simulation.map.setNumOfAnimals(simulation.map.getNumOfAnimals() + 1);
            freeBlockForNewBorn.setAnimal(newBorn);
            mateChangeStats(otherAnimal);
            return true;
        }
        else {
            if (simulation.simulationClient != null) {
                Block blockCopy = null;
                try {
                    blockCopy = (Block) freeBlockForNewBorn.clone();
                    blockCopy.setAnimal(newBorn);
                    if (simulation.simulationClient.setBlock(blockCopy.getCoordX(), blockCopy.getCoordY(), blockCopy)) {
                        LOG.info("Animal was born on another map.");
                        mateChangeStats(otherAnimal);
                        return true;
                    }
                    else {
                        return false;
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean willKill(Simulation simulation, Animal otherAnimal) {
        if (this.isReadyToKill()) {
            kill(simulation, otherAnimal);
            return true;
        }
        return false;
    }

    @Override
    public void kill(Simulation simulation, Animal otherAnimal) {
        if (otherAnimal.die(simulation)) {
            killHareAddStats();
        }
    }

    @Override
    public void killHareAddStats() {
        this.setEnergy(this.getEnergy() + CONF.FOX_KILLING_ENERGY_INCREASE);
    }

    @Override
    protected void nextDayChangeStats() {
        this.setEnergy(this.getEnergy() - FOX_DAILY_ENERGY_DECREASE);
        this.setAge(this.getAge() + FOX_DAILY_AGE_INCREASE);
        if(this.getEnergyForMating() < ENERGY_FOR_MATING) {
            this.setEnergyForMating(this.getEnergyForMating() + CONF.ENERGY_FOR_MATING_DAILY_INCREASE);
        }
    }

    @Override
    protected Animal createNewBorn(Block blockForNewBorn) {
        Fox newBornFox = new Fox();
        newBornFox.setAge(0);
        newBornFox.setEnergyForMating(0);
        newBornFox.setDidEvaluate(true);

        newBornFox.setBlock(blockForNewBorn);
        blockForNewBorn.setAnimal(newBornFox);

        return newBornFox;
    }

    @Override
    protected void mateChangeStats (Animal otherAnimal) {
        this.setEnergyForMating(0);
        otherAnimal.setEnergyForMating(0);
    }
}
