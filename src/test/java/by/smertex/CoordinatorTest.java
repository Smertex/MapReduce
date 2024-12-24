package by.smertex;

import by.smertex.coordinator.interfaces.Coordinator;
import by.smertex.coordinator.realisation.CoordinatorIml;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class CoordinatorTest {

    private static final String START_TEST = "start.txt";

    private static final String END_TEST = "proxy/end.txt";

    private static final UUID taskId = UUID.fromString("b418aa0f-74e8-4459-a2c8-002fc5c89a74");

    private final Coordinator coordinator = new CoordinatorIml(10);

    @Test
    void mapTest() throws InterruptedException {
        coordinator.map(START_TEST);
    }

    @Test
    void reduceTest() throws InterruptedException {
        coordinator.reduce(taskId, END_TEST);
        Thread.sleep(1000);
    }
}
