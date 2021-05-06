package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest.Victim;

import java.util.logging.Logger;

import static cz.cvut.fel.pjv.simulation.CONF.ENERGY_FOR_MATING;
import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

/**
 * Hare is an animal that moves around the map, gets eaten by fox and mates with other hare.
 */
public class Hare extends Animal implements Victim {
    private static final Logger LOG = Logger.getLogger(Hare.class.getName());
    public Hare(Block block) {
        this.setBlock(block);
        this.setEnergyForMating(getRandomNumber(CONF.ENERGY_FOR_MATING_MIN, CONF.ENERGY_FOR_MATING_MAX));
        this.setAge(getRandomNumber(CONF.HARE_INIT_MIN_AGE, CONF.HARE_INIT_MAX_AGE));
        this.setEnergy(getRandomNumber(CONF.HARE_INIT_MIN_ENERGY, CONF.HARE_INIT_MAX_ENERGY));
        this.setDirection(CONF.HARE_INIT_DIRECTION);
    }

    public Hare() {
        this(null);
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

        Hare newBorn = (Hare) createNewBorn(freeBlockForNewBorn);


        if (simulation.isOnMyMap(freeBlockForNewBorn.getCoordX(), freeBlockForNewBorn.getCoordY())) {
            simulation.map.getAnimals().add(newBorn);
            simulation.map.setNumOfHare(simulation.map.getNumOfHare() + 1);
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
    protected boolean interact(Simulation simulation, Animal otherAnimal) {
        LOG.info(this.toString() + " interacts with " + otherAnimal.toString());
        if (
                otherAnimal instanceof Fox
                        &&
                        willAnimalGetKilled(otherAnimal)
                &&
                        foxSeesHare(otherAnimal, this)
        ) {
            LOG.info("Evaluated that this animal will get killed by other animal. This animal dies.");
            die(simulation);
            ((Fox) otherAnimal).killHareAddStats();
        }
        else if (
                otherAnimal instanceof Hare
                && areReadyForMating(otherAnimal)
        ) {
            mate(simulation, otherAnimal);
        }
        else {
            LOG.info(this.toString() + " didnt interact with " + otherAnimal.toString() + " at all.");
            return false;
        }
        return true;
    }

    @Override
    protected String animalCode() {
        return "H";
    }

    @Override
    public boolean willAnimalGetKilled(Animal otherAnimal) {
        return otherAnimal.isReadyToKill();
    }

    @Override
    protected boolean die(Simulation simulation) {
        return super.die(simulation);
    }

    @Override
    protected boolean areReadyForMating(Animal otherAnimal) {
        if(
                this.getAge() < CONF.HARE_MATING_MIN_AGE
                        ||
                        otherAnimal.getAge() < CONF.HARE_MATING_MIN_AGE
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
    protected void nextDayChangeStats() {
        this.setEnergy(this.getEnergy() - CONF.HARE_DAILY_ENERGY_DECREASE);
        this.setAge(this.getAge() + CONF.HARE_DAILY_AGE_INCREASE);
        if(this.getEnergyForMating() < ENERGY_FOR_MATING) {
            this.setEnergyForMating(this.getEnergyForMating() + CONF.ENERGY_FOR_MATING_DAILY_INCREASE);
        }
    }

    /**
     * Hare doesnt kill anyone
     * @return false
     */
    @Override
    protected boolean isReadyToKill() {
        return false;
    }

    @Override
    protected void mateChangeStats(Animal otherAnimal) {
        this.setEnergyForMating(0);
        otherAnimal.setEnergyForMating(0);
    }

    @Override
    protected Animal createNewBorn(Block blockForNewBorn) {
        Hare newBornHare = new Hare();
        newBornHare.setAge(0);
        newBornHare.setEnergyForMating(0);
        newBornHare.setDidEvaluate(true);

        newBornHare.setBlock(blockForNewBorn);
        blockForNewBorn.setAnimal(newBornHare);

        return newBornHare;
    }
}