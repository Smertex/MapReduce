package by.smertex.coordinator.realisation;

import by.smertex.coordinator.interfaces.Coordinator;
import by.smertex.reduce.exeception.ReadFileException;
import by.smertex.worker.realisation.MapMapWorkerImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CoordinatorIml implements Coordinator {

    private static final String PROXY_PACKAGE = "proxy";

    private final Executor executor;

    private final int threadCount;

    private final int minCharForThreads;

    @Override
    public UUID map(String fileName) {
        UUID taskId = UUID.randomUUID();
        Coordinator.packageForTaskGenerate(PROXY_PACKAGE, taskId);
        mapAllocator(taskId, contentSpliterator(fileName));
        return taskId;
    }

    @Override
    public void reduce(UUID processId, String finalFile) {

    }

    private void mapAllocator(UUID taskId, List<String> words) {
        words.forEach(word -> executor.execute(() -> new MapMapWorkerImpl(taskId, word, PROXY_PACKAGE).run()));
    }

    private List<String> contentSpliterator(String fileName){
        List<String> wordList = new ArrayList<>();
        List<String> words = List.of(readFile(fileName).split(" "));

        int pointer = 0;
        for (int i = 0; i < threadCount; i++) {
            StringBuilder sb = new StringBuilder();
            int start = pointer;
            if(words.size() - pointer > minCharForThreads) {
                for (int j = start; j < (words.size() / threadCount) + start; j++) {
                    sb.append(" ").append(words.get(j)).append(" ");
                    pointer++;
                }
                wordList.add(sb.toString());
            } else {
                for (int j = start; j < words.size(); j++)
                    sb.append(" ").append(words.get(j)).append(" ");
                wordList.set(i - 1, wordList.get(i - 1) + sb);
                break;
            }
        }
        return wordList;
    }

    @SuppressWarnings("all")
    private String readFile(String fileName){
        StringBuilder content = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        try(FileChannel fileChannel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE)){
           fileChannel.lock();
            while (fileChannel.read(buffer) != -1){
                buffer.flip();
                content.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
            }
            return content.toString();
        } catch (IOException e) {
            throw new ReadFileException(e.getMessage());
        }
    }

    public CoordinatorIml(int threadCount) {
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.minCharForThreads = 500;
    }

    public CoordinatorIml(int threadCount, int minCharForThreads) {
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.minCharForThreads = minCharForThreads;
    }

    static {
        Coordinator.packageGenerate(PROXY_PACKAGE);
    }
}
