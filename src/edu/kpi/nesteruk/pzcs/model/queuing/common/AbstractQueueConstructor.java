package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;

import java.util.Collection;
import java.util.List;

/**
 * Created by Anatolii on 2016-03-31.
 */
public abstract class AbstractQueueConstructor<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    protected final PathLengthsComputer<N, L> lengthComputer;
    private final String title;

    public AbstractQueueConstructor(PathLengthsComputer<N, L> lengthComputer, String title) {
        this.lengthComputer = lengthComputer;
        this.title = title;
    }

    public AbstractQueueConstructor(boolean considerLinksWeights, String title) {
        this(new PathLengthsComputer<>(considerLinksWeights), title);
    }

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

        return Pair.create(title, constructQueues(allNodes, allLinks, allPaths, graphPathsData));
    }

    protected abstract List<CriticalNode<N>> constructQueues(Collection<N> allNodes, Collection<L> allLinks, Collection<List<N>> allPaths, GraphPathsData<N> graphPathsData);

}
