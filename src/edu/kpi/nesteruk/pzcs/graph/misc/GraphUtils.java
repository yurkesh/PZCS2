package edu.kpi.nesteruk.pzcs.graph.misc;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.HasWeight;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.CollectionUtils;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public static <N extends Node, L extends Link<N>, G extends Graph<String, String>> G makeGraphCheckAllEdgesAdded(
            Supplier<G> graphFactory,
            Collection<N> nodes,
            Collection<L> links) {

        G graph = graphFactory.get();
        nodes.forEach(node -> graph.addVertex(node.getId()));
        links.forEach(link -> {
            /*
            String insertedEdge = graph.addEdge(link.getFirst(), link.getSecond());
            */
            String insertedEdge = Graphs.addEdge(graph, link.getFirst(), link.getSecond(), link.getWeight());
            if(insertedEdge == null) {
                throw new IllegalArgumentException("Cannot insert link = '" + link + "'");
            }
        });
        return graph;
    }

    public static int getWeightSum(Collection<? extends HasWeight> weights) {
        return weights.stream().mapToInt(HasWeight::getWeight).sum();
    }

    /**
     *
     * @param allNodes
     * @param allLinks
     * @return sum(nodes.weight) / [sum(nodes.weight) + sum(links.weight)]
     */
    public static double getGraphCorrelation(Collection<? extends Node> allNodes, Collection<? extends Link> allLinks) {
        if(CollectionUtils.isEmpty(allNodes)) {
            throw new IllegalArgumentException();
        }
        double nodesWeight = getWeightSum(allNodes);
        double linksWeight = CollectionUtils.isEmpty(allLinks) ? 0 : getWeightSum(allLinks);
        return  nodesWeight / (nodesWeight + linksWeight);
    }

    public static List<String> sortVertexesByCoherence(Graph<String, String> graph) {
        return graph.vertexSet().stream()
                //Map to pair {vertex, neighbours_of_vertex}
                .map(vertex -> Pair.create(vertex, Graphs.neighborListOf(graph, vertex)))
                //Sort by number of neighbors (desc) than by id (asc)
                .sorted(
                        Comparator.<Pair<String, List<String>>, Integer>comparing(
                                //By number of neighbors (asc)
                                pair -> pair.second.size()
                        )
                        //By number of neighbors (desc)
                        .reversed()
                        //By id asc
                        .thenComparing(Pair::getFirst)
                )
                //Get vertex
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    public static int getMaxCoherence(Graph<String, String> graph) {
        return graph.vertexSet().stream()
                //Map to pair {vertex, neighbours_of_vertex}
                .map(vertex -> Graphs.neighborListOf(graph, vertex).size())
                .max(Integer::compare)
                .get();
    }
}
