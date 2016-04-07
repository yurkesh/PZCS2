package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-31.
 */
public class GraphLengthsComputher {

    public static <N extends Node> PathLength getGraphLengths(List<Pair<List<N>, PathLength>> pathsWithLengths) {
        return new PathLength(
                getMaxLength(pathsWithLengths, PathLength::getInWeight),
                getMaxLength(pathsWithLengths, PathLength::getInNumberOfNodes)
        );
    }

    /**
     *
     * @param pathsWithLengths
     * @param lengthExtractor specifies the param to compare - length in weight or in nodes number
     * @return max length of all paths by weight or by count
     */
    private static <N extends Node> Integer getMaxLength(List<Pair<List<N>, PathLength>> pathsWithLengths, Function<PathLength, Integer> lengthExtractor) {
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
