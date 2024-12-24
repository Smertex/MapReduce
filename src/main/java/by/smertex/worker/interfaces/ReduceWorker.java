package by.smertex.worker.interfaces;

import java.util.List;

public interface ReduceWorker extends Runnable {
    void changeSetting(List<String> rootPaths, String endFile);
}
