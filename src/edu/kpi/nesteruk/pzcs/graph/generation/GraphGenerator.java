package edu.kpi.nesteruk.pzcs.graph.generation;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.Link;
import edu.kpi.nesteruk.pzcs.model.primitives.Node;

/**
 * Created by Yurii on 2016-03-24.
 */
public class GraphGenerator<N extends Node, L extends Link<N>> {

    private final NodeFactory<N> nodeFactory;
    private final LinkFactory<N, L> linkFactory;

    public GraphGenerator(NodeFactory<N> nodeFactory, LinkFactory<N, L> linkFactory) {
        this.nodeFactory = nodeFactory;
        this.linkFactory = linkFactory;
    }

    public GraphModelBundle<N, L> generate(Params params) {
        //TODO implement
        return null;
    }

    public interface NodeFactory<N extends Node> {
        N createNode(String nodeId, int weight);
    }

    public interface LinkFactory<N extends Node, L extends Link<N>> {
        Pair<L, String> createLink(String srcId, String destId, int weight);
    }

    public static class Params {
        public final int minNodeWeight;
        public final int maxNodeWeight;

        public final int nodesCount;
        public final double coherence;

        public final int minLinkWeight;
        public final int maxLinkWeight;

        public Params(int minNodeWeight, int maxNodeWeight, int nodesCount, double coherence, int minLinkWeight, int maxLinkWeight) {
            this.minNodeWeight = minNodeWeight;
            this.maxNodeWeight = maxNodeWeight;
            this.nodesCount = nodesCount;
            this.coherence = coherence;
            this.minLinkWeight = minLinkWeight;
            this.maxLinkWeight = maxLinkWeight;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "minNodeWeight=" + minNodeWeight +
                    ", maxNodeWeight=" + maxNodeWeight +
                    ", nodesCount=" + nodesCount +
                    ", coherence=" + coherence +
                    ", minLinkWeight=" + minLinkWeight +
                    ", maxLinkWeight=" + maxLinkWeight +
                    '}';
        }
    }
}
