package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class LongestSubpathsFetcher<N extends Node, L extends Link<N>> {

    public static <N extends Node, L extends Link<N>> List<Pair<List<N>, Tuple<Integer>>> getLongestSubpaths(
            Collection<N> allNodes,
            Collection<L> allLinks,
            List<Pair<List<N>, Tuple<Integer>>> pathsLengths,
            PathLengthsComputer<N, L> lengthsComputer) {

        return allNodes.stream().map(node -> pathsLengths.stream()
                        .map(pair -> {
                            List<N> path = pair.getFirst();
                            int indexOfNode = path.indexOf(node);
                            return Pair.create(pair, indexOfNode);
                        })
                        .filter(pair -> pair.getSecond() >= 0)
                        .map(pair -> {
                            List<N> path = pair.getFirst().getFirst();
                            int indexOfNode = pair.getSecond();
                            List<N> subPath = path.subList(indexOfNode, path.size());
                            Tuple<Integer> lengthsOfSubPath = lengthsComputer.getLengths(subPath, allLinks);
                            return Pair.create(subPath, lengthsOfSubPath);
                        })
                        .max(Comparator.comparing(pair -> pair.getSecond().getFirst()))
                        .get()
        ).collect(Collectors.toList());
    }

}
