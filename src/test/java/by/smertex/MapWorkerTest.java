package by.smertex;

import by.smertex.worker.realisation.MapWorker;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class MapWorkerTest {

    private static final String CONTENT_TEST =
            """
            Данная задача с повышенной сложностью, её рекомендуется выполнять после более простых задач.
            
            Необходимо разработать программу, которая будет имитировать MapReduce фреймворк. В реальности в MapReduce используются независимые ноды, которые общаются между собой по сети и имеют общее хранилище, у нас же это будут отдельные потоки. В нашей программе будет несколько потоков, которые работают одновременно: один Coordinator и один или несколько Worker’ов. Задача координатора - это раздача задач для воркеров. Задачи могу быть двух видов - это map задача, которая производит набор key value пар из входящего файла и записывает их в промежуточные файлы, и reduce задачи, которые производят финальные файлы с обработанной и отсортированной в алфавитном порядке информацией. При этом для распаралелливания обработки у нас может быть m reduce задач.
            Рассмотрим пример. Нам необходимо посчитать количество разных слов в n файлах.
            На вход в нашу программу подаются эти файлы. Далее запускается какое-то количество воркеров (количество может настраиваться отдельным параметром).
            """;

    private static final String PACKAGE_TEST = "test";

    private static final UUID taskId = UUID.fromString("e8b76b3f-4991-4ea8-a901-6c8665808c07");

    @Test
    void mapWorkerTest() throws IOException {
        MapWorker mapWorker = new MapWorker(taskId, CONTENT_TEST, PACKAGE_TEST);
        Path directory = Path.of(PACKAGE_TEST + "/" + taskId + "/");
        Files.createDirectories(directory);
        mapWorker.run();
    }
}
