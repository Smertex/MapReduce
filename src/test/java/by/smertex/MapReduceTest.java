package by.smertex;

import by.smertex.reduce.interfaces.MapReduce;
import by.smertex.reduce.realisation.MapReduceImpl;
import org.junit.jupiter.api.Test;

public class MapReduceTest {

    private static final String START_FILE_TEST = "start.txt";

    private static final String END_FILE_TEST = "proxy/end.txt";

    @Test
    void reduce() {
        MapReduce mapReduce = new MapReduceImpl();
        System.out.println(mapReduce.mapReduce(START_FILE_TEST, END_FILE_TEST));
    }

    @Test
    void customPoolTest() {
        MapReduce mapReduce = new MapReduceImpl(5);
        System.out.println(mapReduce.mapReduce(START_FILE_TEST, END_FILE_TEST));
    }

}
