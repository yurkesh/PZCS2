package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.GeneralQueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.GraphPathsData;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-03-31.
 */
public class CriticalPathByTimeForAllNodes3 <N extends Node, L extends Link<N>> extends GeneralQueueConstructor<N, L> {

    public CriticalPathByTimeForAllNodes3() {
        super(false, "Critical path by time for all nodes (#3)");
    }

    @Override
    protected List<CriticalNode<N>> constructQueues(GraphPathsData<N> graphPathsData) {
        return graphPathsData.pathsWithLengths.stream()
                //Convert each pair {path, lengthOfPath} to pair {path, lengthOfPath_inWeight}
                .map(pathWithLength -> Pair.create(pathWithLength.first, pathWithLength.getSecond().getInWeight()))
                //Sort by weight
                .sorted(Comparator.<Pair<List<N>, Integer>, Integer>comparing(Pair::getSecond).reversed())
                //Convert to queue(path, weightOfPath}
                .map(CriticalNode::makeWithDouble)
                .collect(Collectors.toList());
    }
}
