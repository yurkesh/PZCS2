package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-31.
 */
public class GraphLengthsComputher<N extends Node, L extends Link<N>> {

    public Tuple<Integer> getGraphLengths(List<Pair<List<N>, Tuple<Integer>>> pathsWithLengths) {
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
