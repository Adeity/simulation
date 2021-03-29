package cz.cvut.fel.pjv.simulation.model;

import java.util.Objects;

public abstract class Animal {

    public int coordX;
    public int coordY;
    int y;
    public int energy;
    public int age;
    public boolean isDead;
    public boolean didEvaluate;
    public Block[] surroundingBlocks;

    @Override
    public String toString() {
        String res = "";
        res += getClass().getSimpleName();
        return res;
    }

    public void evaluate(Map map) {
        surroundingBlocks = map.getSurroundingBlocks(coordX, coordY);

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
        }
    }

    protected abstract String animalCode();
    protected abstract void interact(Map map, Animal otherAnimal, Block otherAnimalBlock);
}
