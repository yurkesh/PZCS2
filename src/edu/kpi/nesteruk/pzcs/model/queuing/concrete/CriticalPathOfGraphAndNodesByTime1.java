package edu.kpi.nesteruk.pzcs.model.queuing.concrete;

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
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-31.
 */
public class CriticalPathOfGraphAndNodesByTime1<N extends Node, L extends Link<N>> implements QueueConstructor<N, L> {

    private final PathLengthsComputer<N, L> lengthComputer;

    public CriticalPathOfGraphAndNodesByTime1(boolean considerLinksWeights) {
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

        Tuple<Integer> graphLengths = getGraphLengths(pathsWithLengths);

        List<Pair<List<N>, Double>> pathsSortedByLength = pathsWithLengths.stream()
                //Make pair: path & its relative length
                .map(pathWithLengths -> Pair.create(
                        pathWithLengths.first,
                        getRelativeLength(pathWithLengths.second, graphLengths)
                ))
                //Sort paths by comparing their relative length; from biggest to smallest (use reversed comparator)
                .sorted(Comparator.<Pair<List<N>, Double>, Double>comparing(Pair::getSecond).reversed())
                .collect(Collectors.toList());

        //TODO need to return all paths sorted by relative (normalized) length from max to min (as specified in lab#1 task)
        return Pair.create(
                "Critical path of graph and nodes by time (#1)",
                pathsSortedByLength.stream()
                        //Make NodesQueue from each pair of {listOfNodes, relativeLength}
                        .map(pathWithLength -> new NodesQueue<>(pathWithLength.first, pathWithLength.second))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Relative (normalized) length of path = Tcr_p / Tgr + Ncr_p / Ngr. See examples from lecture
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
}
