package edu.kpi.nesteruk.pzcs.model.queuing.concrete2;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
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
public class CriticalPathByTimeAndNumberOfNodes2<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    private static final String TITLE = "Критичний шлях по часу і кількості вершин";
    private final PathLengthsComputer<N, L> lengthComputer = new PathLengthsComputer<>(false);

    @Override
    public Pair<String, List<CriticalNode<N>>> constructQueues(GraphModelBundle<N, L> graphModelBundle) {
        DefaultPathsConstructor<N, L> pathsConstructor = new DefaultPathsConstructor<>(graphModelBundle);

        Collection<List<N>> allPaths = pathsConstructor.getAllPaths();

        Collection<L> allLinks = graphModelBundle.getLinksMap().values();
        Collection<N> allNodes = graphModelBundle.getNodesMap().values();
        GraphPathsData<N> graphPathsData = GraphPathsData.compute(
                lengthComputer,
                allPaths,
                allNodes,
                allLinks
        );

        Map<N, Integer> nodeToLengthOfCriticalPathFromBegin = GraphPathsData.computeLengthsOfPaths(
                lengthComputer, allPaths, allNodes, allLinks, false
        ).stream().collect(Collectors.toMap(
                pathWithLengthFromBegin -> CollectionUtils.getLastOrNull(pathWithLengthFromBegin.first),
                pathWithLengthFromBegin -> pathWithLengthFromBegin.second.inWeight
        ));

        List<CriticalNode<N>> computedPaths = graphPathsData.pathsWithLengths.stream()
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
                .map(CriticalNode::new)
                .collect(Collectors.toList());

        return Pair.create(TITLE, computedPaths);
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
}
