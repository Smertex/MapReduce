package by.smertex;

import by.smertex.worker.interfaces.ReduceWorker;
import by.smertex.worker.realisation.ReduceWorkerImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ReduceWorkerTest {
    private static final String END_FILE_TEST = "end.txt";

    private static final String PACKAGE_TEST = "test/e8b76b3f-4991-4ea8-a901-6c8665808c07";

    @Test
    void reduceTest() throws IOException {
        List<String> files = Files.walk(Path.of(PACKAGE_TEST))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());

        ReduceWorker reduceWorker = new ReduceWorkerImpl(files, END_FILE_TEST);
        reduceWorker.run();
    }

    @Test
    void parallelReduceTest() throws IOException, InterruptedException {
        List<String> files = Files.walk(Path.of(PACKAGE_TEST))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
        List<String> firstHalf = files.subList(0, files.size() / 2);
        List<String> secondHalf = files.subList(files.size() / 2, files.size());


        new Thread(() -> new ReduceWorkerImpl(firstHalf, END_FILE_TEST).run()).start();
        new Thread(() -> new ReduceWorkerImpl(secondHalf, END_FILE_TEST).run()).start();
        Thread.sleep(2000);
    }
}
