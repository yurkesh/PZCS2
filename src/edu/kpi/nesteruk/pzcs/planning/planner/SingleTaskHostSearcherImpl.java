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
            TaskFinishTimeProvider taskFinishTimeProvider) {

        int startTime;

        if(!task.hasDependencies()) {
            //If task has no dependencies (it is 'in the top of tasks graph')
            if(processor.isFree(tact)) {
                //And current processor is free now
                //Return current tact
                startTime = tact;
            } else {
                //Processor can start execute task right after becoming free
                startTime = processor.getFreeTime();
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
                // we can start it right after processor becomes free
                startTime = processor.getFreeTime();
            } else {
                startTime = getMinAvailableStartTimeOfTaskDependingOnTransfers(
                        tact,
                        taskFinishTimeProvider,
                        task,
                        processor,
                        router
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
            TaskTransferRouter router) {

        // TODO: 2016-05-24 Add metric customization
        TaskDependencyTransferPriorityComparator taskTransferPriorityComparator =
                getTaskDependencyTransferPriorityComparator(tact, taskFinishTimeProvider, task, processor);

        //Get and process all dependencies of current task that are hosted not on current processor
        List<TaskHostedDependency> nonOnThisProcessorDependencies =
                task.getAllDependenciesExcluding(processor.getProcessorId());

        return nonOnThisProcessorDependencies.stream()
                //Sort all these dependencies tasks by their metric
                .sorted((t1, t2) -> taskTransferPriorityComparator.compare(t1.getSourceTaskId(), t2.getSourceTaskId()))
                //For all dependencies find the time of transfer arrival on this processor
                .map(taskHostedDependency -> {
                    List<List<CongenericLink<Processor>>> allRoutes = router.getAllRoutesBetweenProcessors(
                            taskHostedDependency.getProcessorId(),
                            processor.getProcessorId()
                    );
                    //TODO schedule and find the best route
                    return Integer.MAX_VALUE;
                })
                .mapToInt(Integer::intValue)
                //Get max time of transfer arrival -> this is the time of start of task, because task cannot be
                // started before all its dependencies transfers arrive
                .max()
                .getAsInt();
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
