package edu.kpi.nesteruk.pzcs.model.queuing.concrete2;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.AbstractQueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.CoherenceComputer;
import edu.kpi.nesteruk.pzcs.model.queuing.common.GraphPathsData;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-04-07.
 */
public class CriticalPathByNumberOfNodesAndCoherence10<N extends Node, L extends Link<N>> extends AbstractQueueConstructor<N, L> {

    public CriticalPathByNumberOfNodesAndCoherence10() {
        super(false, "№10 - Критичний шлях по кількості вершин та їх зв'язність");
    }

    @Override
    protected List<CriticalNode<N>> constructQueues(Collection<N> allNodes, Collection<L> allLinks, Collection<List<N>> allPaths, GraphPathsData<N> graphPathsData) {
        return graphPathsData.pathsWithLengths.stream()
                //Map pair {path, pathLength} to pair {{path, pathLength}, coherence}
                .map(pathWithLengthPair -> Pair.create(
                        pathWithLengthPair,
                        CoherenceComputer.getCoherence(CollectionUtils.getFirstOrNull(pathWithLengthPair.first), allLinks)
                ))
                //Sort paths
                .sorted(
                        //Sort by coherence
                        Comparator.<Pair<Pair<List<N>, PathLength>, Integer>, Integer>comparing(Pair::getSecond)
                                //Max first
                                .reversed()
                                //If equal coherence - sort by number of nodes left to end of graph
                                .thenComparing(
                                        Comparator.<Pair<Pair<List<N>, PathLength>, Integer>, Integer>comparing(
                                                pathWithLengthPairWithCoherencePair -> pathWithLengthPairWithCoherencePair.first.second.inNumberOfNodes
                                                //Max number of nodes first
                                        ).reversed()
                                )
                )
                //pair {{path, lengthOfPath}, coherence} -> CriticalNode(firstNodeOfPath, {coherence, lengthOfPathInNumberOfNodes})
                .map(pathWithLengthPairWithCoherencePair -> new CriticalNode<>(
                        CollectionUtils.getFirstOrNull(pathWithLengthPairWithCoherencePair.first.first),
                        new Tuple<>(
                                pathWithLengthPairWithCoherencePair.second,
                                pathWithLengthPairWithCoherencePair.first.second.inNumberOfNodes
                        )
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CriticalPathByNumberOfNodesAndCoherence10";
    }
}
