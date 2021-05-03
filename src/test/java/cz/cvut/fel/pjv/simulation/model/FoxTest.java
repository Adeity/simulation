package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoxTest {
    @ParameterizedTest
    @CsvSource({
            "3",
            "5",
            "10"
    })
    void test_testTemplateMap_twoFoxesMateInFiveRounds(int numOfRounds) {
        CONF.HARE_INIT_DIRECTION = null;
        CONF.FOX_INIT_DIRECTION = null;
        Simulation simulation = new Simulation();
        Map map = new Map("test_twoFoxesMateInFiveRounds.txt", simulation);
        simulation.setMap(map);
        Animal fox1 = map.blocks[0][0].getAnimal();
        Animal fox2 = map.blocks[0][1].getAnimal();

        //  set energy for mating needed
        CONF.ENERGY_FOR_MATING = 100;

        int hare1CurrEnergyForMating = CONF.ENERGY_FOR_MATING - numOfRounds;
        fox1.setEnergyForMating(hare1CurrEnergyForMating);

        assertEquals(
                hare1CurrEnergyForMating,
                fox1.getEnergyForMating()
        );

        int hare2CurrEnergyForMating = CONF.ENERGY_FOR_MATING - numOfRounds;
        fox2.setEnergyForMating(hare2CurrEnergyForMating);

        assertEquals(
                hare2CurrEnergyForMating,
                fox2.getEnergyForMating()
        );

        //  make sure fox wont die
        fox1.setEnergy(hare1CurrEnergyForMating + 100);
        assertEquals(
                hare1CurrEnergyForMating + 100,
                fox1.getEnergy()
        );

        fox2.setEnergy(hare2CurrEnergyForMating + 100);
        assertEquals(
                hare2CurrEnergyForMating + 100,
                fox2.getEnergy()
        );

        fox1.setAge(CONF.FOX_MATING_MIN_AGE + 10);
        assertEquals(
                CONF.FOX_MATING_MIN_AGE + 10,
                fox1.getAge()
        );

        fox2.setAge(CONF.FOX_MATING_MIN_AGE + 10);
        assertEquals(
                CONF.FOX_MATING_MIN_AGE + 10,
                fox2.getAge()
        );

        System.out.println("-------------numofrounds: " + numOfRounds + " --------------------");
        for (int i = 0; i < numOfRounds; i++) {

            System.out.println("iteration: " + i + " | fox1 energy: " + fox1.getEnergyForMating());
            System.out.println("iteration: " + i + " | fox2 energy: " + fox2.getEnergyForMating());
            map.evaluate();
            assertEquals(
                    2,
                    map.numOfAnimals
            );
        }

        //  when they have enough energy for mating finally a new fox is born
        System.out.println("Hare1 energy before last eval: " + fox1.getEnergyForMating());
        System.out.println("Hare2 energy before last eval: " + fox2.getEnergyForMating());
        map.evaluate();


        System.out.println("Hare1 energy after last eval: " + fox1.getEnergyForMating());
        System.out.println("Hare2 energy after last eval: " + fox2.getEnergyForMating());
        assertEquals(
                3,
                map.numOfAnimals
        );
    }

    @Test
    void test_spawnFoxHasAttributesInInterval() {
        Fox fox1 = new Fox();

        int minimumEnergy = CONF.FOX_INIT_MIN_ENERGY;
        int maximumEnergy = CONF.FOX_INIT_MAX_ENERGY;

        int minimumAge = CONF.FOX_INIT_MIN_AGE;
        int maximumAge = CONF.FOX_INIT_MAX_AGE;

        int minimumEnergyForMating = CONF.ENERGY_FOR_MATING_MIN;
        int maximumEnergyForMating = CONF.ENERGY_FOR_MATING_MAX;

        assertTrue(
                fox1.getEnergy() <= maximumEnergy
                &&
                        fox1.getEnergy() >= minimumEnergy
        );

        assertTrue(
                fox1.getAge() <= maximumAge
                        &&
                        fox1.getAge() >= minimumAge
        );

        assertTrue(
                fox1.getEnergyForMating() <= maximumEnergyForMating
                        &&
                        fox1.getEnergyForMating() >= minimumEnergyForMating
        );
    }

    @Test
    void test_mockito_twoFoxesMateSevenTimesInARow_blankmap() {
        //  make sure newborns arent ready for mating
        CONF.FOX_MATING_MIN_AGE = 100;
        Simulation simulation = new Simulation();
        Map map = new Map("test_mockito_twoFoxesMateSevenTimesInARow_blankmap.txt", simulation);
        simulation.setMap(map);

        Fox fox1 = spy(new Fox());
        Fox fox2 = spy(new Fox());

        fox1.setAge(CONF.FOX_MATING_MIN_AGE + 10);
        fox2.setAge(CONF.FOX_MATING_MIN_AGE + 10);

        map.setAnimalAtCoord(fox1, 1, 0);
        map.setAnimalAtCoord(fox2, 1, 1);

        assertEquals(
                fox1,
                map.blocks[1][0].getAnimal()
        );
        assertEquals(
                fox2,
                map.blocks[1][1].getAnimal()
        );

        Mockito.doReturn(true).when(fox1).areReadyForMating(fox2);
        Mockito.doReturn(true).when(fox2).areReadyForMating(fox1);

        System.out.println(map.animals.size());

        map.evaluate();
        assertEquals(
                3,
                map.animals.size()
        );

        map.evaluate();
        assertEquals(
                4,
                map.animals.size()
        );

        map.evaluate();
        assertEquals(
                5,
                map.animals.size()
        );

        map.evaluate();
        assertEquals(
                6,
                map.animals.size()
        );

        map.evaluate();
        assertEquals(
                7,
                map.animals.size()
        );

        map.evaluate();
        assertEquals(
                8,
                map.animals.size()
        );

        map.evaluate();
        assertEquals(
                9,
                map.animals.size()
        );
    }

    @Test
    void test_createNewBorn() {
        Fox fox = new Fox();
        Block b1 = new Block(
                Block.Terrain.BUSH,
                0,
                0
        );
        Fox newBorn = (Fox) fox.createNewBorn(b1);

        int expectedAge = 0;
        int expecteEnergyForMating = 0;
        boolean expectedDidEvaluate = true;
        Block expectedBlock = b1;
        Animal expectedAnimalOnBlock = newBorn;

        assertEquals(
                expectedAge,
                newBorn.getAge()
        );
        assertEquals(
                expecteEnergyForMating,
                newBorn.getEnergyForMating()
        );
        assertEquals(
                expectedDidEvaluate,
                newBorn.didEvaluate
        );
        assertEquals(
                expectedBlock,
                newBorn.getBlock()
        );
        assertEquals(
                expectedAnimalOnBlock,
                b1.getAnimal()
        );
    }

    @Test
    void test_foxDiesOfHunger() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_foxDiesOfHunger.txt", simulation);
        simulation.setMap(map);

        Animal fox = map.blocks[0][0].getAnimal();

        fox.setEnergy(5);

        int numOfRounds = 4;
        System.out.println("-------------numofrounds: " + numOfRounds + " --------------------");
        for (int i = 0; i < numOfRounds; i++) {

            System.out.println("iteration: " + i + " | fox1 energy: " + fox.getEnergy());
            map.evaluate();
            assertEquals(
                    1,
                    map.numOfAnimals
            );
        }

        //  when they have enough energy for mating finally a new fox is born
        System.out.println("fox energy before last eval: " + fox.getEnergy());
        map.evaluate();

        System.out.println("fox energy after last eval: " + fox.getEnergy());
        assertEquals(
                0,
                map.numOfAnimals
        );
        assertFalse(
                map.animals.contains(fox)
        );

        assertTrue(
                map.blocks[0][0].getAnimal() != fox
                        &&
                        map.blocks[0][1].getAnimal() != fox
                        &&
                        map.blocks[1][0].getAnimal() != fox
                        &&
                        map.blocks[1][1].getAnimal() != fox
        );
    }
}