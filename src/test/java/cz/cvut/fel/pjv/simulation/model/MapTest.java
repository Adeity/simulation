package cz.cvut.fel.pjv.simulation.model;

import cz.cvut.fel.pjv.simulation.CONF;
import cz.cvut.fel.pjv.simulation.Simulation;
import cz.cvut.fel.pjv.simulation.network.server.SimulationServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapTest {

    @Test
    void test_corretNumOfAnimalsInTemplate() {
        File mapTemplateDirectory = new File(CONF.MAP_TEMPLATE_DIRECTORY);
        File[] templates = mapTemplateDirectory.listFiles();

        String line;

        if(templates != null) {
            for (File template : templates) {
                Integer expectedNumOfFoxes = null;
                Integer expectedNumOfHare = null;
                Integer expectedNumOfAnimals = null;
                try (
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(template), StandardCharsets.UTF_8));
                        ){
                    while ((line = br.readLine()) != null) {
                        String[] metaData = line.split(" ");
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
                        if(line.equals("----------")) {
                            break;
                        }
                        else if(line.isEmpty()) {
                            break;
                        }
                    }
                    Simulation simulation = new Simulation();
                    Map map = new Map(template.getName(), simulation);
                    simulation.setMap(map);

                    if (expectedNumOfAnimals != null) {
                        assertEquals(
                                expectedNumOfAnimals.intValue(),
                                map.numOfAnimals,
                                "Wrong num of animals at map " + template.getName()
                        );
                        int animalListSize = map.animals.size();
                        assertEquals(
                                expectedNumOfAnimals.intValue(),
                                animalListSize,
                                "There are more or less animals in animal list than expected! " + template.getName()
                        );
                    }
                    else {
                        System.out.println("numOfanimals not defined in: " +template.getName());
                    }
                    if(expectedNumOfFoxes != null) {
                        assertEquals(
                                expectedNumOfFoxes.intValue(),
                                map.numOfFoxes,
                                template.getName()
                        );
                    }
                    else {
                        System.out.println("numOfFoxes not defined in: " +template.getName());
                    }
                    if(expectedNumOfHare != null) {
                        assertEquals(
                                expectedNumOfHare.intValue(),
                                map.numOfHare,
                                template.getName()
                        );
                    }
                    else {
                        System.out.println("numOfHare not defined in: " +template.getName());
                    }


                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
    void test_sizeMapConstructor(int mapSizeParam, int expectedNumOfBlocks) {
        Simulation simulation = new Simulation();
        Map map = new Map(mapSizeParam, simulation);

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
    void test_TwoHareDie() {
        CONF.HARE_INIT_DIRECTION = null;
        CONF.FOX_INIT_DIRECTION = null;
        CONF.FOX_MATING_MIN_AGE = 100000;
        CONF.FOX_INIT_MIN_ENERGY = 100;
        CONF.FOX_INIT_MAX_ENERGY = 110;
        Simulation simulation = new Simulation();
        Simulation simulationSpy = spy(simulation);
        Map map = new Map("testmap3.txt", simulationSpy);
        simulationSpy.setMap(map);
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

        mapSpy.animals.add(hare2);
        mapSpy.animals.add(hare1);
        mapSpy.animals.add(fox1);
        mapSpy.animals.add(fox2);
        mapSpy.animals.add(fox3);
        mapSpy.animals.add(hare3);

        assertEquals(
                6,
                map.animals.size()
        );

        mapSpy.evaluate();

        verify(simulationSpy, times(1)).deleteAnimalAtBlock(any(Block.class));

    }
}