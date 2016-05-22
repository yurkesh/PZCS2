package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.state.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
public class SingleTaskScheduleSearcherImpl implements SingleTaskScheduleSearcher {

    @Override
    public int getStartTime(
            TaskTransferRouter router,
            int tact,
            TaskWithHostedDependencies task,
            StatefulProcessor processor) {

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
                // TODO: 2016-05-22 Need to use router
                startTime = Integer.MAX_VALUE;
            }
        }
        return startTime;
    }
}
