package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class GraphPathsData<N extends Node, L extends Link<N>> {

    public final List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths;
    public final Tuple<Integer> graphLengths;

    public GraphPathsData(List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths, Tuple<Integer> graphLengths) {
        this.pathsWithLengths = pathsWithLengths;
        this.graphLengths = graphLengths;
    }

    public static <N extends Node, L extends Link<N>> GraphPathsData<N, L> compute(PathLengthsComputer<N, L> lengthsComputer, Collection<List<N>> allPaths, Collection<L> allLinks) {
        List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths = allPaths.stream()
                //Make pair: path & lengths of this path
                .map(path -> Pair.create(path, lengthsComputer.getLengths(path, allLinks)))
                .collect(Collectors.toList());

        Tuple<Integer> graphLengths = GraphLengthsComputher.getGraphLengths(pathsWithLengths);

        return new GraphPathsData<>(pathsWithLengths, graphLengths);
    }

    @Override
    public String toString() {
        return "GraphPathsData{" +
                "pathsWithLengths=" + pathsWithLengths +
                ", graphLengths=" + graphLengths +
                '}';
    }
}
