package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class AnimalTest {
    @Test
    void test_setAnimalAtCoord_animalAlreadyOnMap() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_setAnimalAtCoord_animalAlreadyOnMap.txt", simulation);
        simulation.setMap(map);

        int expectedNumOfAnimals;
        int currentNumOfAnimalsAttribute;
        int currentSizeOfAnimalsList;

        expectedNumOfAnimals = 1;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );


        map.setAnimalAtCoord(
                new Fox(),
                0,
                0
        );

        expectedNumOfAnimals = 1;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );
    }

    @Test
    void test_setAnimalAtCoord_blankmap() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_setAnimalAtCoord_blankmap.txt", simulation);
        simulation.setMap(map);

        int expectedNumOfAnimals;
        int currentNumOfAnimalsAttribute;
        int currentSizeOfAnimalsList;

        expectedNumOfAnimals = 0;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );


        map.setAnimalAtCoord(
                new Fox(),
                0,
                0
        );

        expectedNumOfAnimals = 1;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );
    }

    @Test
    void test_deleteAnimalAtCoord_blankmap() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_deleteAnimalAtCoord_blankmap.txt", simulation);
        simulation.setMap(map);

        int expectedNumOfAnimals;
        int currentNumOfAnimalsAttribute;
        int currentSizeOfAnimalsList;

        expectedNumOfAnimals = 0;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );


        map.deleteAnimalAtBlock(
                map.blocks[0][0]
        );

        expectedNumOfAnimals = 0;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );
    }

    @Test
    void test_deleteAnimalAtCoord_animalOnMap() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_deleteAnimalAtCoord_animalOnMap.txt", simulation);
        simulation.setMap(map);

        int expectedNumOfAnimals;
        int currentNumOfAnimalsAttribute;
        int currentSizeOfAnimalsList;

        expectedNumOfAnimals = 1;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        System.out.println(map);
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );

        map.deleteAnimalAtBlock(
                map.blocks[0][0]
        );
        expectedNumOfAnimals = 0;
        currentNumOfAnimalsAttribute = map.numOfAnimals;
        currentSizeOfAnimalsList = map.animals.size();
        assertEquals(
                expectedNumOfAnimals,
                currentNumOfAnimalsAttribute
        );
        assertEquals(
                expectedNumOfAnimals,
                currentSizeOfAnimalsList
        );
    }

    @Test
    void test_mockito_killerFoxDoesntSeeHare() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_mockito_killerFoxDoesntSeeHare.txt", simulation);
        simulation.setMap(map);

        Fox fox = spy(new Fox());
        Hare hare = spy(new Hare());

        //  make sure fox is ready to kill
        Mockito.doReturn(true).when(fox).isReadyToKill();

        map.setAnimalAtCoord(fox, 0, 0);

        //  make sure block on which hare is placed is bush
        assertEquals(
                Block.Terrain.BUSH,
                map.blocks[1][0].getTerrain()
        );
        map.setAnimalAtCoord(hare, 1, 0);

        map.evaluate();

        //  assert that there are still two animals on map, therefore fox didnt kill hare
        assertEquals(
                2,
                map.animals.size()
        );
        //  also check animals list
        assertTrue(map.animals.contains(hare));
        assertTrue(map.animals.contains(fox));
    }

    @Test
    void test_mockito_killerFoxKillsFiveHare() {
        Simulation simulation = new Simulation();
        Map map = new Map("test_mockito_killerFoxKillsEigthHare.txt", simulation);
        simulation.setMap(map);

        //  make sure hare dont die
        CONF.HARE_DAILY_ENERGY_DECREASE = 0;

        Fox fox = spy(new Fox());
        fox.setEnergy(1000);

        Mockito.doReturn(true).when(fox).isReadyToKill();

        map.setAnimalAtCoord(fox, 1, 1);

        int numOfRounds = 8;
        System.out.println("-------------numofrounds: " + numOfRounds + " --------------------");
        for (int i = numOfRounds; i > 0; i--) {
            System.out.println("before iteration: " + i + " | numOfAnimals: " + map.animals.size());
            map.evaluate();
            int expectedNumOfAnimals = i;
            System.out.println("after iteration: " + i + " | numOfAnimals: " + map.animals.size());
            assertEquals(
                    expectedNumOfAnimals,
                    map.numOfAnimals
            );
        }

        assertTrue(
                map.animals.contains(fox)
        );
    }
}