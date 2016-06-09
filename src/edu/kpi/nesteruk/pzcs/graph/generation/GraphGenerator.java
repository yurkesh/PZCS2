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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Anatolii on 2016-03-24.
 */
public class GraphGenerator<N extends Node, L extends Link<N>, G extends GraphModelBundle<N, L>> {

    private static final boolean DEBUG_LOG_ENABLED = false;

    private static final GraphValidator<String, String> NON_ACYCLIC_VALIDATOR = new NonAcyclicGraphValidator<>();

    private final Random random;
    private final NodeFactory<N> nodeFactory;
    private final LinkFactory<N, L> linkFactory;
    private final Supplier<? extends Graph<String, String>> graphFactory;
    private final BiFunction<Map<String, N>, Map<String, L>, G> graphBundleFactory;

    public GraphGenerator(
            Random random,
            NodeFactory<N> nodeFactory,
            LinkFactory<N, L> linkFactory,
            Supplier<? extends Graph<String, String>> graphFactory,
            BiFunction<Map<String, N>, Map<String, L>, G> graphBundleFactory) {

        this.random = random;
        this.nodeFactory = nodeFactory;
        this.linkFactory = linkFactory;
        this.graphFactory = graphFactory;
        this.graphBundleFactory = graphBundleFactory;
    }

    public GraphGenerator(
            NodeFactory<N> nodeFactory,
            LinkFactory<N, L> linkFactory,
            Supplier<? extends Graph<String, String>> graphFactory,
            BiFunction<Map<String, N>, Map<String, L>, G> graphBundleFactory) {
        this(new Random(42), nodeFactory, linkFactory, graphFactory, graphBundleFactory);
    }

    public G generate(GeneratorParams generatorParams) {
        Pair<Collection<N>, Map<String, L>> generated = generateInner(generatorParams);
        if(DEBUG_LOG_ENABLED) {
            System.out.println(
                    "NODES_WEIGHT = " + generated.getFirst().stream().mapToDouble(Node::getWeight).sum() + "\n" +
                            "LINKS_WEIGHT = " + generated.getSecond().values().stream().mapToDouble(Link::getWeight).sum()
            );
        }
        return graphBundleFactory.apply(
                generated.first.stream().collect(Collectors.toMap(Node::getId, Function.identity())),
                generated.second
        );
    }

    private Pair<Collection<N>, Map<String, L>> generateInner(GeneratorParams generatorParams) {
        Map<Integer, N> nodesMap = generateNodes(generatorParams);
        Map<String, N> nodesMapStr = nodesMap.values().stream().collect(Collectors.toMap(
                Node::getId,
                Function.identity()
        ));

        double totalWeightOfNodes = GraphUtils.getWeightSum(nodesMap.values());
        final double totalWeightOfLinks = getTotalWeightOfLinks(totalWeightOfNodes, generatorParams.coherence);
        final double[] linksWeight = new double[] {0};
        Map<String, L> allLinks = new LinkedHashMap<>();

        for (double reserve = totalWeightOfLinks; reserve > 0; reserve = totalWeightOfLinks - GraphUtils.getWeightSum(allLinks.values())) {
            double minReserve = Math.max(reserve, 1);
            generateLink(
                    Math.min(minReserve, generatorParams.minLinkWeight),
                    Math.min(minReserve, generatorParams.maxLinkWeight),
                    nodesMap,
                    nodesMapStr,
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
     * @return {weightOfGeneratedLink, {generatedLink, idOfGeneratedLink}}
     */
    private Optional<Pair<Integer, Pair<L, String>>> generateLink(
            double minLinkWeight,
            double maxLinkWeight,
            Map<Integer, N> allNodesMapInt,
            Map<String, N> allNodesMapStr,
            Map<String, L> links) {

        Pair<L, String> linkWithId = generateLink(allNodesMapInt, minLinkWeight, maxLinkWeight);
        int weightOfGeneratedLink = linkWithId.first.getWeight();
        L equivalent = links.get(linkWithId.getSecond());
        if(equivalent != null) {
            L link = linkWithId.first;
            linkWithId = linkFactory.createLink(
                    allNodesMapStr.get(link.getFirst()),
                    allNodesMapStr.get(link.getSecond()),
                    equivalent.getWeight() + link.getWeight()
            );
        }
        Collection<L> linksUpdate = CollectionUtils.add(links.values(), linkWithId.first, LinkedHashSet::new);
        boolean noCycles = checkNoCycles(allNodesMapInt.values(), linksUpdate);
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
     * @return {idOfNode -> node}
     */
    private Map<Integer, N> generateNodes(GeneratorParams generatorParams) {
        return IntStream.range(0, generatorParams.numberOfNodes)
                .mapToObj(id -> Pair.create(
                        id, nodeFactory.createNode(
                                String.valueOf(id), randomFromTo(generatorParams.minNodeWeight, generatorParams.maxNodeWeight)
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
         * @return {link, idOfLink}
         */
        Pair<L, String> createLink(N source, N destination, int weight);
    }

}
