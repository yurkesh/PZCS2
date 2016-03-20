package edu.kpi.nesteruk.pzcs.graph.actions;

import edu.kpi.nesteruk.pzcs.graph.GraphData;
import edu.kpi.nesteruk.util.CollectionUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-20.
 * <br/>See <a href="https://en.wikipedia.org/wiki/Topological_sorting">Topological sorting</a> on Wikipedia
 */
public class TopologicalSort<Vertex, Edge> implements Function<GraphData<Vertex, Edge>, Optional<List<List<Vertex>>>> {

    @Override
    public Optional<List<List<Vertex>>> apply(GraphData<Vertex, Edge> graphData) {
        return null;
    }

    public static <Vertex, Edge> Optional<List<Vertex>> sortTopologically(GraphData<Vertex, Edge> graph) {
        try {
            TopologicalOrderIterator<Vertex, Edge> toi = new TopologicalOrderIterator<>((DirectedGraph<Vertex, Edge>) graph);
            List<Vertex> vertices = new ArrayList<>();
            toi.forEachRemaining(vertices::add);
            return Optional.of(vertices);
        } catch (Exception e) {
            System.err.println("Cannot sort graph topologically. Exception = " + e);
            return Optional.empty();
        }
    }
}
