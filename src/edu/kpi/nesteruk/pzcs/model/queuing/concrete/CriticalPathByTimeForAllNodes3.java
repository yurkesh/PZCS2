package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.AbstractQueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.GraphPathsData;
import edu.kpi.nesteruk.pzcs.model.queuing.common.NodesQueue;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathByTimeForAllNodes3 <N extends Node, L extends Link<N>> extends AbstractQueueConstructor<N, L> {

    public CriticalPathByTimeForAllNodes3() {
        super(false, "Critical path by time for all nodes (#3)");
    }

    @Override
    protected Collection<NodesQueue<N>> constructQueues(Collection<List<N>> allPaths, Collection<L> allLinks, GraphPathsData<N, L> graphPathsData) {
        return graphPathsData.pathsWithLengths.stream()
                .map(pair -> Pair.create(pair.first, pair.getSecond().getFirst()))
                .sorted(Comparator.<Pair<List<N>, Integer>, Integer>comparing(Pair::getSecond).reversed())
                .map(pair -> new NodesQueue<>(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
    }
}
