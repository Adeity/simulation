package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest.Victim;

import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

public class Hare extends Animal implements Victim {

    public Hare(Block block) {
        this.block = block;
        this.age = getRandomNumber(CONF.HARE_INIT_MIN_AGE, CONF.HARE_INIT_MAX_AGE);
        this.energy = getRandomNumber(CONF.HARE_INIT_MIN_ENERGY, CONF.HARE_INIT_MAX_ENERGY);
        this.direction = Direction.randomDirection();
        this.direction = CONF.HARE_INIT_DIRECTION;
    }

    public Hare() {
        this(null);
    }

    @Override
    protected boolean mate(Map map, Animal otherAnimal) {
        Block freeBlockForNewBorn = map.findFreeBlockForMating(this, otherAnimal);
        if (freeBlockForNewBorn == null) {
            System.out.println("There is no space for mating.");
            return false;
        }

        Animal newBorn = new Hare(freeBlockForNewBorn);
        freeBlockForNewBorn.animal = newBorn;
        newBorn.age = 0;
        newBorn.didEvaluate = true;
        newBorn.block.animal = newBorn;
        map.addNewBornOnBlock(newBorn, freeBlockForNewBorn);
        map.animals.add(newBorn);
        map.numOfHare++;
        map.numOfAnimals++;

        mateChangeStats(otherAnimal);

        return true;
    }

    @Override
    protected boolean interact(Map map, Animal otherAnimal) {

        if (
                otherAnimal instanceof Fox
                        &&
                        willAnimalGetKilled(otherAnimal)
                &&
                        foxSeesHare(otherAnimal, this)
        ) {
            die(map);
            ((Fox) otherAnimal).killHareAddStats();
        }
        else if (
                otherAnimal instanceof Hare
                && areReadyForMating(otherAnimal)
        ) {
            mate(map, otherAnimal);
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
    protected void die(Map map) {
        super.die(map);
    }

    @Override
    protected boolean areReadyForMating(Animal otherAnimal) {
        if(
                this.age < CONF.HARE_MATING_MIN_AGE
                        ||
                        otherAnimal.age < CONF.HARE_MATING_MIN_AGE
        ){
            return false;
        }
        return true;
    }

    @Override
    protected void nextDayChangeStats() {
        this.energy -= CONF.HARE_DAILY_ENERGY_DECREASE;
        this.age += CONF.HARE_DAILY_AGE_INCREASE;
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
        this.energy -= CONF.HARE_MATING_ENERGY_CONSUMPTION;
        otherAnimal.energy -= CONF.HARE_MATING_ENERGY_CONSUMPTION;
    }
}