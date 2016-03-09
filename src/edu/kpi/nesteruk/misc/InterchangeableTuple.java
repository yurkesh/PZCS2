package edu.kpi.nesteruk.misc;

import java.util.Objects;

/**
 * Created by Yurii on 2016-03-10.
 */
public class InterchangeableTuple<K> extends Tuple<K> {

    public InterchangeableTuple(K first, K second) {
        super(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?> tuple = (Tuple<?>) o;

        if(Objects.equals(first, tuple.first)) {
            return Objects.equals(second, tuple.second);
        } else if(Objects.equals(first, tuple.second)) {
            return Objects.equals(second, tuple.first);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
