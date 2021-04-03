package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.model.survivalOfTheFittest.Killer;

import static cz.cvut.fel.pjv.simulation.CONF.*;
import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

public class Fox extends Animal implements Killer{

    public Fox() {
        this(null);
    }
    
    public Fox(Block block) {
        this.block = block;
        this.age = getRandomNumber(CONF.FOX_INIT_MIN_AGE, CONF.FOX_INIT_MAX_AGE);
        this.energy = getRandomNumber(CONF.FOX_INIT_MIN_ENERGY, CONF.FOX_INIT_MAX_ENERGY);
        this.satiety = getRandomNumber(CONF.FOX_INIT_MIN_SATIETY, CONF.FOX_INIT_MAX_SATIETY);
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
                && isReadyToKill()
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
        return this.energy > FOX_KILLING_MIN_ENERGY
                &&
                this.age > FOX_KILLING_MIN_AGE;
    }

    @Override
    protected void die(Map map) {
        super.die(map);
        map.numOfFoxes--;
    }

    @Override
    protected boolean areReadyForMating(Animal otherAnimal) {
        if(
                this.energy < CONF.FOX_MATING_MIN_ENERGY
                ||
                        otherAnimal.energy < CONF.FOX_MATING_MIN_ENERGY
        ){
            return false;
        }
        if(
                this.age < CONF.FOX_MATING_MIN_AGE
                        ||
                        otherAnimal.age < CONF.FOX_MATING_MIN_AGE
        ){
            return false;
        }
        if(
                this.satiety < CONF.FOX_MATING_MIN_SATIETY
                        ||
                        otherAnimal.satiety < CONF.FOX_MATING_MIN_SATIETY
        ){
            return false;
        }
        return true;
    }

    @Override
    protected boolean mate(Map map, Animal otherAnimal) {
        Block freeBlockForNewBorn = map.findFreeBlockForMating(this, otherAnimal);
        if (freeBlockForNewBorn == null) {
            System.out.println("There is no space for mating.");
            return false;
        }

        Animal newBorn = new Fox(freeBlockForNewBorn);
        freeBlockForNewBorn.animal = newBorn;
        newBorn.block.animal = newBorn;
        newBorn.age = 1;

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
        this.satiety += CONF.FOX_EATS_HARE_SATIETY_INCREASE;
        this.energy -= CONF.FOX_KILLING_ENERGY_CONSUMPTION;
    }

    @Override
    protected void mateChangeStats (Animal otherAnimal) {
        this.energy -= CONF.FOX_MATING_ENERGY_CONSUMPTION;
        otherAnimal.energy -= CONF.FOX_MATING_ENERGY_CONSUMPTION;
    }

    @Override
    protected void nextDayChangeStats() {
        this.satiety -= FOX_DAILY_SATIETY_DECREASE;
        this.age += FOX_DAILY_AGE_INCREASE;
    }
}
