package edu.kpi.nesteruk.pzcs.model.queuing.common;


import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yurii on 2016-03-20.
 */
public class GetVerticesWithoutIncomingEdges {

    public static <N extends Node, L extends Link<N>> Collection<N> getFirstTopologicalLevel(Map<String, N> nodesMap, Map<String, L> linksMap) {
        Set<N> nodesWithoutInput = new LinkedHashSet<>(nodesMap.values());
        for (L link : linksMap.values()) {
            nodesWithoutInput.remove(link.getSecond());
        }
        return nodesWithoutInput;
    }
}
