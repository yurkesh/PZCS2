package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;

import java.util.Collection;
import java.util.List;

/**
 * Created by Anatolii on 2016-04-07.
 */
public abstract class GeneralQueueConstructor<N extends Node, L extends Link<N>> extends AbstractQueueConstructor<N, L> {
    public GeneralQueueConstructor(PathLengthsComputer<N, L> lengthComputer, String title) {
        super(lengthComputer, title);
    }

    public GeneralQueueConstructor(boolean considerLinksWeights, String title) {
        super(considerLinksWeights, title);
    }

    @Override
    protected List<CriticalNode<N>> constructQueues(Collection<N> allNodes, Collection<L> allLinks, Collection<List<N>> allPaths, GraphPathsData<N> graphPathsData) {
        return constructQueues(graphPathsData);
    }

    protected abstract List<CriticalNode<N>> constructQueues(GraphPathsData<N> graphPathsData);
}
