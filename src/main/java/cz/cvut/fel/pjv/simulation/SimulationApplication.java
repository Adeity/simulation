package cz.cvut.fel.pjv.simulation;

import cz.cvut.fel.pjv.simulation.controller.MapController;
import cz.cvut.fel.pjv.simulation.model.Fox;
import cz.cvut.fel.pjv.simulation.model.Hare;
import cz.cvut.fel.pjv.simulation.model.Map;

public class SimulationApplication {
    public static void main (String[] args) {
        Map map = new Map(30);
        MapController mapController = new MapController(map);

        Fox fox2 = new Fox();
        Fox fox3 = new Fox();

        Hare hare1 = new Hare();
        Hare hare2 = new Hare();
        Hare hare3 = new Hare();
        Hare hare4 = new Hare();

        mapController.addAnimalAtCoord(fox2, 2, 0);
        mapController.changeAnimalAtCoord(fox3, 0, 0);

        System.out.println(map);


    }
}
