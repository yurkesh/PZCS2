package edu.kpi.nesteruk.pzcs.graph;

import java.util.Set;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface GraphData<Vertex, Edge> {

    boolean containsEdge(Vertex source, Vertex destination);

    Set<Edge> getEdges();

    Set<Vertex> getVertices();


}
