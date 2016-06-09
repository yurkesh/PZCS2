package edu.kpi.nesteruk.pzcs.model.queuing.concrete2;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.*;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathByTimeAndNumberOfNodes2<N extends Node, L extends Link<N>> extends AbstractQueueConstructor<N, L> {

    public CriticalPathByTimeAndNumberOfNodes2() {
        super(false, "№2 - Критичний шлях по часу і кількості вершин");
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
                //Convert pair {path, length} to pathWithAdditionalInfo
                .map(pathWithLengths -> new PathWithAdditionalInfo<>(
                        pathWithLengths.first,
                        pathWithLengths.second,
                        nodeToLengthOfCriticalPathFromBegin.get(CollectionUtils.getFirstOrNull(pathWithLengths.first))
                ))
                //Convert pathWithAdditionalInfo to pair {path, metricValue}
                .map(pathWithAdditionalInfo -> Pair.create(pathWithAdditionalInfo.path, getMetric(pathWithAdditionalInfo, graphPathsData.graphLengths)))
                //Sort by metric
                .sorted(Comparator.comparing(Pair::getSecond))
                //Make queue
                .map(CriticalNode::makeWithDouble)
                .collect(Collectors.toList());
    }

    /**
     * See examples from lecture
     * @param pathWithAdditionalInfo
     * @param graphLength
     * @param <N>
     * @return
     */
    private static <N extends Node> int getMetric(PathWithAdditionalInfo<N> pathWithAdditionalInfo, PathLength graphLength) {
        int p_sr_i = graphLength.inWeight - pathWithAdditionalInfo.length.inWeight;
        int r_sr_i = pathWithAdditionalInfo.lengthOfCriticalPathFromBeginning;
        return p_sr_i - r_sr_i;
    }

    private static class PathWithAdditionalInfo<N extends Node> {
        final List<N> path;
        final PathLength length;
        final int lengthOfCriticalPathFromBeginning;

        private PathWithAdditionalInfo(List<N> path, PathLength length, int lengthOfCriticalPathFromBeginning) {
            this.path = path;
            this.length = length;
            this.lengthOfCriticalPathFromBeginning = lengthOfCriticalPathFromBeginning;
        }

        @Override
        public String toString() {
            return "PathWithAdditionalInfo{" +
                    "path=" + path +
                    ", length=" + length +
                    ", lengthOfCriticalPathFromBeginning=" + lengthOfCriticalPathFromBeginning +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CriticalPathByTimeAndNumberOfNodes2";
    }
}
