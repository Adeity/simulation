package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HareTest {
    private static final Logger LOG = Logger.getLogger(HareTest.class.getName());

    @Test
    void test_mateChangeStats() {
        Hare hare1 = new Hare();
        Hare hare2 = new Hare();

        int hare1ExpectedNewEnergy = 0;
        int hare2ExpectedNewEnergy = 0;

        hare1.mateChangeStats(hare2);

        assertEquals(
                hare1ExpectedNewEnergy,
                hare1.getEnergyForMating()
        );
        assertEquals(
                hare2ExpectedNewEnergy,
                hare2.getEnergyForMating()
        );
    }

    @Test
    void test_hareMate() {
        Hare hare1 = spy(new Hare());
        Hare hare2 = spy(new Hare());

        //  stub
        doReturn(true).when(hare1).areReadyForMating(hare2);
        Simulation simulation = new Simulation();
        Map map = new Map("testmap1.txt", simulation);
        simulation.setMap(map);

        map.setAnimalAtCoord(hare1, 2, 1);
        assertEquals(hare1, map.getBlocks()[2][1].getAnimal());
        assertEquals(hare1.getBlock(), map.getBlocks()[2][1]);
        assertEquals(
                1,
                map.getNumOfHare()
        );

        map.setAnimalAtCoord(hare2, 2, 2);
        assertEquals(hare2, map.getBlocks()[2][2].getAnimal());
        assertEquals(hare2.getBlock(), map.getBlocks()[2][2]);
        assertEquals(
                2,
                map.getNumOfHare()
        );

        hare1.interact(simulation, hare2);

        assertEquals(
                3,
                map.getNumOfHare()
        );

        Animal newborn = map.getBlocks()[1][0].getAnimal();
        assertEquals(
                0,
                newborn.getAge()
        );

        Animal newBornInAnimalList = null;
        for (Animal a : map.getAnimals()) {
            if(a.equals(newborn)) {
                newBornInAnimalList = newborn;
                break;
            }
        }
        assertEquals(
                newBornInAnimalList,
                newborn
        );

        for (Animal a : map.getAnimals()) {
            a.setDidEvaluate(false);
            a.nextDayChangeStats();
        }

        assertEquals(
                newborn.getAge(),
                1
        );

        verify(hare1, times(1)).mate(simulation, hare2);
        verify(hare1, times(1)).mateChangeStats(hare2);
    }

    @ParameterizedTest
    @CsvSource({
            "3",
            "5",
            "10"
    })
    void test_testTemplateMap_twoHareMateInFiveRounds(int numOfRounds) {
        CONF.HARE_INIT_DIRECTION = null;
        CONF.FOX_INIT_DIRECTION = null;
        Simulation simulation = new Simulation();
        Map map = new Map("test_twoHareMateInFiveRounds.txt", simulation);
        simulation.setMap(map);
        Animal hare1 = map.getBlocks()[0][0].getAnimal();
        Animal hare2 = map.getBlocks()[0][1].getAnimal();

        //  set energy for mating needed
        CONF.ENERGY_FOR_MATING = 100;

        int hare1CurrEnergyForMating = CONF.ENERGY_FOR_MATING - numOfRounds;
        hare1.setEnergyForMating(hare1CurrEnergyForMating);

        assertEquals(
                hare1CurrEnergyForMating,
                hare1.getEnergyForMating()
        );

        int hare2CurrEnergyForMating = CONF.ENERGY_FOR_MATING - numOfRounds;
        hare2.setEnergyForMating(hare2CurrEnergyForMating);

        assertEquals(
                hare2CurrEnergyForMating,
                hare2.getEnergyForMating()
        );

        //  make sure hare wont die
        hare1.setEnergy(hare1CurrEnergyForMating + 100);
        assertEquals(
                hare1CurrEnergyForMating + 100,
                hare1.getEnergy()
        );

        hare2.setEnergy(hare2CurrEnergyForMating + 100);
        assertEquals(
                hare2CurrEnergyForMating + 100,
                hare2.getEnergy()
        );


        LOG.info("-------------numofrounds: " + numOfRounds + " --------------------");
        for (int i = 0; i < numOfRounds; i++) {

            LOG.info("iteration: " + i + " | hare1 energy: " + hare1.getEnergyForMating());
            LOG.info("iteration: " + i + " | hare2 energy: " + hare2.getEnergyForMating());
            map.evaluate();
            assertEquals(
                    2,
                    map.getNumOfAnimals()
            );
        }

        //  when they have enough energy for mating finally a new hare is born

        LOG.info("Hare1 energy before last eval: " + hare1.getEnergyForMating());
        LOG.info("Hare2 energy before last eval: " + hare2.getEnergyForMating());
        map.evaluate();


        LOG.info("Hare1 energy after last eval: " + hare1.getEnergyForMating());
        LOG.info("Hare2 energy after last eval: " + hare2.getEnergyForMating());
        assertEquals(
                3,
                map.getNumOfAnimals()
        );
    }

    @Test
    void test_spawnHareHasAttributesInInterval() {
        Hare hare1 = new Hare();

        int minimumEnergy = CONF.HARE_INIT_MIN_ENERGY;
        int maximumEnergy = CONF.HARE_INIT_MAX_ENERGY;

        int minimumAge = CONF.HARE_INIT_MIN_AGE;
        int maximumAge = CONF.HARE_INIT_MAX_AGE;

        int minimumEnergyForMating = CONF.ENERGY_FOR_MATING_MIN;
        int maximumEnergyForMating = CONF.ENERGY_FOR_MATING_MAX;

        assertTrue(
                hare1.getEnergy() <= maximumEnergy
                        &&
                        hare1.getEnergy() >= minimumEnergy
        );

        assertTrue(
                hare1.getAge() <= maximumAge
                        &&
                        hare1.getAge() >= minimumAge
        );

        LOG.info("Min energy for mating: " + minimumEnergyForMating + " | Max energy for mating: " + maximumEnergyForMating);
        LOG.info("Hare energy for mating on spawn: " + hare1.getEnergyForMating());
        assertTrue(
                hare1.getEnergyForMating() <= maximumEnergyForMating
                        &&
                        hare1.getEnergyForMating() >= minimumEnergyForMating
        );
    }

    @Test
    void test_createNewBorn() {
        Hare hare = new Hare();
        Block b1 = new Block(
                Block.Terrain.BUSH,
                0,
                0
        );
        Hare newBorn = (Hare) hare.createNewBorn(b1);

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
                newBorn.isDidEvaluate()
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
    void test_mockito_twoFoxesMateSevenTimesInARow_blankmap() {
        //  make sure newborns arent ready for mating
        CONF.FOX_MATING_MIN_AGE = 100;
        Simulation simulation = new Simulation();
        Map map = new Map("test_mockito_twoHareMateSevenTimesInARow_blankmap.txt", simulation);
        simulation.setMap(map);

        Hare hare1 = spy(new Hare());
        Hare hare2 = spy(new Hare());

        hare1.setAge(CONF.FOX_MATING_MIN_AGE + 10);
        hare2.setAge(CONF.FOX_MATING_MIN_AGE + 10);

        map.setAnimalAtCoord(hare1, 1, 0);
        map.setAnimalAtCoord(hare2, 1, 1);

        assertEquals(
                hare1,
                map.getBlocks()[1][0].getAnimal()
        );
        assertEquals(
                hare2,
                map.getBlocks()[1][1].getAnimal()
        );

        Mockito.doReturn(true).when(hare1).areReadyForMating(hare2);
        Mockito.doReturn(true).when(hare2).areReadyForMating(hare1);

        LOG.info("size of map: " + map.getAnimals().size());

        map.evaluate();
        assertEquals(
                3,
                map.getAnimals().size()
        );

        map.evaluate();
        assertEquals(
                4,
                map.getAnimals().size()
        );

        map.evaluate();
        assertEquals(
                5,
                map.getAnimals().size()
        );

        map.evaluate();
        assertEquals(
                6,
                map.getAnimals().size()
        );

        map.evaluate();
        assertEquals(
                7,
                map.getAnimals().size()
        );

        map.evaluate();
        assertEquals(
                8,
                map.getAnimals().size()
        );

        map.evaluate();
        assertEquals(
                9,
                map.getAnimals().size()
        );
    }
}