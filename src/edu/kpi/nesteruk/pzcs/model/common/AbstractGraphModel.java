package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.validation.GraphValidator;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-03-20.
 */
public abstract class AbstractGraphModel<N extends Node, L extends Link<N>> implements GraphModel {

    private final Supplier<Graph<String, String>> graphFactory;
    private IdPool<String> idPool = new CommonIdPool();
    private Map<String, N> nodesMap = new LinkedHashMap<>();
    private Map<String, L> linksMap = new LinkedHashMap<>();

    private final Graph<String, String> graph;
    private final boolean isNodeWeighted;
    private final GraphValidator validator;

    public AbstractGraphModel(Supplier<Graph<String, String>> graphFactory, boolean isNodeWeighted, GraphValidator validator) {
        this.graphFactory = graphFactory;
        this.graph = graphFactory.get();
        this.isNodeWeighted = isNodeWeighted;
        this.validator = validator;
    }

    @Override
    public NodeBuilder getNodeBuilder() {
        return new CommonNodeBuilder(
                isNodeWeighted,
                idPool::obtainID,
                id -> {
                    id = id.toLowerCase();
                    boolean unique = !nodesMap.containsKey(id);
                    if(unique) {
                        idPool.obtainId(id);
                    } else {
                        idPool.releaseId(id);
                    }
                    return unique;
                },
                this::makeNode
        );
    }

    public final IdAndValue makeNode(String id, int weight) {
        N node = makeConcreteNode(id, weight);
        nodesMap.put(id, node);
        try {
            graph.addVertex(id);
        } catch (Exception e) {
            logE("Can not add vertex with id = '" + id + "'. Exception = " + e);
        }
        return new IdAndValue(id, node.toString());
    }

    protected final void logE(String error) {
        System.err.println(error);
    }

    protected abstract N makeConcreteNode(String nodeId, int weight);

    @Override
    public LinkBuilder getLinkBuilder() {
        return new CommonLinkBuilder(true, this::canConnect, this::connect);
    }

    protected N getNode(String nodeId) {
        return nodesMap.get(nodeId);
    }

    protected L getLink(String linkId) {
        return linksMap.get(linkId);
    }

    protected boolean canConnect(String srcId, String destId) {
        return !graph.containsEdge(srcId, destId);
    }

    public final IdAndValue connect(String srcId, String destId, int weight) {
        Pair<L, String> linkWithId = makeConcreteLink(srcId, destId, weight);
        L link = linkWithId.first;
        String linkId = linkWithId.second;
        boolean unique = linksMap.putIfAbsent(linkId, link) == null;
        if(unique) {
            try {
                graph.addEdge(srcId, destId, linkId);
            } catch (Exception e) {
                logE("Can not add vertex with srcId = '"+ srcId +"', destId = '"+ destId +"', linkId = '"+ linkId +"'. Exception = " + e);
                return null;
            }
            return new IdAndValue(linkId, link.toString());
        } else {
            return null;
        }
    }

    protected abstract Pair<L, String> makeConcreteLink(String srcId, String destId, int weight);

    @Override
    public void deleteNode(String id) {
        nodesMap.remove(id);
        graph.removeVertex(id);
    }

    @Override
    public void deleteLink(String id) {
        linksMap.remove(id);
        graph.removeEdge(id);
    }

    public interface GraphCloner<V, E> extends Function<Graph<V, E>, Graph<V, E>> {

        static <V, E, G extends Graph<V, E>> Graph<V, E> cloneGraph(Graph<V, E> srcGraph, G destGraph) {
            srcGraph.vertexSet().forEach(destGraph::addVertex);
            for (E edge : srcGraph.edgeSet()) {
                destGraph.addEdge(srcGraph.getEdgeSource(edge), srcGraph.getEdgeTarget(edge), edge);
            }
            return destGraph;
        }

    }

    protected Graph<String, String> cloneGraph(Graph<String, String> graph) {
        return GraphCloner.cloneGraph(graph, graphFactory.get());
    }

    @Override
    public boolean validate() {
        return validator.isValid(cloneGraph(graph));
    }
}
