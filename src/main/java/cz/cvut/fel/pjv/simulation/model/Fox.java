package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest.Killer;
import cz.cvut.fel.pjv.simulation.utils.Utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.cvut.fel.pjv.simulation.CONF.*;
import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

public class Fox extends Animal implements Killer{
    private static final Logger LOG = Logger.getLogger(Map.class.getName());
    public Fox() {
        this(null);
    }
    
    public Fox(Block block) {
        Utilities.addHandlerToLogger(LOG);
        this.energyForMating = getRandomNumber(CONF.ENERGY_FOR_MATING_MIN, CONF.ENERGY_FOR_MATING_MAX);
        this.block = block;
        this.age = getRandomNumber(CONF.FOX_INIT_MIN_AGE, CONF.FOX_INIT_MAX_AGE);
        this.energy = getRandomNumber(CONF.FOX_INIT_MIN_ENERGY, CONF.FOX_INIT_MAX_ENERGY);
        this.direction = FOX_INIT_DIRECTION;
    }

    @Override
    public String animalCode() {
        return "F";
    }

    @Override
    protected boolean interact(Map map, Animal otherAnimal) {

        if (otherAnimal instanceof Fox && areReadyForMating(otherAnimal)) {
            mate(map, otherAnimal);
        }

        else if (
                otherAnimal instanceof Hare
                && isReadyToKill() && foxSeesHare(this, otherAnimal)
        ) {
            kill(map, otherAnimal);
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
    protected void die(Map map) {
        super.die(map);
    }

    @Override
    protected boolean areReadyForMating(Animal otherAnimal) {
        if(
                this.age < CONF.FOX_MATING_MIN_AGE
                        ||
                        otherAnimal.age < CONF.FOX_MATING_MIN_AGE
                ||
                        this.energyForMating < CONF.ENERGY_FOR_MATING
                ||
                        otherAnimal.energyForMating < CONF.ENERGY_FOR_MATING
        ){
            return false;
        }
        return true;
    }

    @Override
    protected boolean mate(Map map, Animal otherAnimal) {
        mateChangeStats(otherAnimal);
//        LOG.info(this + " is mating with " + otherAnimal);
        System.out.println(this + " is mating with " + otherAnimal);
        Block freeBlockForNewBorn = map.findFreeBlockForMating(this, otherAnimal);
        if (freeBlockForNewBorn == null) {
            LOG.info("No space for mating");
            return false;
        }

        Fox newBorn = (Fox) createNewBorn(freeBlockForNewBorn);

        map.animals.add(newBorn);
        map.numOfFoxes++;
        map.numOfAnimals++;

        mateChangeStats(otherAnimal);
        return true;
    }

    @Override
    public boolean willKill(Map map, Animal otherAnimal) {
        if (this.isReadyToKill()) {
            kill(map, otherAnimal);
            return true;
        }
        return false;
    }

    @Override
    public void kill(Map map, Animal otherAnimal) {
        otherAnimal.die(map);
        killHareAddStats();
    }

    @Override
    public void killHareAddStats() {
        this.energy += CONF.FOX_KILLING_ENERGY_INCREASE;
    }

    @Override
    protected void nextDayChangeStats() {
        this.energy -= FOX_DAILY_ENERGY_DECREASE;
        this.age += FOX_DAILY_AGE_INCREASE;
        if(this.energyForMating < ENERGY_FOR_MATING) {
            this.energyForMating += CONF.ENERGY_FOR_MATING_DAILY_INCREASE;
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
        this.energyForMating = 0;
        otherAnimal.energyForMating = 0;
    }
}
