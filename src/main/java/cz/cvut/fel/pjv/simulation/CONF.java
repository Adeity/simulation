package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.model.Animal;

public class CONF {
    //  file separator
    public static final String fS = System.getProperty("file.separator");

    //  user directory
    private static final String uD = System.getProperty("user.dir");

    public static String simulationDirectory = uD + fS + "src" + fS + "main" + fS + "java" + fS + "cz" + fS + "cvut" + fS + "fel" + fS + "pjv" + fS + "simulation";
    public static String MAP_TEMPLATE_DIRECTORY = simulationDirectory + fS + "mapTemplates";
    public static String MAP_SAVES_DIRECTORY = simulationDirectory + fS + "mapSaves";

    public static int MOVES_PER_ROUND = 5;

    /*------------------Hare----------------*/

    public static Animal.Direction HARE_INIT_DIRECTION = Animal.Direction.randomDirection();
    public static int HARE_DAILY_AGE_INCREASE = 1; public static int HARE_DAILY_ENERGY_DECREASE = 0;

    public static int HARE_INIT_MIN_ENERGY = 20;  public static int HARE_INIT_MAX_ENERGY = 50;
    public static int HARE_INIT_MIN_AGE = 20;      public static int HARE_INIT_MAX_AGE = 50;

    public static int HARE_MATING_MIN_AGE = 10;   public static int HARE_MATING_ENERGY_CONSUMPTION = 5;



    /*------------------Fox----------------*/
    public static Animal.Direction FOX_INIT_DIRECTION = Animal.Direction.DOWN;
    public static int FOX_DAILY_AGE_INCREASE = 1;  public static int FOX_DAILY_ENERGY_DECREASE = 1;

    public static int FOX_INIT_MIN_ENERGY = 40;  public static int FOX_INIT_MAX_ENERGY = 50;
    public static int FOX_INIT_MIN_AGE = 20;      public static int FOX_INIT_MAX_AGE = 25;

    public static int FOX_MATING_MIN_AGE = 20;   public static int FOX_MATING_ENERGY_CONSUMPTION = 5;
    public static int FOX_KILLING_MIN_AGE = 5;   public static int FOX_KILLING_ENERGY_INCREASE = 5;

}
