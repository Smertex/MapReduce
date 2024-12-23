package by.smertex.worker.interfaces;

import java.util.UUID;

public interface Worker extends Runnable {
    void changeSetting(UUID taskId, String content);

    void setEndPath(String endPath);
}
