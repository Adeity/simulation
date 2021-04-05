package cz.cvut.fel.pjv.simulation;

public class CONF {
    public static String folderDirectory = "./src/main/java/cz/cvut/fel/pjv/simulation";
    public static String MAP_TEMPLATE_DIRECTORY = "./src/main/java/cz/cvut/fel/pjv/simulation/mapTemplates/";

    /*------------------Hare----------------*/
    public static int HARE_DAILY_AGE_INCREASE = 1; public static int HARE_DAILY_SATIETY_DECREASE = 0;

    public static int hareMovingFromGrassEnergyConsumption = 1;  public static int hareMovingFromBushesEnergyConsumption = 1;

    public static int HARE_INIT_MIN_ENERGY = 20;  public static int HARE_INIT_MAX_ENERGY = 50;
    public static int HARE_INIT_MIN_AGE = 20;      public static int HARE_INIT_MAX_AGE = 50;
    public static int HARE_INIT_MIN_SATIETY = 10; public static int HARE_INIT_MAX_SATIETY = 10;

    public static int HARE_MATING_MIN_AGE = 10;   public static int HARE_MATING_MIN_ENERGY = 10;  public static int HARE_MATING_MIN_SATIETY = 0; public static int HARE_MATING_ENERGY_CONSUMPTION = 5;



    /*------------------Fox----------------*/
    public static int FOX_DAILY_AGE_INCREASE = 1;  public static int FOX_DAILY_SATIETY_DECREASE = 1;
    public static int FOX_GRASS_ENERGY_DECREASE = 1;  public static int FOX_BUSH_ENERGY_DECREASE = 3;

    public static int FOX_INIT_MIN_ENERGY = 40;  public static int FOX_INIT_MAX_ENERGY = 50;
    public static int FOX_INIT_MIN_AGE = 20;      public static int FOX_INIT_MAX_AGE = 25;
    public static int FOX_INIT_MIN_SATIETY = 100; public static int FOX_INIT_MAX_SATIETY = 120;

    public static int FOX_MATING_MIN_AGE = 10;   public static int FOX_MATING_MIN_ENERGY = 10;   public static int FOX_MATING_MIN_SATIETY = 10;         public static int FOX_MATING_ENERGY_CONSUMPTION = 5;
    public static int FOX_KILLING_MIN_AGE = 5;   public static int FOX_KILLING_MIN_ENERGY = 5;   public static int FOX_KILLING_ENERGY_CONSUMPTION = 5;  public static int FOX_EATS_HARE_SATIETY_INCREASE = 5;

}
