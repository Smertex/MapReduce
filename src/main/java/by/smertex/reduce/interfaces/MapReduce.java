package by.smertex.reduce.interfaces;

import java.util.UUID;

public interface MapReduce {
    UUID mapReduce(String originalFile, String finalFile);

    boolean up(UUID taskId, String finalFile);
}
