package by.smertex;

import by.smertex.reduce.interfaces.MapReduce;
import by.smertex.reduce.realisation.MapReduceImpl;

public class MapReduceTest {

    private static final String START_FILE_TEST = "start.txt";

    private static final String END_FILE_TEST = "end.txt";

    void reduce() {
        MapReduce mapReduce = new MapReduceImpl();
        assert mapReduce.mapReduce(START_FILE_TEST, END_FILE_TEST) == null;
    }

}
