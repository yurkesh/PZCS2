package edu.kpi.nesteruk.pzcs.graph.actions;

import edu.kpi.nesteruk.pzcs.graph.GraphData;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-20.
 * <br/>See <a href="https://en.wikipedia.org/wiki/Topological_sorting">Topological sorting</a> on Wikipedia
 */
public class TopologicalSort<Vertex, Edge> implements Function<GraphData<Vertex, Edge>, Optional<List<List<Vertex>>>> {

    @Override
    public Optional<List<List<Vertex>>> apply(GraphData<Vertex, Edge> graphData) {
        return null;
    }

    public static <Vertex, Edge> Optional<List<List<Vertex>>> sortTopologically(GraphData<Vertex, Edge> graph) {
        Collection<Vertex> verticesWithNoInput = GetVerticesWithoutIncomingEdges.getFirstTopologicalLevel(graph);
        if(CollectionUtils.isEmpty(verticesWithNoInput)) {
            return Optional.empty();
        }
        Set<Vertex> noInput = new HashSet<>(verticesWithNoInput);
        LinkedList<Vertex> sorted = new LinkedList<>();
        for (Iterator<Vertex> iterator = noInput.iterator(); iterator.hasNext();) {
            Vertex n = iterator.next();
            iterator.remove();
            sorted.addLast(n);
            Map<Edge, Vertex> targetedVerticesWithEdges = graph.getTargetedVerticesWithEdges(n);
            for (Map.Entry<Edge, Vertex> entry : targetedVerticesWithEdges.entrySet()) {

            }
        }
        while (!CollectionUtils.isEmpty(noInput)) {
            break;
        }
        return null;
    }
}
