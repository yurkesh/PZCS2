package edu.kpi.nesteruk.misc;

import java.io.Serializable;

/**
 * Created by Anatolii on 3/9/2016.
 */
public class Tuple<K> implements Serializable {

    private static final long serialVersionUID = 1178827501986487743L;

    public final K first;
    public final K second;

    public Tuple(K first, K second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?> tuple = (Tuple<?>) o;

        if (first != null ? !first.equals(tuple.first) : tuple.first != null) return false;
        return second != null ? second.equals(tuple.second) : tuple.second == null;

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
