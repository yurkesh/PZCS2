package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.common.GraphDataAssembly;
import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.graph.validation.GraphValidator;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import org.jgrapht.Graph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-20.
 */
public abstract class AbstractGraphModel<N extends Node, L extends Link<N>, G extends GraphModelBundle<N, L>> implements GraphModel {

    private IdPool<String> idPool;
    private Map<String, N> nodesMap;
    private Map<String, L> linksMap;
    private Graph<String, String> graph;

    private final Supplier<? extends Graph<String, String>> graphFactory;
    private final boolean isNodeWeighted;
    private final GraphValidator<String, String> validator;

    public AbstractGraphModel(Supplier<? extends Graph<String, String>> graphFactory, boolean isNodeWeighted, GraphValidator<String, String> validator) {
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
                () -> idPool.obtainId(id -> !nodesMap.containsKey(id)),
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

    protected boolean canConnect(String srcId, String destId) {
        return !(graph.containsEdge(srcId, destId) || graph.containsEdge(destId, srcId));
    }

    private IdAndValue connect(String srcId, String destId, int weight) {
        Pair<L, String> linkWithId = makeConcreteLink(getNode(srcId), getNode(destId), weight);
        try {
            return addLink(linkWithId);
        } catch (IllegalArgumentException e) {
            logE(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private IdAndValue addLink(Pair<L, String> linkWithId) {
        return addLink(linkWithId.first, linkWithId.second);
    }

    private IdAndValue addLink(L link, String linkId) {
        boolean unique = linksMap.putIfAbsent(linkId, link) == null;
        if(unique) {
            String srcId = link.getFirst();
            String destId = link.getSecond();
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

    /**
     * @return {link, idOfLink}
     */
    protected abstract Pair<L, String> makeConcreteLink(N source, N destination, int weight);

    @Override
    public Collection<String> deleteNode(String id) {
        N removedNode = nodesMap.remove(id);

        Set<String> linkIDsToRemove = linksMap.entrySet().stream()
                //Get all links that are incident to this node
                .filter(entry -> entry.getValue().isIncident(removedNode))
                //Get id of link
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        linkIDsToRemove.forEach(linksMap::remove);
        graph.removeVertex(id);
        graph.removeAllEdges(linkIDsToRemove);

        return linkIDsToRemove;
    }

    @Override
    public void deleteLink(String id) {
        linksMap.remove(id);
        graph.removeEdge(id);
    }

    @Override
    public boolean validate() {
        return validator.isValid(GraphUtils.cloneGraph(graph, graphFactory.get()));
    }

    @Override
    public G getBundle() {
        return makeBundle(nodesMap, linksMap);
    }

    protected abstract G makeBundle(Map<String, N> nodesMap, Map<String, L> linksMap);

    @Override
    public GraphDataAssembly restore(GraphModelBundle modelBundle) {
        @SuppressWarnings("unchecked")
        G bundle = (G) modelBundle;
        return apply(bundle);
    }

    protected GraphDataAssembly apply(G modelBundle) {
        return apply(modelBundle.getNodesMap().values(), modelBundle.getLinksMap());
    }

    private GraphDataAssembly apply(Collection<N> nodes, Map<String, L> linksMap) {
        reset();
        nodes.forEach(this::addNode);
        linksMap.entrySet().forEach(linkEntry -> addLink(linkEntry.getValue(), linkEntry.getKey()));
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
                                            link.getFirst(),
                                            link.getSecond()),
                                    formatLink(linkEntry.getKey(), link)
                            );
                        })
                        .collect(Collectors.toList())
        );
    }

    @Override
    public IdAndValue updateWeightOfLink(String idOfLink, String text) {
        L link = linksMap.get(idOfLink);
        if(link == null) {
            throw new IllegalArgumentException("Cannot find link with id = '" + idOfLink + "'");
        }
        int weight;
        try {
            weight = resolveWeight(text);
        } catch (NumberFormatException e) {
            System.err.println("Cannot convert text = '" + text + "' to weight");
            //Return old value
            return formatLink(idOfLink, link);
        }

        linksMap.remove(idOfLink);
        graph.removeEdge(idOfLink);

        String srcId = link.getFirst();
        String destId = link.getSecond();
        return connect(srcId, destId, weight);
    }

    @Override
    public IdAndValue updateWeightOfNode(String idOfNode, String text) {
        N node = nodesMap.get(idOfNode);
        if(node == null) {
            throw new IllegalArgumentException("Cannot find node with id = '" + idOfNode + "'");
        }
        int weight;
        try {
            weight = resolveWeight(text);
        } catch (NumberFormatException e) {
            System.err.println("Cannot convert text = '" + text + "' to weight");
            //Return old value
            return formatNode(node);
        }

        return makeNode(idOfNode, weight);
    }

    private static Integer resolveWeight(String text) {
        text = text.trim().replaceAll("\n", "").replace("\r", "");
        return Integer.valueOf(text);
    }
}
