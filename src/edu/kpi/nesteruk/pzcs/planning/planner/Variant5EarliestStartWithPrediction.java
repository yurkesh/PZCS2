package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.ProcessorWithTaskEstimate;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.List;
import java.util.Optional;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
public class Variant5EarliestStartWithPrediction implements SingleTaskHostSearcher {
    @Override
    public Optional<ProcessorWithTaskEstimate> getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            TaskFinishTimeProvider taskFinishTimeProvider,
            List<StatefulProcessor> processorsSorted, LockedStatefulProcessorProvider processorCopyProvider) {

        throw new UnsupportedOperationException("Not implemented yet");
    }
}
