package edu.kpi.nesteruk.pzcs.model.queuing.concrete2;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-04-07.
 */
public class CriticalPathByWeightOfNodes14<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    public static final String TITLE = "№14 - Вага вершин";

    @Override
    public Pair<String, List<CriticalNode<N>>> constructQueues(GraphModelBundle<N, L> graphModelBundle) {
        return Pair.create(
                TITLE,
                //Get all nodes
                graphModelBundle.getNodesMap().values().stream()
                        //Sort nodes by weight - max first
                        .sorted(Comparator.comparing(Node::getWeight).reversed())
                        //Make CriticalNode from each node
                        .map(node -> new CriticalNode<>(node, node.getWeight()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return "CriticalPathByWeightOfNodes14";
    }
}
