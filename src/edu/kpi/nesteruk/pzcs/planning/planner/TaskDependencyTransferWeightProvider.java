package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

/**
 * Created by Yurii on 2016-05-24.
 */
@FunctionalInterface
interface TaskDependencyTransferWeightProvider {

    /**
     *
     * @param taskDependencyId id of stored task
     * @return weight of transfer from dependency to stored task
     */
    int getTransferWeightFromDependency(String taskDependencyId);

    static TaskDependencyTransferWeightProvider getTaskDependencyTransferProvider(TaskWithHostedDependencies currentTask, String currentProcessor) {
        return new FunctionWithCache<String, Integer>(
                taskDependencyId -> {
                    TaskHostedDependency taskHostedDependency = currentTask.dependencySourcesMap.get(taskDependencyId);
                    if(currentProcessor.equals(taskHostedDependency.getProcessorId())) {
                        //No transfer needed if dependency is on the same processor as current task
                        return 0;
                    } else {
                        return taskHostedDependency.getTransferWeight();
                    }
                }
        )::apply;
    }

}
