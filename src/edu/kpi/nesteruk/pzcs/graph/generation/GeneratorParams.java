package edu.kpi.nesteruk.pzcs.graph.generation;

import edu.kpi.nesteruk.misc.GenericBuilder;

/**
 * Created by Yurii on 2016-04-13.
 */
public class GeneratorParams {
    public final int minNodeWeight;
    public final int maxNodeWeight;

    public final int numberOfNodes;
    /**
     * The same as 'correlation'
     */
    public final double coherence;

    public final int minLinkWeight;
    public final int maxLinkWeight;

    public GeneratorParams(int minNodeWeight, int maxNodeWeight, int numberOfNodes, double coherence, int minLinkWeight, int maxLinkWeight) {
        this.minNodeWeight = minNodeWeight;
        this.maxNodeWeight = maxNodeWeight;
        this.numberOfNodes = numberOfNodes;
        this.coherence = coherence;
        this.minLinkWeight = minLinkWeight;
        this.maxLinkWeight = maxLinkWeight;
    }

    public static class Builder implements GenericBuilder<GeneratorParams> {
        private int minNodeWeight = -1;
        private int maxNodeWeight = -1;
        private int numberOfNodes = -1;
        private double coherence = -1;
        private int minLinkWeight = -1;
        private int maxLinkWeight = -1;

        public Builder() {
        }

        public Builder(Builder builder) {
            minNodeWeight = builder.minNodeWeight;
            maxNodeWeight = builder.maxNodeWeight;
            numberOfNodes = builder.numberOfNodes;
            coherence = builder.coherence;
            minLinkWeight = builder.minLinkWeight;
            maxLinkWeight = builder.maxLinkWeight;
        }

        public Builder setMinNodeWeight(int minNodeWeight) {
            this.minNodeWeight = minNodeWeight;
            return this;
        }

        public Builder setMaxNodeWeight(int maxNodeWeight) {
            this.maxNodeWeight = maxNodeWeight;
            return this;
        }

        public Builder setNumberOfNodes(int numberOfNodes) {
            this.numberOfNodes = numberOfNodes;
            return this;
        }

        public Builder setCoherence(double coherence) {
            this.coherence = coherence;
            return this;
        }

        public Builder setMinLinkWeight(int minLinkWeight) {
            this.minLinkWeight = minLinkWeight;
            return this;
        }

        public Builder setMaxLinkWeight(int maxLinkWeight) {
            this.maxLinkWeight = maxLinkWeight;
            return this;
        }

        @Override
        public GeneratorParams build() {
            return new GeneratorParams(minNodeWeight, maxNodeWeight, numberOfNodes, coherence, minLinkWeight, maxLinkWeight);
        }
    }

    public static CheckResult isCorrect(GeneratorParams generatorParams) {
        if (generatorParams.minNodeWeight < 0) {
            return CheckResult.MIN_NODE_WEIGHT_LESS_THAN_ZERO;
        }
        if (generatorParams.maxNodeWeight < generatorParams.minNodeWeight) {
            return CheckResult.MAX_NODE_WEIGHT_LESS_THAN_MIN;
        }
        if (generatorParams.numberOfNodes < 1) {
            return CheckResult.NUMBER_OF_NODES_LESS_THAN_ONE;
        }
        if (generatorParams.coherence <= 0 || generatorParams.coherence >= 1) {
            return CheckResult.INCORRECT_COHERENCE;
        }
        if (generatorParams.minLinkWeight < 0) {
            return CheckResult.MIN_LINK_WEIGHT_LESS_THAN_ZERO;
        }
        if (generatorParams.maxLinkWeight < generatorParams.minLinkWeight) {
            return CheckResult.MAX_LINK_WEIGHT_LESS_THAN_MIN;
        }
        return CheckResult.OK;
    }

    @Override
    public String toString() {
        return "GeneratorParams{" +
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
