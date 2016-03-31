package edu.kpi.nesteruk.pzcs.model.queuing.concrete2;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.DefaultPathsConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.NodesQueue;
import edu.kpi.nesteruk.pzcs.model.queuing.common.PathLengthsComputer;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathByTimeAndNumberOfNodes2<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    private final PathLengthsComputer<N, L> lengthComputer;

    public CriticalPathByTimeAndNumberOfNodes2(boolean considerLinksWeights) {
        this.lengthComputer = new PathLengthsComputer<>(considerLinksWeights);
    }

    @Override
    public Pair<String, Collection<NodesQueue<N>>> constructQueue(GraphModelBundle<N, L> graphModelBundle) {
        DefaultPathsConstructor<N, L> pathsConstructor = new DefaultPathsConstructor<>(graphModelBundle);

        Collection<List<N>> allPaths = pathsConstructor.getAllPaths();
        Collection<L> allLinks = graphModelBundle.getLinksMap().values();

        List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths = allPaths.stream()
                //Make pair: path & lengths of this path
                .map(path -> Pair.create(path, lengthComputer.getLengths(path, allLinks)))
                .collect(Collectors.toList());

        return null;
    }
}
