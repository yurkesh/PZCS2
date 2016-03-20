package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.graph.GraphData;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

            @Override
            public String getEdgeTarget(String linkId) {
                L link = checkNotNull(linksMap.get(checkId(linkId)), linkId);
                return link.getSecond().getId();
            }

            /*
            @Override
            public Collection<String> getTargetedVertices(String nodeId) {
                N node = checkNotNull(nodesMap.get(checkId(nodeId)), nodeId);
                return linksMap.entrySet().stream()
                        // Only edges with specified node as target
                        .filter(entry -> node.equals(entry.getValue().getSecond()))
                        // Get id of edge
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
            }
            */

            @Override
            public Map<String, String> getTargetedVerticesWithEdges(String nodeId) {
                N node = checkNotNull(nodesMap.get(checkId(nodeId)), nodeId);
                return linksMap.entrySet().stream()
                        // Only edges with specified node as source
                        .filter(entry -> node.equals(entry.getValue().getFirst()))
                        .collect(Collectors.toMap(
                                // Get id of edge
                                Map.Entry::getKey,
                                // Get id of targeted vertex
                                entry -> entry.getValue().getSecond().getId()
                        ));
            }

            protected <T> T checkNotNull(T o, String id) {
                if(o == null) {
                    throw new NullPointerException("Id = '" + id + "'");
                }
                return o;
            }

            protected String checkId(String id) {
                if(StringUtils.isEmpty(id)) {
                    throw new IllegalArgumentException();
                }
                return id;
            }
        };
    }

}
