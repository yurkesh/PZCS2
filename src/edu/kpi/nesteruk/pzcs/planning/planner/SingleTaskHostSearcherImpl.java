package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.state.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
public class SingleTaskHostSearcherImpl implements SingleTaskHostSearcher {

    @Override
    public int getStartTime(
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
                //Processor can start execute task immediately after becoming free
                startTime = processor.getReleaseTime();
            }
        } else {
            List<String> dependencies = task.dependencySources.stream()
                    .map(TaskHostedDependency::getSourceTaskId)
                    .collect(Collectors.toList());

            boolean processorHasAllDependencies = processor.hasAllTasks(dependencies);
            boolean allTasksAreOnProcessor = task.allAreOnProcessor(processor.getProcessorId());
            if(processorHasAllDependencies ^ allTasksAreOnProcessor) {
                //Just to check that processor is in correct state
                throw new AssertionError("processorHasAllDependencies = " + processorHasAllDependencies + ", allTasksAreOnProcessor = " + allTasksAreOnProcessor);
            }

            if(allTasksAreOnProcessor) {
                // If all dependencies of specified task were executed on current processor
                // we can start it immediately after processor becomes free
                startTime = processor.getReleaseTime();
            } else {
                //Need to transfer data from parent tasks located on other processors. Time of arrival of the last data
                // transfer => time of start
                startTime = getMinAvailableStartTimeOfTaskDependingOnTransfers(
                        tact,
                        taskFinishTimeProvider,
                        task,
                        processor,
                        router,
                        processorProvider
                );
            }
        }
        return startTime;
    }

    private static int getMinAvailableStartTimeOfTaskDependingOnTransfers(
            int tact,
            TaskFinishTimeProvider taskFinishTimeProvider,
            TaskWithHostedDependencies task,
            StatefulProcessor processor,
            TaskTransferRouter router,
            LockedStatefulProcessorProvider processorProvider) {

        // TODO: 2016-05-24 Add metric customization
        TaskDependencyTransferPriorityComparator taskTransferPriorityComparator =
                getTaskDependencyTransferPriorityComparator(tact, taskFinishTimeProvider, task, processor);

        //Get and process all dependencies of current task that are hosted not on current processor
        List<TaskHostedDependency> nonOnThisProcessorDependencies =
                task.getAllDependenciesExcluding(processor.getProcessorId());

        return nonOnThisProcessorDependencies.stream()
                //Sort all these dependencies tasks by their metric
                .sorted((t1, t2) -> taskTransferPriorityComparator.compare(t1.getSourceTaskId(), t2.getSourceTaskId()))
                //For all dependencies find the time of transfer arrival on this processor:
                .mapToInt(taskHostedDependency -> {
                    //Get all possible routes for this transfer.
                    List<List<CongenericLink<Processor>>> allRoutes = router.getAllRoutesBetweenProcessors(
                            taskHostedDependency.getProcessorId(),
                            processor.getProcessorId()
                    );
                    //Between all routes
                    return allRoutes.stream()
                            //Find the best transfer time
                            .mapToInt(route -> getTransferTime(
                                    tact,
                                    taskHostedDependency.getTransferWeight(),
                                    route,
                                    processorProvider
                            ))
                            .max()
                            .getAsInt();
                })
                //Get max time of transfer arrival -> this is the time of start of task, because task cannot be
                // started before all its dependencies transfers arrive
                .max()
                .getAsInt();
    }

    private static int getTransferTime(
            int tact,
            int transferWeight,
            List<CongenericLink<Processor>> route,
            LockedStatefulProcessorProvider processorProvider) {

        //For all links in route:
        return route.stream()
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
                            //Get time of receiving of message
                            return sender.sendTo(startTact, transferWeight, receiver);
                        },
                        //Return sum
                        Integer::sum
                );
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
                        processor.getProcessorId()
                ),
                finishTimeProvider
        );
    }
}
