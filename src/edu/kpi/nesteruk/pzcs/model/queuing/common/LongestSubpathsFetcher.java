package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class LongestSubpathsFetcher {

    public static <N extends Node> BiFunction<List<N>, Integer, List<N>> getSubpathFetcher(boolean beginningFromNode) {
        return beginningFromNode ?
                (path,indexOfNode) -> path.subList(indexOfNode,path.size())
                : (path,indexOfNode) -> path.subList(0, indexOfNode);
    }

    public static <N extends Node, L extends Link<N>> List<Pair<List<N>, PathLength>> getLongestSubpathsForEachNode(
            Collection<N> allNodes,
            Collection<L> allLinks,
            List<Pair<List<N>, PathLength>> pathsLengths,
            PathLengthsComputer<N, L> lengthsComputer,
            boolean extractSubpathBeginningFromNode) {
        return getLongestSubpathsForEachNode(
                allNodes,
                allLinks,
                pathsLengths,
                lengthsComputer,
                getSubpathFetcher(extractSubpathBeginningFromNode),
                !extractSubpathBeginningFromNode
        );
    }

    private static <N extends Node, L extends Link<N>> List<Pair<List<N>, PathLength>> getLongestSubpathsForEachNode(
            Collection<N> allNodes,
            Collection<L> allLinks,
            List<Pair<List<N>, PathLength>> pathsLengths,
            PathLengthsComputer<N, L> lengthsComputer,
            BiFunction<List<N>, Integer, List<N>> subpathFetcher,
            boolean addNodeToPath) {

        return allNodes.stream()
                //Get longest sub-path for each node
                .map(node ->
                        pathsLengths.stream()
                                //For each pair {path, length} make pair {{path, length}, index_of_node}
                                .map(pair -> {
                                    List<N> path = pair.getFirst();
                                    int indexOfNode = path.indexOf(node);
                                    return Pair.create(pair, indexOfNode);
                                })
                                //Pass only paths where node is present
                                .filter(pair -> pair.getSecond() >= 0)
                                //From pair {{path, length}, index_of_node} make pair {subpath, length_of_subpath}
                                .map(pair -> {
                                    List<N> path = pair.getFirst().getFirst();
                                    int indexOfNode = pair.getSecond();
                                    //Get subpath from/to current node (depending on extractSubpathBeginningFromNode param)
                                    List<N> subPath = subpathFetcher.apply(path, indexOfNode);
                                    PathLength lengthsOfSubPath = lengthsComputer.getLengths(subPath, allLinks);
                                    if(addNodeToPath) {
                                        subPath = new ArrayList<>(subPath);
                                        subPath.add(node);
                                    }
                                    return Pair.create(subPath, lengthsOfSubPath);
                                })
                                //Get max by length of subpath
                                .max(Comparator.comparing(pair -> pair.getSecond().getInWeight()))
                                .get()
                ).collect(Collectors.toList());
    }

}
