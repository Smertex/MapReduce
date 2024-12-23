package by.smertex;

import by.smertex.coordinator.interfaces.Coordinator;
import by.smertex.coordinator.realisation.CoordinatorIml;

public class CoordinatorTest {

    private static final String START_TEST = "start.txt";

    private final Coordinator coordinator = new CoordinatorIml(10);

    void mapTest(){
        coordinator.map(START_TEST);
    }

}
