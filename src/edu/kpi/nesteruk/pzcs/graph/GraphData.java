package edu.kpi.nesteruk.pzcs.graph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphData<Vertex, Edge> {

    boolean containsEdge(Vertex source, Vertex destination);

    Set<Edge> getEdges();

    Set<Vertex> getVertices();

    Vertex getEdgeTarget(Edge edge);

    /*
    Collection<Vertex> getTargetedVertices(Vertex vertex);
    */

    /**
     *
     * @param vertex
     * @return {connecting edge -> targeted vertex}
     */
    Map<Edge, Vertex> getTargetedVerticesWithEdges(Vertex vertex);
}
