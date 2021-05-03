package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest.Victim;

import static cz.cvut.fel.pjv.simulation.CONF.ENERGY_FOR_MATING;
import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

public class Hare extends Animal implements Victim {

    public Hare(Block block) {
        this.block = block;
        this.energyForMating = getRandomNumber(CONF.ENERGY_FOR_MATING_MIN, CONF.ENERGY_FOR_MATING_MAX);
        this.age = getRandomNumber(CONF.HARE_INIT_MIN_AGE, CONF.HARE_INIT_MAX_AGE);
        this.energy = getRandomNumber(CONF.HARE_INIT_MIN_ENERGY, CONF.HARE_INIT_MAX_ENERGY);
        this.direction = CONF.HARE_INIT_DIRECTION;
    }

    public Hare() {
        this(null);
    }

    @Override
    protected boolean mate(Simulation simulation, Animal otherAnimal) {
        mateChangeStats(otherAnimal);
        Block freeBlockForNewBorn = simulation.map.findFreeBlockForMating(this, otherAnimal);
        if (freeBlockForNewBorn == null) {
            System.out.println("There is no space for mating.");
            return false;
        }

        Hare newBorn = (Hare) createNewBorn(freeBlockForNewBorn);

        simulation.map.animals.add(newBorn);
        simulation.map.numOfHare++;
        simulation.map.numOfAnimals++;
        return true;
    }

    @Override
    protected boolean interact(Simulation simulation, Animal otherAnimal) {

        if (
                otherAnimal instanceof Fox
                        &&
                        willAnimalGetKilled(otherAnimal)
                &&
                        foxSeesHare(otherAnimal, this)
        ) {
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
        if (otherAnimal.isReadyToKill()) {
            return true;
        }
        //  TODO: implement terrain constraints
        return false;
    }

    @Override
    protected boolean die(Simulation simulation) {
        return super.die(simulation);
    }

    @Override
    protected boolean areReadyForMating(Animal otherAnimal) {
        if(
                this.age < CONF.HARE_MATING_MIN_AGE
                        ||
                        otherAnimal.age < CONF.HARE_MATING_MIN_AGE
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
    protected void nextDayChangeStats() {
        this.energy -= CONF.HARE_DAILY_ENERGY_DECREASE;
        this.age += CONF.HARE_DAILY_AGE_INCREASE;
        if(this.energyForMating < ENERGY_FOR_MATING) {
            this.energyForMating += CONF.ENERGY_FOR_MATING_DAILY_INCREASE;
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
        this.energyForMating = 0;
        otherAnimal.energyForMating = 0;
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