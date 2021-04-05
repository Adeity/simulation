package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HareTest {

    @Test
    void mateChangeStats_twoHareMate_CurrentEnergyMinusCONFEnergyConsumption() {
        Hare hare1 = new Hare();
        Hare hare2 = new Hare();

        int hare1ExpectedNewEnergy = hare1.energy - CONF.HARE_MATING_ENERGY_CONSUMPTION;
        int hare2ExpectedNewEnergy = hare2.energy - CONF.HARE_MATING_ENERGY_CONSUMPTION;

        hare1.mateChangeStats(hare2);

        assertEquals(
                hare1ExpectedNewEnergy,
                hare1.energy
        );
        assertEquals(
                hare2ExpectedNewEnergy,
                hare2.energy
        );
    }

    @Test
    void Mating_stubbingASpyOfHare_newAnimalOnMap() {
        Hare hare1 = spy(new Hare());
        Hare hare2 = spy(new Hare());

        //  set attributes to verify functional stub
        hare1.satiety = -100;
        hare2.satiety = -55;

        //  stub
        doReturn(true).when(hare1).areReadyForMating(hare2);

        Map map = new Map("testmap1.txt");

        map.setAnimalAtCoord(hare1, 2, 1);
        assertEquals(hare1, map.blocks[2][1].animal);
        assertEquals(hare1.block, map.blocks[2][1]);
        assertEquals(
                1,
                map.numOfHare
        );

        map.setAnimalAtCoord(hare2, 2, 2);
        assertEquals(hare2, map.blocks[2][2].animal);
        assertEquals(hare2.block, map.blocks[2][2]);
        assertEquals(
                2,
                map.numOfHare
        );

        hare1.interact(map, hare2);

        assertEquals(
                3,
                map.numOfHare
        );

        Animal newborn = map.blocks[1][0].animal;
        assertEquals(
                0,
                newborn.age
        );

        Animal newBornInAnimalList = null;
        for (Animal a : map.animals) {
            if(a.equals(newborn)) {
                newBornInAnimalList = newborn;
                break;
            }
        }
        assertEquals(
                newBornInAnimalList,
                newborn
        );

        for (Animal a : map.animals) {
            a.didEvaluate = false;
            a.nextDayChangeStats();
        }

        assertEquals(
                newborn.age,
                1
        );

        verify(hare1, times(1)).mate(map, hare2);
        verify(hare1, times(1)).mateChangeStats(hare2);
    }
}