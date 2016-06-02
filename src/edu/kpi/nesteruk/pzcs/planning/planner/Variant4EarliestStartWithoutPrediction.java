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
public class Variant4EarliestStartWithoutPrediction implements SingleTaskHostSearcher {

    @Override
    public Optional<ProcessorWithTaskEstimate> getStartTime(
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
                    false
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

        /*
        int startTime;

        if(!task.hasDependencies()) {
            //If task has no dependencies (it is 'in the top of tasks graph')
            if(processor.isFree(tact)) {
                //And current processor is free now
                //Return current tact
                startTime = tact;
            } else {
                //Processor can start execute task immediately after becoming free and available to process whole task
                startTime = processor.getMinStartTime(tact, task.weight);
            }
        } else {
            List<String> dependencies = task.dependencySources.stream()
                    .map(TaskHostedDependency::getSourceTaskId)
                    .collect(Collectors.toList());

            boolean processorHasAllDependencies = processor.hasAllTasks(dependencies);
            boolean allTasksAreOnProcessor = task.allAreOnProcessor(processor.getId());
            if(processorHasAllDependencies ^ allTasksAreOnProcessor) {
                //Just to check that processor is in correct state
                throw new AssertionError("processorHasAllDependencies = " + processorHasAllDependencies + ", allTasksAreOnProcessor = " + allTasksAreOnProcessor);
            }

            if(allTasksAreOnProcessor) {
                // If all dependencies of specified task were executed on current processor
                // we can start it immediately after processor becomes free and could be available to process whole task
                startTime = processor.getMinStartTime(tact, task.weight);
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
                        false
                );
                startTime = taskWithTransfersEstimate.start;
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
        return new TaskWithTransfersEstimate(startTime, null);
        */
    }

}
