package by.smertex.worker.realisation;

import by.smertex.worker.exception.WriteToFileException;
import by.smertex.worker.interfaces.Worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapWorker implements Worker {

    private static final String CLEAN = "[^a-zA-Zа-яА-Я0-9\\s]";

    private UUID taskId;

    private String content;

    private String endPath;

    private final Map<String, Integer> wordMap;

    @Override
    public void run() {
        writeMapInFile();
    }

    private void cleanContent() {
        content = content.replaceAll(CLEAN, "");
    }

    private String[] lineSeparator(){
        return content.split("\\s+");
    }

    private void mapFeeling(){
        cleanContent();
        String[] words = lineSeparator();
        for(String word : words){
            if(!word.isEmpty()) wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
        }
    }

    private void writeMapInFile(){
        mapFeeling();
        wordMap.keySet().forEach(word -> writeWordInMap(word, wordMap.get(word)));
        wordMap.clear();
    }

    @SuppressWarnings("all")
    private void writeWordInMap(String word, Integer count){
        Path file = Path.of(endPath + "/" + taskId.toString() + "/" + hashCodeGenerate(word) + ".txt");
        try(FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
            FileLock lock = null;
            while (lock == null){
                try {
                    lock = fileChannel.tryLock();
                } catch (OverlappingFileLockException e) {
                    Thread.sleep(50);
                }
            }
            fileChannel.write(ByteBuffer.wrap((word + ":" + count.toString() + "\n").getBytes()));
        } catch (IOException | InterruptedException e) {
            throw new WriteToFileException(e.getMessage());
        }
    }

    private int hashCodeGenerate(String word){
        int code = word.hashCode();
        return code < 0 ? -code : code;
    }

    @Override
    public void changeSetting(UUID taskId, String content) {
        this.taskId = taskId;
        this.content = content;
    }

    @Override
    public void setEndPath(String endPath) {
        this.endPath = endPath;
    }

    public MapWorker(UUID taskId, String content, String endPath) {
        this.taskId = taskId;
        this.content = content;
        this.endPath = endPath;
        this.wordMap = new HashMap<>();
    }
}
