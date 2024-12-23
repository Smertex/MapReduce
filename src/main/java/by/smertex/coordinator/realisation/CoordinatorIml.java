package by.smertex.coordinator.realisation;

import by.smertex.coordinator.interfaces.Coordinator;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CoordinatorIml implements Coordinator {

    private static final String PROXY_PACKAGE = "proxy";

    private final Executor executor;

    private final int threadCount;

    private final Map<UUID, List<String>> tasks;

    @Override
    public UUID map(String fileName) {
        UUID taskId = UUID.randomUUID();
        tasks.put(taskId, new ArrayList<>());
        File file = new File(fileName);

        return taskId;
    }

    @Override
    public void reduce(UUID processId, String finalFile) {

    }

    public CoordinatorIml(int threadCount) {
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.tasks = new HashMap<>();
    }
}
