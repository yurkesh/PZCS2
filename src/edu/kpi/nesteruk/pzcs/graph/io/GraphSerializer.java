package edu.kpi.nesteruk.pzcs.graph.io;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-21.
 @deprecated Not supported yet
 */
@Deprecated
public class GraphSerializer<N extends Node, L extends Link<N>> implements IOConst {

    private final String type;
    private final Function<N, String> nodeSerializer;
    private final Function<L, String> linkSerializer;

    public GraphSerializer(String type, Function<N, String> nodeSerializer, Function<L, String> linkSerializer) {
        this.type = type;
        this.nodeSerializer = nodeSerializer;
        this.linkSerializer = linkSerializer;
    }

    public String serializeGraph(Collection<N> nodes, Collection<L> links) {
        StringBuilder sb = new StringBuilder();
        sb.append(TYPE_INFO).append(DIVIDER).append(type).append("\n");
        sb.append(VERSION_INFO).append(DIVIDER).append(VERSION_VALUE).append("\n");
        sb.append(NODES_INFO).append(DIVIDER).append(nodes.size()).append("\n");
        nodes.forEach(node -> sb.append(nodeSerializer.apply(node)).append("\n"));
        sb.append(LINKS_INFO).append(DIVIDER).append(links.size()).append("\n");
        links.forEach(link -> sb.append(linkSerializer.apply(link)).append("\n"));
        return sb.toString();
    }

    public static String escape(String src, String esc) {
        return src.replace(esc, "\\" + esc);
    }
}
