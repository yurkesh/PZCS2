package edu.kpi.nesteruk.pzcs.model.queuing.primitives;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.List;

/**
 * Created by Yurii on 2016-04-07.
 */
public class CriticalNode<N extends Node> {
    public final N node;
    public final double value;

    public CriticalNode(N node, double value) {
        this.node = node;
        this.value = value;
    }

    public CriticalNode(Pair<List<N>, ? extends Number> pathWithLength) {
        this(pathWithLength.first.stream().findFirst().get(), pathWithLength.second.doubleValue());
    }

    @Override
    public String toString() {
        return "CriticalNode{" +
                "node=" + node +
                ", value=" + value +
                '}';
    }
}
