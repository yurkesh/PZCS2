package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.ProcessorWithTaskEstimate;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yurii on 2016-06-08.
 */
class EarliestStartTaskHostSearcher {

    public Optional<ProcessorWithTaskEstimate> getStartTime(
            boolean useTransferBackwardPrediction,
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            TaskFinishTimeProvider taskFinishTimeProvider,
            List<StatefulProcessor> processorsSorted,
            LockedStatefulProcessorProvider processorCopyProvider) {

        //Find processor with the best start time for this task
        return processorsSorted.stream()
                //StatefulProcessor -> {StatefulProcessor, startTime}
                .map(statefulProcessor -> new ProcessorWithTaskEstimate(
                        statefulProcessor,
                        getEstimate(
                                useTransferBackwardPrediction,
                                router,
                                tact,
                                task,
                                statefulProcessor,
                                taskFinishTimeProvider,
                                processorCopyProvider
                        )
                ))
                //Get {StatefulProcessor, startTime} with the lowest (best) startTime (sort by
                // startTime, asc)
                .min(Comparator.comparing(ProcessorWithTaskEstimate::getStartTime));
    }

    private static TaskWithTransfersEstimate getEstimate(
            boolean useTransferBackwardPrediction,
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            StatefulProcessor processor,
            TaskFinishTimeProvider taskFinishTimeProvider,
            LockedStatefulProcessorProvider processorProvider) {

        int minStartTime = TaskOnProcessorWithoutTransfersMinStartDefiner.needTransfersFromDependencies(
                tact, task, processor
        );
        if(minStartTime >= 0) {
            return new TaskWithTransfersEstimate(minStartTime, null);
        } else {
            //Need to transfer data from parent tasks located on other processors. Time of arrival of the last data
            // transfer => time of start
            TaskWithTransfersEstimate taskWithTransfersEstimate = TaskWithTransferEstimatePerformer.getMinAvailableStartTimeOfTaskDependingOnTransfers(
                    tact,
                    taskFinishTimeProvider,
                    task,
                    processor,
                    router,
                    processorProvider,
                    useTransferBackwardPrediction
            );
            int startTime = taskWithTransfersEstimate.start;
            //We need to check result
            if(startTime < tact) {
                throw new IllegalStateException(
                        "StartTime must be >= tact"
                                + ". Tact = " + tact
                                + ", startTime = " + startTime
                                + "\nTask = " + task
                                + "\nProcessor = " + processor
                );
            }
            return taskWithTransfersEstimate;
        }
    }
}
