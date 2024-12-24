package by.smertex.worker.realisation;

import by.smertex.reduce.exeception.ReadFileException;
import by.smertex.worker.exception.WriteToFileException;
import by.smertex.worker.interfaces.ReduceWorker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ReduceWorkerImpl implements ReduceWorker {

    private List<String> rootPaths;

    private String endFile;

    @Override
    public void changeSetting(List<String> rootPaths, String endFile) {
        this.rootPaths = rootPaths;
        this.endFile = endFile;
    }

    @Override
    public void run() {
        fileWriter(endFile);
    }

    @SuppressWarnings("all")
    private void fileWriter(String endFile) {
        try(FileChannel outputChannel = FileChannel.open(Path.of(endFile), StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            for (String root: rootPaths) {
                String data = fileReader(root) + "\n";
                FileLock lock = null;
                while (lock == null){
                    try {
                        lock = outputChannel.tryLock();
                    } catch (OverlappingFileLockException e) {
                        Thread.sleep(50);
                    }
                }
                outputChannel.write(ByteBuffer.wrap(data.getBytes()));
                lock.release();
            }
        } catch (IOException | InterruptedException e){
            throw new WriteToFileException(e.getMessage());
        }
    }

    private String fileReader(String root){
        try(FileChannel inputChannel = FileChannel.open(Path.of(root), StandardOpenOption.READ)){
            return readFile(inputChannel).toString();
        } catch (IOException e) {
            throw new ReadFileException(e.getMessage());
        }
    }

    private KeyValue readFile(FileChannel inputChannel){
        StringBuilder sb = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        try {
            while (inputChannel.read(buffer) != -1){
                buffer.flip();
                sb.append(new String(buffer.array(), 0, buffer.limit()));
                buffer.clear();
            }
            String[] lines = sb.toString().split("\n");
            String key = lines[0].split(":")[0];
            int count = 0;
            for (String line : lines) {
                String[] kv = line.split(":");
                if(kv.length == 2)
                    count += Integer.parseInt(kv[1]);
            }
            return new KeyValue(key, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReduceWorkerImpl(List<String> rootPaths, String endFile) {
        this.rootPaths = rootPaths;
        this.endFile = endFile;
    }

    final static class KeyValue{
        private final String key;
        private final int value;

        public KeyValue(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + ":" + value;
        }
    }
}
