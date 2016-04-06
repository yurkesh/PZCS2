package edu.kpi.nesteruk.pzcs.model.queuing.concrete2;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathByTimeAndNumberOfNodes2<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    private final PathLengthsComputer<N, L> lengthComputer;

    public CriticalPathByTimeAndNumberOfNodes2(boolean considerLinksWeights) {
        this.lengthComputer = new PathLengthsComputer<>(considerLinksWeights);
    }

    @Override
    public Pair<String, Collection<NodesQueue<N>>> constructQueues(GraphModelBundle<N, L> graphModelBundle) {
        DefaultPathsConstructor<N, L> pathsConstructor = new DefaultPathsConstructor<>(graphModelBundle);

        Collection<List<N>> allPaths = pathsConstructor.getAllPaths();

        GraphPathsData<N, L> graphPathsData = GraphPathsData.compute(
                lengthComputer,
                allPaths,
                graphModelBundle.getNodesMap().values(),
                graphModelBundle.getLinksMap().values()
        );
        final List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths = graphPathsData.pathsWithLengths;
        final Tuple<Integer> graphLengths = graphPathsData.graphLengths;



        return null;
    }
}
