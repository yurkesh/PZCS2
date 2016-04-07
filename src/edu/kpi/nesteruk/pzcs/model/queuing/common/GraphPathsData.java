package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class GraphPathsData<N extends Node> {

    public final List<Pair<List<N>, PathLength>> pathsWithLengths;
    public final PathLength graphLengths;

    public GraphPathsData(List<Pair<List<N>, PathLength>> pathsWithLengths, PathLength graphLengths) {
        this.pathsWithLengths = pathsWithLengths;
        this.graphLengths = graphLengths;
    }

    public static <N extends Node, L extends Link<N>> GraphPathsData<N> compute(
            PathLengthsComputer<N, L> lengthsComputer,
            Collection<List<N>> allPaths,
            Collection<N> allNodes,
            Collection<L> allLinks) {

        List<Pair<List<N>, PathLength>> pathsWithLengths = computeLengthsOfPaths(
                lengthsComputer,
                allPaths,
                allNodes,
                allLinks,
                true
        );

        PathLength graphLengths = GraphLengthsComputher.getGraphLengths(pathsWithLengths);

        return new GraphPathsData<>(pathsWithLengths, graphLengths);
    }

    public static <N extends Node, L extends Link<N>> List<Pair<List<N>, PathLength>> computeLengthsOfPaths(
            PathLengthsComputer<N, L> lengthsComputer,
            Collection<List<N>> allPaths,
            Collection<N> allNodes,
            Collection<L> allLinks,
            boolean extractSubpathBeginningFromNode) {

        List<Pair<List<N>, PathLength>> pathsWithLengths = allPaths.stream()
                //Make pair: path & lengths of this path
                .map(path -> Pair.create(path, lengthsComputer.getLengths(path, allLinks)))
                .collect(Collectors.toList());

        return LongestSubpathsFetcher.getLongestSubpathsForEachNode(
                allNodes,
                allLinks,
                pathsWithLengths,
                lengthsComputer,
                extractSubpathBeginningFromNode
        );
    }

    @Override
    public String toString() {
        return "GraphPathsData{" +
                "pathsWithLengths=" + pathsWithLengths +
                ", graphLengths=" + graphLengths +
                '}';
    }
}
