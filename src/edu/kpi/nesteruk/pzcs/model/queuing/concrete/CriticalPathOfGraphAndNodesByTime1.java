package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.*;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathOfGraphAndNodesByTime1<N extends Node, L extends Link<N>> extends GeneralQueueConstructor<N, L> {

    public CriticalPathOfGraphAndNodesByTime1() {
        super(false, "Critical path of graph and nodes by time (#1)");
    }

    @Override
    protected List<CriticalNode<N>> constructQueues(GraphPathsData<N> graphPathsData) {
        final List<Pair<List<N>, PathLength>> pathsWithLengths = graphPathsData.pathsWithLengths;
        final PathLength graphLengths = graphPathsData.graphLengths;

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
                //Make Critical from each pair of {listOfNodes, relativeLength}
                .map(CriticalNode::makeWithDouble)
                .collect(Collectors.toList());
    }

    /**
     * Relative (normalized) length of path = Tcr_p / Tgr + Ncr_p / Ngr. See examples from lecture
     * @param pathLengths
     * @param graphLengths
     * @return relative length of path.
     */
    private Double getRelativeLength(PathLength pathLengths, PathLength graphLengths) {
        return 1.0 * pathLengths.inWeight / graphLengths.inWeight + 1.0 * pathLengths.inNumberOfNodes / graphLengths.inNumberOfNodes;
    }

    @Override
    public String toString() {
        return "CriticalPathOfGraphAndNodesByTime1";
    }
}
