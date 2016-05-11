package edu.kpi.nesteruk.pzcs.graph.generation;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.graph.validation.GraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonAcyclicGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.CollectionUtils;
import edu.kpi.nesteruk.util.RandomUtils;
import org.jgrapht.Graph;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Anatolii on 2016-03-24.
 */
public class GraphGenerator<N extends Node, L extends Link<N>> {

    private static final boolean DEBUG_LOG_ENABLED = false;

    private static final GraphValidator<String, String> NON_ACYCLIC_VALIDATOR = new NonAcyclicGraphValidator<>();

    private final Random random;
    private final NodeFactory<N> nodeFactory;
    private final LinkFactory<N, L> linkFactory;
    private final Supplier<Graph<String, String>> graphFactory;

    public GraphGenerator(Random random, NodeFactory<N> nodeFactory, LinkFactory<N, L> linkFactory, Supplier<Graph<String, String>> graphFactory) {
        this.random = random;
        this.nodeFactory = nodeFactory;
        this.linkFactory = linkFactory;
        this.graphFactory = graphFactory;
    }

    public GraphGenerator(NodeFactory<N> nodeFactory, LinkFactory<N, L> linkFactory, Supplier<Graph<String, String>> graphFactory) {
        this(new Random(42), nodeFactory, linkFactory, graphFactory);
    }

    public GraphModelBundle<N, L> generate(Params params) {
        Pair<Collection<N>, Map<String, L>> generated = generateInner(params);
        if(DEBUG_LOG_ENABLED) {
            System.out.println(
                    "NODES_WEIGHT = " + generated.getFirst().stream().mapToDouble(Node::getWeight).sum() + "\n" +
                            "LINKS_WEIGHT = " + generated.getSecond().values().stream().mapToDouble(Link::getWeight).sum()
            );
        }
        return new GraphModelBundle<>(
                generated.first.stream().collect(Collectors.toMap(Node::getId, Function.identity())),
                generated.second
        );
    }

    private Pair<Collection<N>, Map<String, L>> generateInner(Params params) {
        Map<Integer, N> nodesMap = generateNodes(params);
        double totalWeightOfNodes = GraphUtils.getWeightSum(nodesMap.values());
        final double totalWeightOfLinks = getTotalWeightOfLinks(totalWeightOfNodes, params.coherence);
        final double[] linksWeight = new double[] {0};
        Map<String, L> allLinks = new LinkedHashMap<>();

        for (double reserve = totalWeightOfLinks; reserve > 0; reserve = totalWeightOfLinks - GraphUtils.getWeightSum(allLinks.values())) {
            double minReserve = Math.max(reserve, 1);
            generateLink(
                    Math.min(minReserve, params.minLinkWeight),
                    Math.min(minReserve, params.maxLinkWeight),
                    nodesMap,
                    allLinks
            ).ifPresent(new LinkAddPerformer(linksWeight, allLinks));
        }

        return Pair.create(nodesMap.values(), allLinks);
    }

    private class LinkAddPerformer implements Consumer<Pair<Integer, Pair<L, String>>> {

        private final double[] linksWeight;
        private final Map<String, L> allLinks;

        private LinkAddPerformer(double[] linksWeight, Map<String, L> allLinks) {
            this.linksWeight = linksWeight;
            this.allLinks = allLinks;
        }

        @Override
        public void accept(Pair<Integer, Pair<L, String>> generated) {
            Pair<L, String> linkWithId = generated.second;
            double weightOfGeneratedLink = generated.first;
            linksWeight[0] += weightOfGeneratedLink;
            allLinks.put(linkWithId.second, linkWithId.first);
        }
    }

    /**
     *
     * @param minLinkWeight
     * @param maxLinkWeight
     * @param allNodesMap
     * @param links
     * @return {weightOfGeneratedLink, {generatedLink, idOfGeneratedLink}}
     */
    private Optional<Pair<Integer, Pair<L, String>>> generateLink(double minLinkWeight, double maxLinkWeight, Map<Integer, N> allNodesMap, Map<String, L> links) {
        Pair<L, String> linkWithId = generateLink(allNodesMap, minLinkWeight, maxLinkWeight);
        int weightOfGeneratedLink = linkWithId.first.getWeight();
        L equivalent = links.get(linkWithId.getSecond());
        if(equivalent != null) {
            L link = linkWithId.first;
            linkWithId = linkFactory.createLink(link.getFirst(), link.getSecond(), equivalent.getWeight() + link.getWeight());
        }
        Collection<L> linksUpdate = CollectionUtils.add(links.values(), linkWithId.first, LinkedHashSet::new);
        boolean noCycles = checkNoCycles(allNodesMap.values(), linksUpdate);
        return noCycles ? Optional.of(Pair.create(weightOfGeneratedLink, linkWithId)) : Optional.empty();
    }

    private Pair<L, String> generateLink(Map<Integer, N> allNodesMap,double minLinkWeight, double maxLinkWeight ) {
        int numberOfNodes = allNodesMap.size();
        return linkFactory.createLink(
                allNodesMap.get(random.nextInt(numberOfNodes)),
                allNodesMap.get(random.nextInt(numberOfNodes)),
                (int) Math.round(randomFromTo(minLinkWeight, maxLinkWeight))
        );
    }

    private boolean checkNoCycles(Collection<N> allNodes, Collection<L> links) {
        try {
            Graph<String, String> graph = GraphUtils.makeGraphCheckAllEdgesAdded(
                    graphFactory, allNodes, links
            );
            return NON_ACYCLIC_VALIDATOR.isValid(graph);
        } catch (Exception e) {
            if(DEBUG_LOG_ENABLED) {
                System.err.println("Loop found in graph. Nodes = " + allNodes + ", links = " + links);
            }
            return false;
        }
    }

    /**
     * correlation = sum(nodes.weight) / [sum(nodes.weight) + sum(links.weight)] ==>
     * sum(links.weight) = sum(nodes.weight) * (1 - correlation) / correlation
     */
    private static double getTotalWeightOfLinks(double totalWeightOfNodes, double correlation) {
        return totalWeightOfNodes * (1 - correlation) / correlation;
    }

    /**
     *
     * @param params
     * @return {idOfNode -> node}
     */
    private Map<Integer, N> generateNodes(Params params) {
        return IntStream.range(0, params.numberOfNodes)
                .mapToObj(id -> Pair.create(
                        id, nodeFactory.createNode(
                                String.valueOf(id), randomFromTo(params.minNodeWeight, params.maxNodeWeight)
                        )
                )).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private double randomFromTo(double from, double to) {
        if(Double.compare(from, to) == 0) {
            return from;
        }
        return random.nextDouble() * (to - from) + from;
    }

    private int randomFromTo(int from, int to) {
        return RandomUtils.randomFromTo(random, from, to);
    }

    public interface NodeFactory<N extends Node> {
        N createNode(String nodeId, int weight);
    }

    public interface LinkFactory<N extends Node, L extends Link<N>> {
        /**
         *
         * @param source
         * @param destination
         * @param weight
         * @return {link, idOfLink}
         */
        Pair<L, String> createLink(N source, N destination, int weight);
    }

}
