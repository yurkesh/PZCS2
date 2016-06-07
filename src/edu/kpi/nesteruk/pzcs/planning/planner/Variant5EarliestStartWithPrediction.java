package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.ProcessorWithTaskEstimate;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-06-02.
 */
class Variant5EarliestStartWithPrediction extends EarliestStartTaskHostSearcher implements SingleTaskHostSearcher {

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
}
