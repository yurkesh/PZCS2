package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;

/**
 * Created by Anatolii Bed on 2016-05-22.
 */
@FunctionalInterface
interface TaskWithPredecessorsProvider {

    /**
     * @param taskId id of task
     * @return container of task with its predecessors
     */
    TaskWithPredecessors getTaskWithItsPredecessors(String taskId);

    static TaskWithPredecessorsProvider getTaskWithPredecessorsMapper(
            TasksGraph tasksGraph,
            DirectPredecessorsProvider tasksPredecessorsProvider) {

        return task -> TaskWithPredecessors.getTaskWithPredecessorsSupplier(
                tasksPredecessorsProvider,
                task,
                TransfersFromPredecessorProvider.getTransfersFromPredecessorProvider(tasksGraph, task)
        );
    }
}
