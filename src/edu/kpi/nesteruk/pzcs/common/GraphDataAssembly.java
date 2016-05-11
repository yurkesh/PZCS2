package edu.kpi.nesteruk.pzcs.common;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;

import java.util.Collection;

/**
 * Created by Yurii on 2016-03-24.
 */
public class GraphDataAssembly {

    public final Collection<IdAndValue> nodes;
    /**
     * links:[{{sourceId, destinationId}, link:idAndValue}]
     */
    public final Collection<Pair<Pair<String, String>, IdAndValue>> links;

    /**
     *
     * @param nodes
     * @param links [{{sourceId, destinationId}, link:idAndValue}]
     */
    public GraphDataAssembly(Collection<IdAndValue> nodes, Collection<Pair<Pair<String, String>, IdAndValue>> links) {
        this.nodes = nodes;
        this.links = links;
    }

    public Collection<IdAndValue> getNodes() {
        return nodes;
    }

    public Collection<Pair<Pair<String, String>, IdAndValue>> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return "GraphDataAssembly{" +
                "nodes=" + nodes +
                ", links=" + links +
                '}';
    }
}
