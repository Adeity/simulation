package cz.cvut.fel.pjv.simulation.model;

public class Fox extends Animal {
    public int satiety;

    public Fox() {
        super();
        this.energy = 10;
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
