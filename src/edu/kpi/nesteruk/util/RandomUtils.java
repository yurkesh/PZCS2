package edu.kpi.nesteruk.util;

import java.util.Random;

/**
 * Created by Yurii on 2016-04-19.
 */
public class RandomUtils {

    public static int randomFromTo(Random random, int from, int to) {
        if(from == to) {
            return from;
        }
        if(from > to) {
            throw new IllegalArgumentException("From(" + from + ") > To(" + to + ")");
        }
        return random.nextInt(to - from) + from;
    }

}
