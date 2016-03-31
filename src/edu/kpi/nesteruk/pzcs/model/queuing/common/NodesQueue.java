package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.List;

/**
 * Created by Yurii on 2016-03-31.
 */
public class NodesQueue<N extends Node> {
    public final double value;
    public final List<N> tasksSequence;

    public NodesQueue(List<N> tasksSequence, double value) {
        this.tasksSequence = tasksSequence;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodesQueue<?> that = (NodesQueue<?>) o;

        return tasksSequence != null ? tasksSequence.equals(that.tasksSequence) : that.tasksSequence == null;

    }

    @Override
    public int hashCode() {
        return tasksSequence != null ? tasksSequence.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NodesQueue{" +
                "value=" + value +
                ", tasksSequence=" + tasksSequence +
                '}';
    }
}
