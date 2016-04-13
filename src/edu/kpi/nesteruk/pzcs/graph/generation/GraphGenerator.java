package edu.kpi.nesteruk.pzcs.graph.generation;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.graph.validation.GraphValidator;
import edu.kpi.nesteruk.pzcs.graph.validation.NonAcyclicGraphValidator;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.util.CollectionUtils;
import org.jgrapht.Graph;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Yurii on 2016-03-24.
 */
public class GraphGenerator<N extends Node, L extends Link<N>> {

    private static final Random RANDOM = new Random(42);

    private static final GraphValidator<String, String> NON_ACYCLIC_VALIDATOR = new NonAcyclicGraphValidator<>();

    private final NodeFactory<N> nodeFactory;
    private final LinkFactory<N, L> linkFactory;
    private final Supplier<Graph<String, String>> graphFactory;

    public GraphGenerator(NodeFactory<N> nodeFactory, LinkFactory<N, L> linkFactory, Supplier<Graph<String, String>> graphFactory) {
        this.nodeFactory = nodeFactory;
        this.linkFactory = linkFactory;
        this.graphFactory = graphFactory;
    }

    public GraphModelBundle<N, L> generate(Params params) {
        Pair<Collection<N>, Map<String, L>> generated = generateInner(params);
        System.out.println(
                "NODES_WEIGHT = " + generated.getFirst().stream().mapToDouble(Node::getWeight).sum() + "\n" +
                "LINKS_WEIGHT = " + generated.getSecond().values().stream().mapToDouble(Link::getWeight).sum()
        );
        return new GraphModelBundle<>(
                generated.first.stream().collect(Collectors.toMap(Node::getId, Function.identity())),
                generated.second
        );
    }

    private Pair<Collection<N>, Map<String, L>> generateInner(Params params) {
        Map<Integer, N> nodesMap = generateNodes(params, nodeFactory);
        double totalWeightOfNodes = getTotalWeightOfNodes(nodesMap.values());
        final double totalWeightOfLinks = getTotalWeightOfLinks(totalWeightOfNodes, params.coherence);
        final double[] linksWeight = new double[] {0};
        Map<String, L> allLinks = new LinkedHashMap<>();

//        for (double linksWeightReserve = totalWeightOfLinks; linksWeightReserve >= 0; linksWeightReserve -= linksWeight[0]) {
//            double reserve = linksWeightReserve;
//            generateLink(
//                    Math.min(linksWeightReserve, params.minLinkWeight),
//                    Math.min(linksWeightReserve, params.maxLinkWeight),
//                    nodesMap,
//                    allLinks
//            ).ifPresent(/*linkWithId -> {
//                double weightOfGeneratedLink = linkWithId.first.getWeight();
//                if(weightOfGeneratedLink > Math.round(reserve)) {
//                    throw new IllegalArgumentException("Weight of generated = " + weightOfGeneratedLink + ", linksWeightReserve = " + reserve);
//                }
//                linksWeight[0] += weightOfGeneratedLink;
//                allLinks.put(linkWithId.second, linkWithId.first);
//            }*/ new LinkAddPerformer(linksWeightReserve, linksWeight, allLinks).andThen(linkWithId -> reserve -= linkWithId.first.getWeight()));
//            System.out.println("Total = " + totalWeightOfLinks + ", sum = " + linksWeight[0] + ", reserve = " + linksWeightReserve);
//        }

        double[] linksWeightReserve = new double[] {totalWeightOfLinks};
        while (true) {
            double reserve = totalWeightOfLinks - allLinks.values().stream().mapToDouble(Link::getWeight).sum();
            if(reserve <= 0) {
                break;
            }

            generateLink(
                    Math.min(reserve, params.minLinkWeight),
                    Math.min(reserve, params.maxLinkWeight),
                    nodesMap,
                    allLinks
            ).ifPresent(new LinkAddPerformer(linksWeightReserve[0], linksWeight, allLinks).andThen(
                    linkWithId -> {
                        int weight = linkWithId.getFirst().getWeight();
//                        System.out.println("Links weight reserve = " + linksWeightReserve[0]);
//                        linksWeightReserve[0] -= weight;
                    }
            ));
        }

        return Pair.create(nodesMap.values(), allLinks);
    }

    private class LinkAddPerformer implements Consumer<Pair<L, String>> {

        private final double reserve;
        private final double[] linksWeight;
        private final Map<String, L> allLinks;

        private LinkAddPerformer(double reserve, double[] linksWeight, Map<String, L> allLinks) {
            this.reserve = reserve;
            this.linksWeight = linksWeight;
            this.allLinks = allLinks;
        }

        @Override
        public void accept(Pair<L, String> linkWithId) {
            double weightOfGeneratedLink = linkWithId.first.getWeight();
//            if(weightOfGeneratedLink > Math.round(reserve)) {
//                throw new IllegalArgumentException("Weight of generated = " + weightOfGeneratedLink + ", linksWeightReserve = " + reserve);
//            }
            linksWeight[0] += weightOfGeneratedLink;
            allLinks.put(linkWithId.second, linkWithId.first);
        }
    }

    private Optional<Pair<L, String>> generateLink(double minLinkWeight, double maxLinkWeight, Map<Integer, N> allNodesMap, Map<String, L> links) {
        Pair<L, String> linkWithId = generateLink(allNodesMap, minLinkWeight, maxLinkWeight);
        L equivalent = links.get(linkWithId.getSecond());
        if(equivalent != null) {
            L link = linkWithId.first;
            linkWithId = linkFactory.createLink(link.getFirst(), link.getSecond(), equivalent.getWeight() + link.getWeight());
        }
        Collection<L> linksUpdate = CollectionUtils.add(links.values(), linkWithId.first, LinkedHashSet::new);
        boolean noCycles = checkNoCycles(allNodesMap.values(), linksUpdate);
        return noCycles ? Optional.of(linkWithId) : Optional.empty();
    }

//    private Optional<L> getEquivalent(Map<String, L> links, L linkToFind) {
//        return links.entrySet().stream().filter(entry -> entry.getValue().equals(linkToFind));
//        return Optional.of(links.stream().collect(Collectors.toMap(Function.identity(), Function.identity())).get(linkToFind));
//    }

    private Pair<L, String> generateLink(Map<Integer, N> allNodesMap,double minLinkWeight, double maxLinkWeight ) {
        int numberOfNodes = allNodesMap.size();
        return linkFactory.createLink(
                allNodesMap.get(RANDOM.nextInt(numberOfNodes)),
                allNodesMap.get(RANDOM.nextInt(numberOfNodes)),
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
            System.err.println("Loop found in graph. Nodes = " + allNodes + ", links = " + links);
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
     * @param allNodes
     * @param allLinks
     * @return sum(nodes.weight) / [sum(nodes.weight) + sum(links.weight)]
     */
    private static double getGraphCorrelation(Collection<? extends Node> allNodes, Collection<? extends Link> allLinks) {
        if(CollectionUtils.isEmpty(allNodes)) {
            throw new IllegalArgumentException();
        }
        double nodesWeight = getTotalWeightOfNodes(allNodes);
        double linksWeight = CollectionUtils.isEmpty(allLinks) ? 0 : allLinks.stream().mapToDouble(Link::getWeight).sum();
        return  nodesWeight / (nodesWeight + linksWeight);
    }

    private static double getTotalWeightOfNodes(Collection<? extends Node> allNodes) {
        return allNodes.stream().mapToDouble(Node::getWeight).sum();
    }

    /**
     *
     * @param params
     * @param nodeFactory
     * @param <N>
     * @return {idOfNode -> node}
     */
    private static <N extends Node> Map<Integer, N> generateNodes(Params params, NodeFactory<N> nodeFactory) {
        return IntStream.range(0, params.numberOfNodes)
                .mapToObj(id -> Pair.create(
                        id, nodeFactory.createNode(
                                String.valueOf(id), randomFromTo(params.minNodeWeight, params.maxNodeWeight)
                        )
                )).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static double randomFromTo(double from, double to) {
        if(Double.compare(from, to) == 0) {
            return from;
        }
        return RANDOM.nextDouble() * (to - from) + from;
    }

    private static int randomFromTo(int from, int to) {
        if(from == to) {
            return from;
        }
        return RANDOM.nextInt(to - from) + from;
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
