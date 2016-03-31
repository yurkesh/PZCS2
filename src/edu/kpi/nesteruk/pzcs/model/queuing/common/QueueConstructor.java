package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;

/**
 * Created by Yurii on 2016-03-31.
 */
@FunctionalInterface
public interface QueueConstructor<N extends Node, L extends Link<N>> {
    Pair<String, Collection<NodesQueue<N>>> constructQueue(GraphModelBundle<N, L> graphModelBundle);
}
