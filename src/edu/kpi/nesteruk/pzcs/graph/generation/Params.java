package edu.kpi.nesteruk.pzcs.graph.generation;

/**
 * Created by Anatolii Bed on 2016-04-13.
 */
public class Params {
    public final int minNodeWeight;
    public final int maxNodeWeight;

    public final int numberOfNodes;
    /**
     * The same as 'correlation'
     */
    public final double coherence;

    public final int minLinkWeight;
    public final int maxLinkWeight;

    public Params(int minNodeWeight, int maxNodeWeight, int numberOfNodes, double coherence, int minLinkWeight, int maxLinkWeight) {
        this.minNodeWeight = minNodeWeight;
        this.maxNodeWeight = maxNodeWeight;
        this.numberOfNodes = numberOfNodes;
        this.coherence = coherence;
        this.minLinkWeight = minLinkWeight;
        this.maxLinkWeight = maxLinkWeight;
    }

    public static class Builder {
        private int minNodeWeight = -1;
        private int maxNodeWeight = -1;
        private int numberOfNodes = -1;
        private double coherence = -1;
        private int minLinkWeight = -1;
        private int maxLinkWeight = -1;

        public void setMinNodeWeight(int minNodeWeight) {
            this.minNodeWeight = minNodeWeight;
        }

        public void setMaxNodeWeight(int maxNodeWeight) {
            this.maxNodeWeight = maxNodeWeight;
        }

        public void setNumberOfNodes(int numberOfNodes) {
            this.numberOfNodes = numberOfNodes;
        }

        public void setCoherence(double coherence) {
            this.coherence = coherence;
        }

        public void setMinLinkWeight(int minLinkWeight) {
            this.minLinkWeight = minLinkWeight;
        }

        public void setMaxLinkWeight(int maxLinkWeight) {
            this.maxLinkWeight = maxLinkWeight;
        }

        public Params build() {
            return new Params(minNodeWeight, maxNodeWeight, numberOfNodes, coherence, minLinkWeight, maxLinkWeight);
        }
    }

    public static CheckResult isCorrect(Params params) {
        if (params.minNodeWeight < 0) {
            return CheckResult.MIN_NODE_WEIGHT_LESS_THAN_ZERO;
        }
        if (params.maxNodeWeight < params.minNodeWeight) {
            return CheckResult.MAX_NODE_WEIGHT_LESS_THAN_MIN;
        }
        if (params.numberOfNodes < 1) {
            return CheckResult.NUMBER_OF_NODES_LESS_THAN_ONE;
        }
        if (params.coherence <= 0 || params.coherence >= 1) {
            return CheckResult.INCORRECT_COHERENCE;
        }
        if (params.minLinkWeight < 0) {
            return CheckResult.MIN_LINK_WEIGHT_LESS_THAN_ZERO;
        }
        if (params.maxLinkWeight < params.minLinkWeight) {
            return CheckResult.MAX_LINK_WEIGHT_LESS_THAN_MIN;
        }
        return CheckResult.OK;
    }

    @Override
    public String toString() {
        return "Params{" +
                "minNodeWeight=" + minNodeWeight +
                ", maxNodeWeight=" + maxNodeWeight +
                ", numberOfNodes=" + numberOfNodes +
                ", coherence=" + coherence +
                ", minLinkWeight=" + minLinkWeight +
                ", maxLinkWeight=" + maxLinkWeight +
                '}';
    }

    public enum CheckResult {
        MIN_NODE_WEIGHT_LESS_THAN_ZERO,
        MAX_NODE_WEIGHT_LESS_THAN_MIN,
        NUMBER_OF_NODES_LESS_THAN_ONE,
        INCORRECT_COHERENCE,
        MIN_LINK_WEIGHT_LESS_THAN_ZERO,
        MAX_LINK_WEIGHT_LESS_THAN_MIN,
        OK
    }
}
