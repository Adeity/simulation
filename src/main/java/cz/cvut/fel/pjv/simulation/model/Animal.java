package cz.cvut.fel.pjv.simulation.model;

import java.util.Objects;

public abstract class Animal {
    public int energy;
    public int age;
    public boolean isDead;
    public boolean didEvaluate;
    public Block block;

    @Override
    public String toString() {
        String res = "";
        String isDead = (this.isDead ? "Dead" : "Alive");
        String didEvaluate = (this.didEvaluate ? "Evaluated" : "notEvaluated");
        res += getClass().getSimpleName() + " " + isDead + " " + age + " " + didEvaluate + " x:"+this.block.coordX+"y:"+this.block.coordY;
        return res;
    }


    public void evaluate(Map map) {
        Block[] surroundingBlocks = map.getSurroundingBlocks(this.block);

        for (Block block : surroundingBlocks) {
            if (block == null) {
                continue;
            }
            Animal otherAnimal = block.animal;

            if (otherAnimal == null) {
                continue;
            }
            if (otherAnimal.didEvaluate) {
                continue;
            }
            this.interact(map, otherAnimal, block);
            this.didEvaluate = true;
            otherAnimal.didEvaluate = true;
        }
    }
    protected boolean areReadyForMating (Animal otherAnimal) {
        boolean areSameSpecies = this.getClass().equals(otherAnimal.getClass());
        if (!areSameSpecies) {
            return false;
        }
        //  are of age
        if (!(this.age > 10 && otherAnimal.age > 10)) {
            return false;
        }
        //  have enough energy
        if (!(this.energy > 10 && otherAnimal.energy > 10)) {
            return false;
        }
        return true;
    }

    protected abstract String animalCode();
    protected abstract void interact(Map map, Animal otherAnimal, Block otherAnimalBlock);
}
