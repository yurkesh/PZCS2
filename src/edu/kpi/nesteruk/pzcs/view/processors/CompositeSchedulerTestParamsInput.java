package edu.kpi.nesteruk.pzcs.view.processors;

import edu.kpi.nesteruk.pzcs.scheduling.CompositeSchedulerTestParams;
import edu.kpi.nesteruk.pzcs.view.common.input.ParamsInput;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-06-09.
 */
public enum CompositeSchedulerTestParamsInput implements ParamsInput<CompositeSchedulerTestParams, CompositeSchedulerTestParams.Builder> {
    MIN_NUMBER_OF_TASKS(
            "Min number of tasks",
            CompositeSchedulerTestParams::getMinNumberOfTasks,
            CompositeSchedulerTestParams.Builder::setMinNumberOfTasks
    ),
    MAX_NUMBER_OF_TASKS(
            "Max number of tasks",
            CompositeSchedulerTestParams::getMaxNumberOfTasks,
            CompositeSchedulerTestParams.Builder::setMaxNumberOfTasks
    ),
    DELTA_NUMBER_OF_TASKS(
            "Delta number of tasks",
            CompositeSchedulerTestParams::getDeltaNumberOfTasks,
            CompositeSchedulerTestParams.Builder::setDeltaNumberOfTasks
    ),

    MIN_COHERENCE(
            "Min coherence",
            CompositeSchedulerTestParams.Builder::setMinTasksGraphCoherence,
            CompositeSchedulerTestParams::getMinTasksGraphCoherence
    ),
    MAX_COHERENCE(
            "Max coherence",
            CompositeSchedulerTestParams.Builder::setMaxTasksGraphCoherence,
            CompositeSchedulerTestParams::getMaxTasksGraphCoherence
    ),
    DELTA_COHERENCE(
            "Delta coherence",
            CompositeSchedulerTestParams.Builder::setDeltaTasksGraphCoherence,
            CompositeSchedulerTestParams::getDeltaTasksGraphCoherence
    ),

    MIN_TASK_WEIGHT(
            "Min task weight",
            CompositeSchedulerTestParams::getMinTaskWeight,
            CompositeSchedulerTestParams.Builder::setMinTaskWeight
    ),
    MAX_TASK_WEIGHT(
            "Max task weight",
            CompositeSchedulerTestParams::getMaxTaskWeight,
            CompositeSchedulerTestParams.Builder::setMaxTaskWeight
    ),

    MIN_TASK_LINK_WEIGHT(
            "Min task link weight",
            CompositeSchedulerTestParams::getMinTasksLinkWeight,
            CompositeSchedulerTestParams.Builder::setMinTasksLinkWeight
    ),
    MAX_TASK_LINK_WEIGHT(
            "Max task link weight",
            CompositeSchedulerTestParams::getMaxTasksLinkWeight,
            CompositeSchedulerTestParams.Builder::setMaxTasksLinkWeight
    ),

    NUMBER_OF_GRAPHS(
            "Number of graphs",
            CompositeSchedulerTestParams::getNumberOfTaskGraphsToGenerate,
            CompositeSchedulerTestParams.Builder::setNumberOfTaskGraphsToGenerate
    ),

    ;

    private final String caption;
    private final Function<CompositeSchedulerTestParams, Number> getValueFunction;
    private final BiConsumer<CompositeSchedulerTestParams.Builder, Double> putValueConsumer;

    CompositeSchedulerTestParamsInput(
            String caption,
            BiConsumer<CompositeSchedulerTestParams.Builder, Double> putValueConsumer,
            Function<CompositeSchedulerTestParams, Number> getValueFunction) {

        this.caption = caption;
        this.getValueFunction = getValueFunction;
        this.putValueConsumer = putValueConsumer;
    }

    CompositeSchedulerTestParamsInput(
            String caption,
            Function<CompositeSchedulerTestParams, Number> getValueFunction,
            BiConsumer<CompositeSchedulerTestParams.Builder, Integer> putValueConsumer) {

        this(
                caption,
                (builder, value) -> {
                    putValueConsumer.accept(builder, (int) Math.round(value));
                },
                getValueFunction
        );
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public Number getValue(CompositeSchedulerTestParams params) {
        return getValueFunction.apply(params);
    }

    @Override
    public void setValue(String input, CompositeSchedulerTestParams.Builder paramsBuilder) {
        putValueConsumer.accept(paramsBuilder, Double.valueOf(input));
    }
}
