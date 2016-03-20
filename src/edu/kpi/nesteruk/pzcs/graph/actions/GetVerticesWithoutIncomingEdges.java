package edu.kpi.nesteruk.pzcs.graph.actions;

import edu.kpi.nesteruk.pzcs.graph.GraphData;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-20.
 */
public class GetVerticesWithoutIncomingEdges<Vertex, Edge> implements Function<GraphData<Vertex, Edge>, Collection<Vertex>> {

    @Override
    public Collection<Vertex> apply(GraphData<Vertex, Edge> graphData) {
        return getFirstTopologicalLevel(graphData);
    }

    public static <Vertex, Edge> Collection<Vertex> getFirstTopologicalLevel(GraphData<Vertex, Edge> graph) {
        Set<Edge> edges = graph.getEdges();
        Set<Vertex> vertices = graph.getVertices();
        for (Edge edge : edges) {
            Vertex targetVertex = graph.getEdgeTarget(edge);
            vertices.remove(targetVertex);
        }
        return vertices;
    }
}
