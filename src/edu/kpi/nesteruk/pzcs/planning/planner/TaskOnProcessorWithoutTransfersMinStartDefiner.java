package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-06-02.
 */
class TaskOnProcessorWithoutTransfersMinStartDefiner {

    /**
     * @return the lowest time (tact) of task's start on specified processor. <b>Return -1 if any transfer is needed</b>
     */
    public static int needTransfersFromDependencies(
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
                //Need to transfer data from parent tasks located on other processors
                startTime = -1;
            }
        }
        return startTime;
    }

}
