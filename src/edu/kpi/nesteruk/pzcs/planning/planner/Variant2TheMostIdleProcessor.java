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
public class Variant2TheMostIdleProcessor implements SingleTaskHostSearcher {

    @Override
    public Optional<ProcessorWithTaskEstimate> getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            TaskFinishTimeProvider taskFinishTimeProvider,
            List<StatefulProcessor> processorsSorted,
            LockedStatefulProcessorProvider processorCopyProvider) {

        Optional<StatefulProcessor> theMostIdleProcessor = processorsSorted.stream()
                //Only free processors
                .filter(processor -> processor.isFree(tact))
                //Sort by time when idle
                .max(Comparator.comparing(processor -> processor.getIdleTime(tact)));

        if(theMostIdleProcessor.isPresent()) {
            StatefulProcessor processor = theMostIdleProcessor.get();

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
                        false
                );
            }

            return Optional.of(new ProcessorWithTaskEstimate(processor, taskWithTransfersEstimate));
        }

        return Optional.empty();
    }
}
