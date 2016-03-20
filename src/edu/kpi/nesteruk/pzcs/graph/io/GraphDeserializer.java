package edu.kpi.nesteruk.pzcs.graph.io;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-21.
 */
public class GraphDeserializer<N extends Node, L extends Link<N>> implements IOConst {

    private final String type;
    private final Function<N, String> nodeDeserializer;
    private final Function<L, String> linkDeserializer;

    public GraphDeserializer(String type, Function<N, String> nodeDeserializer, Function<L, String> linkDeserializer) {
        this.type = type;
        this.nodeDeserializer = nodeDeserializer;
        this.linkDeserializer = linkDeserializer;
    }

    public String serializeGraph(Collection<N> nodes, Collection<L> links) {
        StringBuilder sb = new StringBuilder();
        sb.append(TYPE_INFO).append(":").append(type).append("\n");
        sb.append(VERSION_INFO).append(":").append(VERSION_VALUE).append("\n");
        sb.append(NODES_INFO).append(":").append(nodes.size()).append("\n");
        nodes.forEach(node -> sb.append(nodeDeserializer.apply(node)).append("\n"));
        sb.append(LINKS_INFO).append(":").append(links.size()).append("\n");
        links.forEach(link -> sb.append(linkDeserializer.apply(link)).append("\n"));
        return sb.toString();
    }

}
