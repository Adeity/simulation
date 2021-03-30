package cz.cvut.fel.pjv.simulation.model;

import static cz.cvut.fel.pjv.simulation.utils.Utilities.getRandomNumber;

public class Hare extends Animal {

    public Hare(Block block) {
        this.block = block;
        this.age = getRandomNumber(10, 20);
        this.energy = 14;
    }

    public Hare() {
        this.age = getRandomNumber(10, 20);
        this.energy = 14;
    }

    @Override
    protected String animalCode() {
        return "H";
    }

    @Override
    protected void interact(Map map, Animal otherAnimal, Block otherAnimalBlock) {

        if (otherAnimal instanceof Fox) {
            this.isDead = true;
            map.numOfHare--;
            this.block.animal = null;
        }

        if (otherAnimal instanceof Hare) {
            if (this.areReadyForMating(otherAnimal)) {
                Block freeBlockForMating = map.findFreeBlockForMating(this, otherAnimal);
                //  first check surrounding of this animal
                if (freeBlockForMating == null) {
                    System.out.println("There is no space for mating.");
                    return;
                }
                Animal newBorn = new Hare(freeBlockForMating);
                freeBlockForMating.animal = newBorn;
                newBorn.age = 1;
                map.animals.add(newBorn);
                map.numOfHare++;
                this.energy -= 10;

            }
        }
        this.didEvaluate = true;
    }
}
