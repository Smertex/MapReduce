package by.smertex.coordinator.interfaces;

import java.util.UUID;

public interface Coordinator {
    UUID map(String fileName);

    void reduce(UUID processId, String finalFile);
}
