package cz.cvut.fel.pjv.simulation.model;

public abstract class Animal {
    public int energy;
    public int age;
    public int satiety;
    public boolean isDead;
    public boolean didEvaluate;
    public Block block;

    /**
     * If animal looks around and finds another animal to interact with, it does so.
     * Otherwise the animal moves.
     * @param map map of simulation
     */
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
            if(this.interact(map, otherAnimal)) {
                this.didEvaluate = true;
                otherAnimal.didEvaluate = true;
            }
        }
        if(!this.didEvaluate) {
            //  TODO: implement moving
//            move();
        }
        nextDayChangeStats();
    }

    /**
     * Animal dies.
     * Its parameters get set to negative values. And it gets deleted from map
     * @param map map of simulation
     */
    protected void die(Map map) {
        this.isDead = true;
        this.energy = -10;
        this.age = -10;
        this.satiety = -10;
        this.block.animal = null;
        this.block = null;
        map.numOfAnimals--;
    }

    /**
     * Does the animal meet the requirements to be able to kill another animal
     * @return true if the animal meets the requirements to kill another animal
     */
    protected abstract boolean isReadyToKill();

    /**
     * Do both animals meet the requirements for mating
     * @param otherAnimal is animal to mate with
     * @return true if both animals meet the requirements for mating
     */
    protected abstract boolean areReadyForMating (Animal otherAnimal);


    /**
     * Two animals mate with each other
     * @param map is map of simulation
     * @param otherAnimal is animal animal is mating with
     * @return true if there is a newborn animal on the map, false if there isn't
     */
    protected abstract boolean mate(Map map, Animal otherAnimal);

    /**
     * One letter code of animal
     * @return code of animal
     */
    protected abstract String animalCode();

    /**
     * Animal interacts with another animal on the map
     * @param map is map of simulation
     * @param otherAnimal is animal animal is interacting with
     * @return true if animals were able to interact with each other, false otherwise
     */
    protected abstract boolean interact(Map map, Animal otherAnimal);

    /**
     * After each day set new attribute values for an animal.
     */
    protected abstract void nextDayChangeStats();

    /**
     * Change stats of animals after mating.
     * @param otherAnimal the animal animal mated with.
     */
    protected abstract void mateChangeStats(Animal otherAnimal);

    @Override
    public String toString() {
        String res = "";
        String isDead = (this.isDead ? "Dead" : "Alive");
        String didEvaluate = (this.didEvaluate ? "Evaluated" : "notEvaluated");
        res += getClass().getSimpleName() + " x,y:("+this.block.coordX+", "+this.block.coordY + ") " + isDead + " age: " + age + " satiety: " +  satiety + " energy: " +energy;
        return res;
    }
}
