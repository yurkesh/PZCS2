package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-04-11.
 */
public class CriticalPathByNumberOfNodesAndCoherence11<N extends Node, L extends Link<N>> extends AbstractQueueConstructor<N, L> {

    public CriticalPathByNumberOfNodesAndCoherence11() {
        super(false, "Critical path by number of nodes and their coherence (#11)");
    }

    @Override
    protected List<CriticalNode<N>> constructQueues(Collection<N> allNodes, Collection<L> allLinks, Collection<List<N>> allPaths, GraphPathsData<N> graphPathsData) {
        //Get paths and their lengths from beginning of graph to node
        List<Pair<List<N>, PathLength>> pathWithLengthPairs = GraphPathsData.computeLengthsOfPaths(
                lengthComputer, allPaths, allNodes, allLinks, false
        );
        Map<N, Integer> nodeToLengthOfCriticalPathFromBegin = pathWithLengthPairs.stream().collect(Collectors.toMap(
                //Key = first node of path
                pathWithLengthFromBegin -> CollectionUtils.getLastOrNull(pathWithLengthFromBegin.first),
                //Value = length of path in weight
                pathWithLengthFromBegin -> pathWithLengthFromBegin.second.inWeight
        ));

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
                                //If equal coherence - sort by number of nodes left to beginning of graph
                                .thenComparing(
                                        pathWithLengthPairWithCoherencePair ->
                                                nodeToLengthOfCriticalPathFromBegin.get(
                                                        CollectionUtils.getFirstOrNull(
                                                                pathWithLengthPairWithCoherencePair.first.first
                                                        )
                                                )
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
}
