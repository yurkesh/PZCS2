package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.ProcessorWithTaskEstimate;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
class Variant4EarliestStartWithoutPrediction extends EarliestStartTaskHostSearcher implements SingleTaskHostSearcher {

    @Override
    public Optional<ProcessorWithTaskEstimate> getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            TaskFinishTimeProvider taskFinishTimeProvider,
            List<StatefulProcessor> processorsSorted,
            LockedStatefulProcessorProvider processorCopyProvider) {

        return super.getStartTime(
                true,
                router,
                tact,
                task,
                taskFinishTimeProvider,
                processorsSorted,
                processorCopyProvider
        );
    }

    @Override
    public String toString() {
        return "Variant4EarliestStartWithoutPrediction";
    }
}
