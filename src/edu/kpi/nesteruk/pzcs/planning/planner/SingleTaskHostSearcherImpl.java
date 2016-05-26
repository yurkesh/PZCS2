package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.processors.ChannelTransfer;
import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
public class SingleTaskHostSearcherImpl implements SingleTaskHostSearcher {

    @Override
    public TaskWithTransfersEstimate getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            StatefulProcessor processor,
            TaskFinishTimeProvider taskFinishTimeProvider,
            LockedStatefulProcessorProvider processorProvider) {

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
                TaskWithTransfersEstimate taskWithTransfersEstimate = getMinAvailableStartTimeOfTaskDependingOnTransfers(
                        tact,
                        taskFinishTimeProvider,
                        task,
                        processor,
                        router,
                        processorProvider
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
    }

    private static TaskWithTransfersEstimate getMinAvailableStartTimeOfTaskDependingOnTransfers(
            int tact,
            TaskFinishTimeProvider taskFinishTimeProvider,
            TaskWithHostedDependencies task,
            StatefulProcessor processor,
            TaskTransferRouter router,
            LockedStatefulProcessorProvider processorProvider) {

        // TODO: 2016-05-24 Add metric customization
        TaskDependencyTransferPriorityComparator taskTransferPriorityComparator =
                getTaskDependencyTransferPriorityComparator(tact, taskFinishTimeProvider, task, processor);


        final Function<String, StatefulProcessor> cachedProcessorProvider = new FunctionWithCache<>(
                processorProvider::getLockedStatefulProcessor
        );
        LockedStatefulProcessorProvider localProcessorProvider = cachedProcessorProvider::apply;

        //Get and process all dependencies of current task that are hosted not on current processor
        List<TaskHostedDependency> nonOnThisProcessorDependencies =
                task.getAllDependenciesExcluding(processor.getId());

        Collection<TaskTransfer> taskTransfers = new ArrayList<>();

        int lastTransferFinished = nonOnThisProcessorDependencies.stream()
                //Sort all these dependencies tasks by their metric
                .sorted((t1, t2) -> taskTransferPriorityComparator.compare(t1.getSourceTaskId(), t2.getSourceTaskId()))
                //For all dependencies find the time of transfer arrival on this processor:
                .mapToInt(taskHostedDependency -> {
                    //Get all possible routes for this transfer.
                    List<List<CongenericLink<Processor>>> allRoutes = router.getAllRoutesBetweenProcessors(
                            taskHostedDependency.getProcessorId(),
                            processor.getId()
                    );

                    Pair<Integer, List<ProcessorTransfer>> bestEstimate = allRoutes.stream()
                            //Find the route with the best (the lowest) transfer time
                            .map(route -> getTransferTimeAndTransfers(
                                    tact,
                                    taskHostedDependency.getTransferWeight(),
                                    taskHostedDependency.getTransferId(),
                                    route,
                                    localProcessorProvider
                            ))
                            .min(Comparator.comparing(Pair::getFirst))
                            .get();

                    // TODO: 2016-05-26 Apply transfer on local processor state

                    taskTransfers.add(
                            new TaskTransfer(
                                    taskHostedDependency.getSourceTaskId(),
                                    taskHostedDependency.getDestinationTaskId(),
                                    bestEstimate.second
                            )
                    );
                    return bestEstimate.first;
                })
                //Get max time of transfer arrival -> this is the minimum time of task start on specified processor
                // (because task cannot be started before all its dependencies transfers arrive)
                .max()
                .getAsInt();

        int minStartTime = processor.getMinStartTime(lastTransferFinished, task.weight);

        return new TaskWithTransfersEstimate(minStartTime, taskTransfers);
    }

    private static Pair<Integer, List<ProcessorTransfer>> getTransferTimeAndTransfers(
            int tact,
            int transferWeight,
            String transfer,
            List<CongenericLink<Processor>> route,
            LockedStatefulProcessorProvider processorProvider) {

        List<ProcessorTransfer> processorTransfers = new ArrayList<>();
        int transferLength = route.stream()
                //For all links in route:
                //Calculate the time of completion for all transfers between processors in route for specified transfer
                // between tasks
                .reduce(
                        //Start counting from specified task
                        tact,
                        //Use previous calculated value as start for current processors pair
                        (startTact, processorsLink) -> {
                            StatefulProcessor sender = processorProvider.getLockedStatefulProcessor(
                                    processorsLink.getSource()
                            );
                            StatefulProcessor receiver = processorProvider.getLockedStatefulProcessor(
                                    processorsLink.getDestination()
                            );
                            //Get estimate of transferring message
                            ChannelTransfer channelTransfer = sender.getSendingEstimate(startTact, transferWeight, receiver);
                            //Check: we cannot use estimate that returns earlier time that we specified
                            if (channelTransfer.startTact < startTact) {
                                throw new IllegalStateException();
                            }
                            channelTransfer.setTransfer(transfer);
                            processorTransfers.add(
                                    new ProcessorTransfer(sender.getId(), receiver.getId(), channelTransfer)
                            );
                            //Return only amount of time between specified startTact and end of receiving
                            return channelTransfer.startTact + transferWeight;
                        },
                        //This will be used to combine results of parallel operations (not needed for us now)
                        Integer::sum
                );

        return Pair.create(transferLength, processorTransfers);
    }

    private static TaskDependencyTransferPriorityComparator getTaskDependencyTransferPriorityComparator(
            int tact,
            TaskFinishTimeProvider finishTimeProvider,
            TaskWithHostedDependencies task,
            StatefulProcessor processor) {

        return TaskDependencyTransferPriorityComparator.getTaskTransferPriorityCalculator(
                tact,
                TaskDependencyTransferWeightProvider.getTaskDependencyTransferProvider(
                        task,
                        processor.getId()
                ),
                finishTimeProvider
        );
    }
}
