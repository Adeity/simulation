package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapTest {

    @Test
    void constructorMap_metaDataInTemplate_CorrectNumOfAnimals() {
        File mapTemplateDirectory = new File(CONF.MAP_TEMPLATE_DIRECTORY);
        File[] templates = mapTemplateDirectory.listFiles();

        int expectedNumOfFoxes = -1;
        int expectedNumOfHare = -1;
        int expectedNumOfAnimals = -1;

        Scanner scanner;
        String nextLine;

        if(templates != null) {
            for (File template : templates) {
                try {
                    scanner = new Scanner(template);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
                while (scanner.hasNextLine()) {
                    nextLine = scanner.nextLine();
                    String[] metaData = nextLine.split(" ");
                    try {
                        if(metaData[0].equals("numOfFoxes:")) {
                            expectedNumOfFoxes = Integer.parseInt(metaData[1]);
                        }
                        if(metaData[0].equals("numOfHare:")) {
                            expectedNumOfHare = Integer.parseInt(metaData[1]);
                        }
                        if(metaData[0].equals("numOfAnimals:")) {
                            expectedNumOfAnimals = Integer.parseInt(metaData[1]);
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("Template has invalid number values!");
                    }
                    if(nextLine.equals("----------")) {
                        break;
                    }
                    else if(nextLine.isEmpty()) {
                        break;
                    }
                }
                Map map = new Map(template.getName());

                assertEquals(
                        expectedNumOfAnimals,
                        map.numOfAnimals,
                        "Wrong num of animals at map " + template.getName()
                );
                assertEquals(
                        expectedNumOfFoxes,
                        map.numOfFoxes,
                        template.getName()
                );
                assertEquals(
                        expectedNumOfHare,
                        map.numOfHare,
                        template.getName()
                );

                int animalListSize = map.animals.size();
                assertEquals(
                        expectedNumOfAnimals,
                        animalListSize,
                        "There are more or less animals in animal list than expected! " + template.getName()
                );
            }
        }
    }

    @ParameterizedTest
    @CsvSource({
                    "1, 1",
                    "20, 400",
                    "100 , 10000",
                    "1000, 1000000"
            })
    void constructorMap_testMapSize_sizeSquared(int mapSizeParam, int expectedNumOfBlocks) {
        Map map = new Map(mapSizeParam);

        int actualNumOfBlocks = 0;
        for (Block[] blocks : map.blocks) {
            actualNumOfBlocks += blocks.length;
        }

        assertEquals(
                expectedNumOfBlocks,
                actualNumOfBlocks
        );
    }

    @Test
    void evaluate_predefinedState_TwoHareDie() {
        Map map = new Map("testmap3.txt");
        Map mapSpy = spy(map);
        Animal animalMock = mock(Animal.class);

        Hare hare1 = new Hare(
                mapSpy.blocks[2][1]
        );
        Hare hare2 = new Hare(
                mapSpy.blocks[1][0]
        );
        Fox fox1 = new Fox(
                mapSpy.blocks[0][1]
        );
        Fox fox2 = new Fox(
                mapSpy.blocks[0][2]
        );
        Fox fox3 = new Fox(
                mapSpy.blocks[0][0]
        );
        Hare hare3 = new Hare(
                mapSpy.blocks[2][0]
        );

        mapSpy.animals = new ArrayList<Animal>(9);

        mapSpy.animals.add(hare1);
        mapSpy.animals.add(hare2);
        mapSpy.animals.add(fox1);
        mapSpy.animals.add(fox2);
        mapSpy.animals.add(fox3);
        mapSpy.animals.add(hare3);

        assertEquals(
                6,
                map.animals.size()
        );

        mapSpy.evaluate();

        verify(mapSpy, times(2)).deleteAnimalAtBlock(any(Block.class));

    }
}