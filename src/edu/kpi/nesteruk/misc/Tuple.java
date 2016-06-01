package edu.kpi.nesteruk.misc;

import java.io.Serializable;

/**
 * Created by Anatolii on 3/9/2016.
 */
public class Tuple<K> extends Pair<K, K> implements Serializable {

    private static final long serialVersionUID = 1178827501986487743L;

    public Tuple(K first, K second) {
        super(first, second);
    }

    public static <K> Tuple<K> of(K first, K second) {
        return new Tuple<>(first, second);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                "}";
    }
}
