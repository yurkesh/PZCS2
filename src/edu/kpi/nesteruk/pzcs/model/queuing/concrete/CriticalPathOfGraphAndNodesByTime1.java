package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathOfGraphAndNodesByTime1<N extends Node, L extends Link<N>> extends AbstractQueueConstructor<N, L> {

    public CriticalPathOfGraphAndNodesByTime1() {
        super(false, "Critical path of graph and nodes by time (#1)");
    }

    @Override
    protected Collection<NodesQueue<N>> constructQueues(Collection<List<N>> allPaths, Collection<L> allLinks, GraphPathsData<N, L> graphPathsData) {
        final List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths = graphPathsData.pathsWithLengths;
        final Tuple<Integer> graphLengths = graphPathsData.graphLengths;

        List<Pair<List<N>, Double>> pathsSortedByLength = pathsWithLengths.stream()
                //Make pair: path & its relative length
                .map(pathWithLengths -> Pair.create(
                        pathWithLengths.first,
                        getRelativeLength(pathWithLengths.second, graphLengths)
                ))
                //Sort paths by comparing their relative length; from biggest to smallest (use reversed comparator)
                .sorted(Comparator.<Pair<List<N>, Double>, Double>comparing(Pair::getSecond).reversed())
                .collect(Collectors.toList());

        return pathsSortedByLength.stream()
                //Make NodesQueue from each pair of {listOfNodes, relativeLength}
                .map(pathWithLength -> new NodesQueue<>(pathWithLength.first, pathWithLength.second))
                .collect(Collectors.toList());
    }

    /**
     * Relative (normalized) length of path = Tcr_p / Tgr + Ncr_p / Ngr. See examples from lecture
     * @param pathLengths
     * @param graphLengths
     * @return relative length of path.
     */
    private Double getRelativeLength(Tuple<Integer> pathLengths, Tuple<Integer> graphLengths) {
        return 1.0 * pathLengths.first / graphLengths.first + 1.0 * pathLengths.second / graphLengths.second;
    }
}
