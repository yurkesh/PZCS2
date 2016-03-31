package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.common.DefaultPathsConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.common.NodesQueue;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathOfGraphAndNodesByTime1<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    private final boolean considerLinksWeights;

    public CriticalPathOfGraphAndNodesByTime1(boolean considerLinksWeights) {
        this.considerLinksWeights = considerLinksWeights;
    }

    @Override
    public Pair<String, NodesQueue<N>> constructQueue(GraphModelBundle<N, L> graphModelBundle) {
        DefaultPathsConstructor<N, L> pathsConstructor = new DefaultPathsConstructor<>(graphModelBundle);

        Collection<List<N>> allPaths = pathsConstructor.getAllPaths();
        Collection<L> allLinks = graphModelBundle.getLinksMap().values();

        List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths = allPaths.stream()
                //Make pair: path & lengths of this path
                .map(path -> Pair.create(path, getLengths(path, allLinks)))
                .collect(Collectors.toList());

        Tuple<Integer> graphLengths = getGraphLengths(pathsWithLengths);
        Pair<List<N>, Double> longestPath = pathsWithLengths.stream()
                //Make pair: path & its relative length
                .map(pathWithLengths -> Pair.create(
                        pathWithLengths.first,
                        getRelativeLength(pathWithLengths.second, graphLengths)
                ))
                //Find biggest path by comparing its relative length
                .max(Comparator.comparing(Pair::getSecond))
                //Get from Optional
                .get();
        return Pair.create(
                "Critical path of graph and nodes by time (#1)",
                new NodesQueue<>(longestPath.first, longestPath.second)
        );
    }

    /**
     * Relative length of path = Tcr_p / Tgr + Ncr_p / Ngr. See examples from lecture
     * @param pathLengths
     * @param graphLengths
     * @return relative length of path.
     */
    private Double getRelativeLength(Tuple<Integer> pathLengths, Tuple<Integer> graphLengths) {
        return 1.0 * pathLengths.first / graphLengths.first + 1.0 * pathLengths.second / graphLengths.second;
    }

    private Tuple<Integer> getGraphLengths(List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths) {
        return new Tuple<>(
                getMaxLength(pathsWithLengths, Tuple::getFirst),
                getMaxLength(pathsWithLengths, Tuple::getSecond)
        );
    }

    /**
     *
     * @param pathsWithLengths
     * @param lengthExtractor specifies the param to compare - length in weight or in nodes number
     * @return max length of all paths by weight or by count
     */
    private Integer getMaxLength(List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths, Function<Tuple<Integer>, Integer> lengthExtractor) {
        //Get length in total weight or in number of nodes
        return lengthExtractor.apply(
                pathsWithLengths.stream()
                        .max((Comparator.comparing(
                                //Get lengths of path
                                Pair::getSecond,
                                //Get length in total weight or in number of nodes
                                Comparator.comparing(lengthExtractor)
                        )))
                        //Get from Optional
                        .get()
                        //Get max tuple lengths
                        .second
        );
    }

    /**
     * This is pair of {Tcr, Ncr} from examples. See examples from lecture
     * @param path
     * @return {lengthInWeight, lengthInNodesNumber} == {Tcr_p, Ncr_p}
     */
    private Tuple<Integer> getLengths(List<N> path, Collection<L> allLinks) {
        N previousNode = null;
        int sum = 0;
        for (N node : path) {
            if(previousNode != null) {
                if(considerLinksWeights) {
                    sum += getLinkBetween(previousNode, node, allLinks).getWeight();
                }
            }
            previousNode = node;
            sum += node.getWeight();
        }
        return new Tuple<>(sum, path.size());
    }

    private L getLinkBetween(N src, N dest, Collection<L> links) {
        return links.stream().filter(link -> link.getFirst().equals(src) && link.getSecond().equals(dest)).findFirst().get();
    }
}
