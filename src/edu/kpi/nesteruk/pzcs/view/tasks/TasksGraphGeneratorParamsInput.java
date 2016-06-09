package edu.kpi.nesteruk.pzcs.view.tasks;

import edu.kpi.nesteruk.pzcs.graph.generation.GeneratorParams;
import edu.kpi.nesteruk.pzcs.view.common.input.ParamsInput;

/**
 * Created by Yurii on 2016-04-13.
 */
public enum TasksGraphGeneratorParamsInput implements ParamsInput<GeneratorParams, GeneratorParams.Builder> {
    MIN_NODE_WEIGHT("Min weight of node") {
        @Override
        public Number getValue(GeneratorParams generatorParams) {
            return generatorParams.minLinkWeight;
        }

        @Override
        public void setValue(String input, GeneratorParams.Builder paramsBuilder) {
            paramsBuilder.setMinNodeWeight(Integer.valueOf(input));
        }
    },
    MAX_NODE_WEIGHT("Max weight of node") {
        @Override
        public Number getValue(GeneratorParams generatorParams) {
            return generatorParams.maxNodeWeight;
        }

        @Override
        public void setValue(String input, GeneratorParams.Builder paramsBuilder) {
            paramsBuilder.setMaxNodeWeight(Integer.valueOf(input));
        }
    },
    NUMBER_OF_NODES("Number of nodes") {
        @Override
        public Number getValue(GeneratorParams generatorParams) {
            return generatorParams.numberOfNodes;
        }

        @Override
        public void setValue(String input, GeneratorParams.Builder paramsBuilder) {
            paramsBuilder.setNumberOfNodes(Integer.valueOf(input));
        }
    },
    COHERENCE("Coherence") {
        @Override
        public Number getValue(GeneratorParams generatorParams) {
            return generatorParams.coherence;
        }

        @Override
        public void setValue(String input, GeneratorParams.Builder paramsBuilder) {
            paramsBuilder.setCoherence(Double.valueOf(input));
        }
    },
    MIN_LINK_WEIGHT("Min weight of link") {
        @Override
        public Number getValue(GeneratorParams generatorParams) {
            return generatorParams.minLinkWeight;
        }

        @Override
        public void setValue(String input, GeneratorParams.Builder paramsBuilder) {
            paramsBuilder.setMinLinkWeight(Integer.valueOf(input));
        }
    },
    MAX_LINK_WEIGHT("Max weight of link") {
        @Override
        public Number getValue(GeneratorParams generatorParams) {
            return generatorParams.maxLinkWeight;
        }

        @Override
        public void setValue(String input, GeneratorParams.Builder paramsBuilder) {
            paramsBuilder.setMaxLinkWeight(Integer.valueOf(input));
        }
    };
    private final String caption;

    TasksGraphGeneratorParamsInput(String caption) {
        this.caption = caption;
    }

    @Override
    public String getCaption() {
        return caption;
    }
}
