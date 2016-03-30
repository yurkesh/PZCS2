package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.graph.generation.GraphGenerator;
import edu.kpi.nesteruk.pzcs.graph.validation.GraphValidator;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import org.jgrapht.Graph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-20.
 */
public abstract class AbstractGraphModel<N extends Node, L extends Link<N>> implements GraphModel {

    private IdPool<String> idPool;
    private Map<String, N> nodesMap;
    private Map<String, L> linksMap;
    private Graph<String, String> graph;

    private final Supplier<Graph<String, String>> graphFactory;
    private final boolean isNodeWeighted;
    private final GraphValidator<String, String> validator;

    public AbstractGraphModel(Supplier<Graph<String, String>> graphFactory, boolean isNodeWeighted, GraphValidator<String, String> validator) {
        this.graphFactory = graphFactory;
        this.isNodeWeighted = isNodeWeighted;
        this.validator = validator;

        reset();
    }

    @Override
    public void reset() {
        idPool = new CommonIdPool();
        nodesMap = new LinkedHashMap<>();
        linksMap = new LinkedHashMap<>();
        graph = graphFactory.get();
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

    private IdAndValue makeNode(String id, int weight) {
        return addNode(makeConcreteNode(id, weight));
    }

    private IdAndValue addNode(N node) {
        String id = node.getId();
        nodesMap.put(id, node);
        try {
            graph.addVertex(id);
        } catch (Exception e) {
            logE("Can not add vertex with id = '" + id + "'. Exception = " + e);
        }
        return formatNode(node);
    }

    private static IdAndValue formatNode(Node node) {
        return new IdAndValue(node.getId(), node.toString());
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
        return !(graph.containsEdge(srcId, destId) || graph.containsEdge(destId, srcId));
    }

    private IdAndValue connect(String srcId, String destId, int weight) {
        Pair<L, String> linkWithId = makeConcreteLink(srcId, destId, weight);
        try {
            return addLink(linkWithId.second, linkWithId.first);
        } catch (IllegalArgumentException e) {
            logE(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private IdAndValue addLink(String linkId, L link) {
        boolean unique = linksMap.putIfAbsent(linkId, link) == null;
        if(unique) {
            String srcId = link.getFirst().getId();
            String destId = link.getSecond().getId();
            try {
                graph.addEdge(srcId, destId, linkId);
            } catch (Exception e) {
                throw new IllegalArgumentException("Can not add vertex with srcId = '"+ srcId +"', destId = '"+ destId +"', linkId = '"+ linkId +"'", e);
            }
            return formatLink(linkId, link);
        } else {
            throw new IllegalArgumentException("Link with id = '" + linkId + "' is not unique");
        }
    }

    private static IdAndValue formatLink(String linkId, Link link) {
        return new IdAndValue(linkId, String.valueOf(link.getWeight()));
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

    @Override
    public GraphModelBundle getSerializable() {
        return new GraphModelBundle<>(nodesMap, linksMap);
    }

    @Override
    public GraphDataAssembly restore(GraphModelBundle serializable) {
        return apply((GraphModelBundle<N, L>) serializable);
    }

    private GraphDataAssembly apply(GraphModelBundle<N, L> modelSerializable) {
        return apply(modelSerializable.getNodesMap().values(), modelSerializable.getLinksMap());
    }

    private GraphDataAssembly apply(Collection<N> nodes, Map<String, L> linksMap) {
        reset();
        nodes.forEach(this::addNode);
        linksMap.entrySet().forEach(linkEntry -> addLink(linkEntry.getKey(), linkEntry.getValue()));
        return getForPresenter();
    }

    private GraphDataAssembly getForPresenter() {
        return new GraphDataAssembly(
                nodesMap.values().stream()
                        .map(AbstractGraphModel::formatNode)
                        .collect(Collectors.toList()),
                linksMap.entrySet().stream()
                        .map(linkEntry -> {
                            L link = linkEntry.getValue();
                            return Pair.create(
                                    Pair.create(
                                            link.getFirst().getId(),
                                            link.getSecond().getId()),
                                    formatLink(linkEntry.getKey(), link)
                            );
                        })
                        .collect(Collectors.toList())
        );
    }

    @Override
    public GraphDataAssembly generate(GraphGenerator.Params params) {
        return apply(new GraphGenerator<>(this::makeConcreteNode, this::makeConcreteLink).generate(params));
    }
}
