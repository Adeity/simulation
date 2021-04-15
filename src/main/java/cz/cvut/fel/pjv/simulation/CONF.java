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


    /*------------------Hare----------------*/
    public static int HARE_MOVES_PER_ROUND = 5;
    public static Animal.Direction HARE_INIT_DIRECTION = Animal.Direction.randomDirection();
    public static int HARE_DAILY_AGE_INCREASE = 1; public static int HARE_DAILY_SATIETY_DECREASE = 0;
    public static int HARE_GRASS_ENERGY_DECREASE = 1;  public static int HARE_BUSH_ENERGY_DECREASE = 3;


    public static int hareMovingFromGrassEnergyConsumption = 1;  public static int hareMovingFromBushesEnergyConsumption = 1;

    public static int HARE_INIT_MIN_ENERGY = 20;  public static int HARE_INIT_MAX_ENERGY = 50;
    public static int HARE_INIT_MIN_AGE = 20;      public static int HARE_INIT_MAX_AGE = 50;
    public static int HARE_INIT_MIN_SATIETY = 10; public static int HARE_INIT_MAX_SATIETY = 10;

    public static int HARE_MATING_MIN_AGE = 10;   public static int HARE_MATING_MIN_ENERGY = 10;  public static int HARE_MATING_MIN_SATIETY = 0; public static int HARE_MATING_ENERGY_CONSUMPTION = 5;



    /*------------------Fox----------------*/
    public static int FOX_MOVES_PER_ROUND = 6;
    public static Animal.Direction FOX_INIT_DIRECTION = Animal.Direction.DOWN;
    public static int FOX_DAILY_AGE_INCREASE = 1;  public static int FOX_DAILY_SATIETY_DECREASE = 1;
    public static int FOX_GRASS_ENERGY_DECREASE = 1;  public static int FOX_BUSH_ENERGY_DECREASE = 3;

    public static int FOX_INIT_MIN_ENERGY = 40;  public static int FOX_INIT_MAX_ENERGY = 50;
    public static int FOX_INIT_MIN_AGE = 20;      public static int FOX_INIT_MAX_AGE = 25;
    public static int FOX_INIT_MIN_SATIETY = 100; public static int FOX_INIT_MAX_SATIETY = 120;

    public static int FOX_MATING_MIN_AGE = 10;   public static int FOX_MATING_MIN_ENERGY = 10;   public static int FOX_MATING_MIN_SATIETY = 10;         public static int FOX_MATING_ENERGY_CONSUMPTION = 5;
    public static int FOX_KILLING_MIN_AGE = 5;   public static int FOX_KILLING_MIN_ENERGY = 5;   public static int FOX_KILLING_ENERGY_CONSUMPTION = 5;  public static int FOX_EATS_HARE_SATIETY_INCREASE = 5;

}
