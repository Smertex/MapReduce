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

    private static final String PACKAGE_TEST = "proxy/9872cec9-556c-4395-89f9-c377906089e3";

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
