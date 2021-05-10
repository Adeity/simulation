package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.model.Animal;

/**
 * configuration file with all animal parameters
 */
public class CONF {
    //  file separator
    public static final String fS = System.getProperty("file.separator");

    //  user directory
    private static final String uD = System.getProperty("user.dir");

    /**
     * This is where main root of simulation application is placed.
     */
    public static String simulationDirectory = uD + fS + "src" + fS + "main" + fS + "java" + fS + "cz" + fS + "cvut" + fS + "fel" + fS + "pjv" + fS + "simulation";

    /**
     * This is where package with map templates is.
     */
    public static String MAP_TEMPLATE_DIRECTORY = simulationDirectory + fS + "mapTemplates";

    /**
     * This is where package with map saves is.
     */
    public static String MAP_SAVES_DIRECTORY = simulationDirectory + fS + "mapSaves";

    public static int MOVES_PER_ROUND = 5;

    /**
     * This is the value animal needs to have to be able to mate. It gets reset to 0 after animal mates.
     */
    public static int ENERGY_FOR_MATING = 10;

    /**
     * This is minimum value of energy for mating with which animal gets generated with.
     */
    public static int ENERGY_FOR_MATING_MIN = 1;

    /**
     * This is maximum value of energy for mating with which animal gets generated with.
     */
    public static int ENERGY_FOR_MATING_MAX = 9;

    /**
     * This is how many units of energy for mating does animal get after each round.
     */
    public static int ENERGY_FOR_MATING_DAILY_INCREASE = 5;

    /*------------------Hare----------------*/
    /**
     * This is initial direction that hare gets generated with. Can be UP, LEFT, DOWN, RIGHT, or random.
     */
    public static Animal.Direction HARE_INIT_DIRECTION = Animal.Direction.randomDirection();

    /**
     * This is by what value age of animal gets increased every day.
     */
    public static int HARE_DAILY_AGE_INCREASE = 1;

    /**
     * This is by what value does energy increase for animal every day.
     */
    public static int HARE_DAILY_ENERGY_DECREASE = 0;

    /**
     * This is minimum value of energy animal gets generated with
     */
    public static int HARE_INIT_MIN_ENERGY = 20;

    /**
     * This is maximum value of energy animal gets generated with
     */
    public static int HARE_INIT_MAX_ENERGY = 50;

    /**
     * This is minimum value of age animal gets generated with
     */
    public static int HARE_INIT_MIN_AGE = 10;

    /**
     * This is maximum value of age animal gets generated with
     */
    public static int HARE_INIT_MAX_AGE = 200;

    /**
     * Animal must be atleast MATING_MIN_AGE old to be able to mate.
     */
    public static int HARE_MATING_MIN_AGE = 150;

    /*------------------Fox----------------*/
    /**
     * This is initial direction that fox gets generated with. Can be UP, LEFT, DOWN, RIGHT, or random.
     */
    public static Animal.Direction FOX_INIT_DIRECTION = Animal.Direction.randomDirection();

    /**
     * This is by what value age of animal gets increased every day.
     */
    public static int FOX_DAILY_AGE_INCREASE = 1;

    /**
     * This is by what value does energy increase for animal every day.
     */
    public static int FOX_DAILY_ENERGY_DECREASE = 5;

    /**
     * This is minimum value of energy animal gets generated with
     */
    public static int FOX_INIT_MIN_ENERGY = 30;

    /**
     * This is maximum value of energy animal gets generated with
     */
    public static int FOX_INIT_MAX_ENERGY = 50;

    /**
     * This is minimum value of age animal gets generated with
     */
    public static int FOX_INIT_MIN_AGE = 5;

    /**
     * This is maximum value of age animal gets generated with
     */
    public static int FOX_INIT_MAX_AGE = 50;

    /**
     * Animal must be atleast MATING_MIN_AGE old to be able to mate.
     */
    public static int FOX_MATING_MIN_AGE = 25;

    /**
     * This is how many units of energy does fox get after killing another animal.
     */
    public static int FOX_KILLING_ENERGY_INCREASE = 30;
}
