package cz.cvut.fel.pjv.simulation.model;

import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

public class Fox extends Animal {
    public int satiety;

    public Fox() {
        this.age = getRandomNumber(8, 15);
        this.energy = getRandomNumber(8, 15);
        this.satiety = 10;
    }

    @Override
    public String animalCode() {
        return "F";
    }

    @Override
    protected void interact(Map map, Animal otherAnimal, Block otherAnimalBlock) {
//        Block thisAnimalBlock = map.blocks[this.coordX][this.coordY];

        if (otherAnimal instanceof Fox) {

        }

        if (otherAnimal instanceof Hare) {
            otherAnimal.isDead = true;
            otherAnimal.didEvaluate = true;
            otherAnimalBlock.animal = null;
            map.numOfHare--;
            this.satiety += 5;
        }

        this.didEvaluate = true;
    }
}
