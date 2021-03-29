package cz.cvut.fel.pjv.simulation.model;

public class Hare extends Animal {

    public Hare() {
        this.energy = 14;
    }

    @Override
    protected String animalCode() {
        return "H";
    }

    @Override
    protected void interact(Map map, Animal otherAnimal, Block otherAnimalBlock) {
        Block thisAnimalBlock = map.blocks[this.coordX][this.coordY];

        if (otherAnimal instanceof Fox) {
            this.isDead = true;
            map.numOfHare--;
            thisAnimalBlock.animal = null;
        }

        if (otherAnimal instanceof Hare) {
            if (
                    this.age > 10
                            &&
                            otherAnimal.age > 10
            ) {
                Block blockForNewborn = null;
                //  first check surrounding of this animal
                for (Block block : surroundingBlocks) {
                    if (block.animal == null) {
                        blockForNewborn = block;
                        break;
                    }
                }
                //  if surrounding of this animal fails, check surrounding of other animal
                if (blockForNewborn == null) {
                    Block[] otherAnimalSurroundingBlocks = map.getSurroundingBlocks(
                            otherAnimal.coordX,
                            otherAnimal.coordY
                    );
                    for (Block block : otherAnimalSurroundingBlocks) {
                        if (block.animal == null) {
                            blockForNewborn = block;
                            break;
                        }
                    }
                }
                //  if an empty block was found, addNewBorn to map on found block
                if(blockForNewborn != null) {
                    map.addNewBorn(new Hare(), blockForNewborn.coordX, blockForNewborn.coordY);
                    map.numOfHare++;
                }
            }
        }
        this.didEvaluate = true;
    }
}
