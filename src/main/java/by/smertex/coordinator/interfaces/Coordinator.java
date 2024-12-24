package by.smertex.coordinator.interfaces;

import by.smertex.coordinator.exception.DirectoryGenerateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public interface Coordinator {
    UUID map(String fileName);

    void reduce(UUID processId, String finalFile);

    static void packageGenerate(String directory){
        try {
            Files.createDirectories(Path.of(directory));
        } catch (IOException e) {
            throw new DirectoryGenerateException(e.getMessage());
        }
    }

    static void packageForTaskGenerate(String directory, UUID processId){
        try {
            Files.createDirectories(Path.of(directory + "/" + processId.toString()));
        } catch (IOException e) {
            throw new DirectoryGenerateException(e.getMessage());
        }
    }
}
