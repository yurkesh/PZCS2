package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.ProcessorWithTaskEstimate;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-06-09.
 */
abstract class SortingProcessorsSingleTaskHostSearcher implements SingleTaskHostSearcher {

    @Override
    public Optional<ProcessorWithTaskEstimate> getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            TaskFinishTimeProvider taskFinishTimeProvider,
            List<StatefulProcessor> processorsSorted,
            LockedStatefulProcessorProvider processorCopyProvider) {

        //Use 'Template method' pattern
        Optional<StatefulProcessor> selectedProcessor = selectProcessor(tact, processorsSorted);

        if(selectedProcessor.isPresent()) {
            StatefulProcessor processor = selectedProcessor.get();

            int minStartTime = TaskOnProcessorWithoutTransfersMinStartDefiner.needTransfersFromDependencies(
                    tact, task, processor
            );

            TaskWithTransfersEstimate taskWithTransfersEstimate;
            if(minStartTime >= 0) {
                taskWithTransfersEstimate = new TaskWithTransfersEstimate(minStartTime, null);
            } else {
                taskWithTransfersEstimate = TaskWithTransferEstimatePerformer.getMinAvailableStartTimeOfTaskDependingOnTransfers(
                        tact,
                        taskFinishTimeProvider,
                        task,
                        processor,
                        router,
                        processorCopyProvider,
                        true
                );
            }

            return Optional.of(new ProcessorWithTaskEstimate(processor, taskWithTransfersEstimate));
        }

        return Optional.empty();
    }

    protected abstract Optional<StatefulProcessor> selectProcessor(int tact, List<StatefulProcessor> processorsSorted);
}
