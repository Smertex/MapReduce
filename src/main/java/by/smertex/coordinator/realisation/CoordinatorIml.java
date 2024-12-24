package by.smertex.coordinator.realisation;

import by.smertex.coordinator.exception.CoordinatorException;
import by.smertex.coordinator.interfaces.Coordinator;
import by.smertex.reduce.exeception.ReadFileException;
import by.smertex.worker.realisation.MapWorkerImpl;
import by.smertex.worker.realisation.ReduceWorkerImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoordinatorIml implements Coordinator {

    private static final String PROXY_PACKAGE = "proxy";

    private final ExecutorService executor;

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
        reduceAllocator(processId, finalFile);
    }

    private void reduceAllocator(UUID processId, String finalFile){
        List<List<String>> listsForThread = reduceSpliterator(PROXY_PACKAGE + "/" + processId);
        CountDownLatch countDownLatch = new CountDownLatch(listsForThread.size());
        listsForThread.forEach(list -> {
            executor.execute(() -> {
                try{
                    new ReduceWorkerImpl(list, finalFile).run();
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new CoordinatorException(e.getMessage());
        }
    }

    private List<List<String>> reduceSpliterator(String path){
        List<List<String>> listsForThread = new ArrayList<>();
        try(Stream<Path> stream = Files.walk(Path.of(path))) {
            List<String> files = stream.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            int pointer = 0;
            int count = files.size() / threadCount;
            for(int i = 0; i < threadCount; i++) {
                int start = pointer;
                List<String> list = new ArrayList<>();
                for (int j = start; j < count + start; j++, pointer++) {
                    list.add(files.get(j));
                }
                listsForThread.add(list);
            }
            return listsForThread;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mapAllocator(UUID taskId, List<String> words) {
        CountDownLatch countDownLatch = new CountDownLatch(words.size());
        words.forEach(word -> executor.execute(() -> {
                try {
                    new MapWorkerImpl(taskId, word, PROXY_PACKAGE).run();
                } finally {
                    countDownLatch.countDown();
                }
            }));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new CoordinatorException(e.getMessage());
        }
    }

    private List<String> contentSpliterator(String fileName){
        List<String> wordList = new ArrayList<>();
        List<String> words = List.of(readFile(fileName).split(" "));

        int pointer = 0;
        for (int i = 0; i < threadCount; i++) {
            StringBuilder sb = new StringBuilder();
            int start = pointer;
            int count = words.size() / threadCount;
            if(words.size() - pointer > minCharForThreads) {
                for (int j = start; j < count + start; j++) {
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
