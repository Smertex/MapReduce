package by.smertex;

import by.smertex.coordinator.interfaces.Coordinator;
import by.smertex.coordinator.realisation.CoordinatorIml;
import org.junit.jupiter.api.Test;

public class CoordinatorTest {

    private static final String START_TEST = "start.txt";

    private final Coordinator coordinator = new CoordinatorIml(10);

    @Test
    void mapTest() throws InterruptedException {
        coordinator.map(START_TEST);
        Thread.sleep(10000);
    }
}
