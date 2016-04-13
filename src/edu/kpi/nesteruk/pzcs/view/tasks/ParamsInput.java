package edu.kpi.nesteruk.pzcs.view.tasks;

import edu.kpi.nesteruk.pzcs.graph.generation.Params;

/**
 * Created by Yurii on 2016-04-13.
 */
public enum ParamsInput {
    MIN_NODE_WEIGHT("Min weight of node") {
        @Override
        public Number getValue(Params params) {
            return params.minLinkWeight;
        }

        @Override
        public void setValue(String input, Params.Builder paramsBuilder) {
            paramsBuilder.setMinNodeWeight(Integer.valueOf(input));
        }
    },
    MAX_NODE_WEIGHT("Max weight of node") {
        @Override
        public Number getValue(Params params) {
            return params.maxNodeWeight;
        }

        @Override
        public void setValue(String input, Params.Builder paramsBuilder) {
            paramsBuilder.setMaxNodeWeight(Integer.valueOf(input));
        }
    },
    NUMBER_OF_NODES("Number of nodes") {
        @Override
        public Number getValue(Params params) {
            return params.numberOfNodes;
        }

        @Override
        public void setValue(String input, Params.Builder paramsBuilder) {
            paramsBuilder.setNumberOfNodes(Integer.valueOf(input));
        }
    },
    COHERENCE("Coherence") {
        @Override
        public Number getValue(Params params) {
            return params.coherence;
        }

        @Override
        public void setValue(String input, Params.Builder paramsBuilder) {
            paramsBuilder.setCoherence(Double.valueOf(input));
        }
    },
    MIN_LINK_WEIGHT("Min weight of link") {
        @Override
        public Number getValue(Params params) {
            return params.minLinkWeight;
        }

        @Override
        public void setValue(String input, Params.Builder paramsBuilder) {
            paramsBuilder.setMinLinkWeight(Integer.valueOf(input));
        }
    },
    MAX_LINK_WEIGHT("Max weight of link") {
        @Override
        public Number getValue(Params params) {
            return params.maxLinkWeight;
        }

        @Override
        public void setValue(String input, Params.Builder paramsBuilder) {
            paramsBuilder.setMaxLinkWeight(Integer.valueOf(input));
        }
    };
    private final String caption;

    ParamsInput(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public abstract Number getValue(Params params);

    public abstract void setValue(String input, Params.Builder paramsBuilder);
}
