package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.graph.GraphData;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by Yurii on 2016-03-13.
 */
public class GraphDataAdapter<N extends Node, L extends Link<N>> {

    private final Map<String, N> nodesMap;
    private final Map<String, L> linksMap;
    private final BiFunction<String, String, String> linkIdComposer;

    public GraphDataAdapter(Map<String, N> nodesMap, Map<String, L> linksMap, BiFunction<String, String, String> linkIdComposer) {
        this.nodesMap = nodesMap;
        this.linksMap = linksMap;
        this.linkIdComposer = linkIdComposer;
    }

    public GraphData<String, String> getGraphData() {
        return new GraphData<String, String>() {
            @Override
            public boolean containsEdge(String source, String destination) {
                String linkId = linkIdComposer.apply(source, destination);
                return linksMap.containsKey(linkId);
            }

            @Override
            public Set<String> getEdges() {
                return linksMap.keySet();
            }

            @Override
            public Set<String> getVertices() {
                return nodesMap.keySet();
            }
        };
    }

}
