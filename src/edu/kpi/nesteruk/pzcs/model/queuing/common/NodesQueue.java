package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.List;

/**
 * Created by Yurii on 2016-03-31.
 */
@Deprecated
public class NodesQueue<N extends Node> {
    public final double value;
    public final List<N> path;

    public NodesQueue(List<N> path, double value) {
        this.path = path;
        this.value = value;
    }

    public NodesQueue(Pair<List<N>, ? extends Number> pathWithLength) {
        this(pathWithLength.first, pathWithLength.second.doubleValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodesQueue<?> that = (NodesQueue<?>) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NodesQueue{" +
                "value=" + value +
                ", path=" + path +
                '}';
    }
}
