package edu.kpi.nesteruk.pzcs.graph.misc;

import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import org.jgrapht.Graph;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-04-13.
 */
public class GraphUtils {
    public static <V, E, G extends Graph<V, E>> Graph<V, E> cloneGraph(Graph<V, E> srcGraph, G destGraph) {
        srcGraph.vertexSet().forEach(destGraph::addVertex);
        srcGraph.edgeSet().forEach(
                edge -> destGraph.addEdge(srcGraph.getEdgeSource(edge), srcGraph.getEdgeTarget(edge), edge)
        );
        return destGraph;
    }

    public static <N extends Node, L extends Link<N>> Graph<String, String> makeGraphCheckAllEdgesAdded(
            Supplier<Graph<String, String>> graphFactory,
            Collection<N> nodes,
            Collection<L> links) {

        Graph<String, String> graph = graphFactory.get();
        nodes.forEach(node -> graph.addVertex(node.getId()));
        links.forEach(link -> {
            String insertedEdge = graph.addEdge(link.getFirst().getId(), link.getSecond().getId());
            if(insertedEdge == null) {
                throw new IllegalArgumentException("Cannot insert link = '" + link + "'");
            }
        });
        return graph;
    }
}