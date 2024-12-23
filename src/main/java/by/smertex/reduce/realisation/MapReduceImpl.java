package by.smertex.reduce.realisation;

import by.smertex.coordinator.realisation.CoordinatorIml;
import by.smertex.reduce.interfaces.MapReduce;

import java.util.UUID;

public class MapReduceImpl implements MapReduce {

    private final CoordinatorIml coordinatorIml;

    @Override
    public UUID mapReduce(String originalFile, String finalFile) {
        UUID taskId = null;
        try {
            taskId = coordinatorIml.map(originalFile);
            coordinatorIml.reduce(taskId, finalFile);
            return null;
        } catch (Exception e){
            return taskId;
        }
    }

    @Override
    public boolean up(UUID taskId, String finalFile) {
        try {
            coordinatorIml.reduce(taskId, finalFile);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public MapReduceImpl() {
        coordinatorIml = new CoordinatorIml(10);
    }

    public MapReduceImpl(int threadCount) {
        coordinatorIml = new CoordinatorIml(threadCount);
    }
}
