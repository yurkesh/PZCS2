package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskHostedDependency;
import edu.kpi.nesteruk.pzcs.planning.tasks.TaskWithHostedDependencies;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
@FunctionalInterface
interface TaskWithHostedPredecessorsProvider {

    /**
     * @param taskWithPredecessors provider of id of task and its {predecessor -> transfer(predecessor -> task)#id}
     * @return task with its dependencies with info about processors executing them
     */
    TaskWithHostedDependencies getTaskWithHostedPredecessors(TaskWithPredecessors taskWithPredecessors);

    static TaskWithHostedPredecessorsProvider getTaskWithHostedPredecessorsProvider(
            TasksGraphBundle tasksGraphBundle,
            ProcessorExecutingTaskProvider processorExecutingTaskProvider,
            Map<String, Processor> allProcessors) {

        return taskWithPredecessors -> {
            //1) Get transfers IDs from all direct predecessors: {task -> link_between_tasks_id}
            Map<String, String> transfersFromPredecessorsIds = taskWithPredecessors.getTransfersFromPredecessors();

            //2) Get transfers from all direct predecessors: {task -> link_between_tasks}
            Map<String, DirectedLink<Task>> transfersFromPredecessors = transfersFromPredecessorsIds.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> tasksGraphBundle.getLinksMap().get(entry.getValue())
                    ));

            //3) Get processors of predecessors: {task -> processor}
            Map<String, String> tasksOnProcessors = transfersFromPredecessors.keySet().stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            processorExecutingTaskProvider::getIdOfProcessorExecutingTask
                    ));

            //4) Get sources of task - transfer from each processor without assuming links between processors
            List<TaskHostedDependency> taskSources = transfersFromPredecessors.entrySet().stream()
                    .map(entry -> new TaskHostedDependency(
                            entry.getValue(),
                            allProcessors.get(tasksOnProcessors.get(entry.getKey()))
                    ))
                    .collect(Collectors.toList());

            String taskId = taskWithPredecessors.getTaskId();
            int weight = tasksGraphBundle.getNodesMap().get(taskId).getWeight();
            return new TaskWithHostedDependencies(taskId, weight, taskSources);
        };
    }
}
