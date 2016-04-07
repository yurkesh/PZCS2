package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.PathLength;

import java.util.Collection;
import java.util.List;

/**
 * Created by Yurii on 2016-03-31.
 */
public class PathLengthsComputer<N extends Node, L extends Link<N>> {

    private final boolean considerLinksWeights;

    public PathLengthsComputer(boolean considerLinksWeights) {
        this.considerLinksWeights = considerLinksWeights;
    }

    /**
     * This is pair of {Tcr, Ncr} from examples. See examples from lecture
     * @param path
     * @return {lengthInWeight, lengthInNodesNumber} == {Tcr_p, Ncr_p}
     */
    public PathLength getLengths(List<N> path, Collection<L> allLinks) {
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
        return new PathLength(sum, path.size());
    }

    private L getLinkBetween(N src, N dest, Collection<L> links) {
        return links.stream().filter(link -> link.getFirst().equals(src) && link.getSecond().equals(dest)).findFirst().get();
    }
}
