package edu.kpi.nesteruk.pzcs.model.queuing.common;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;

/**
 * Created by Yurii on 2016-04-11.
 */
public class CoherenceComputer {
    public static <N extends Node, L extends Link<N>> int getCoherence(N node, Collection<L> allLinks) {
        return (int) allLinks.stream().filter(link -> link.isIncident(node)).count();
    }
}
