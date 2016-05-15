package edu.kpi.nesteruk.pzcs.graph.misc;

import edu.kpi.nesteruk.pzcs.model.primitives.HasWeight;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.CollectionUtils;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;

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

    public static <N extends Node, L extends Link<N>> WeightedGraph<String, String> makeGraphCheckAllEdgesAdded(
            Supplier<WeightedGraph<String, String>> graphFactory,
            Collection<N> nodes,
            Collection<L> links) {

        WeightedGraph<String, String> graph = graphFactory.get();
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

}
