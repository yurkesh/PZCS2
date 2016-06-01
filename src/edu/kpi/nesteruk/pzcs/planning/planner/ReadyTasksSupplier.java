package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-22.
 */
@FunctionalInterface
interface ReadyTasksSupplier {

    /**
     * @return ids of tasks that are ready to schedule (do not have predecessors or do not have non-executed dependencies)
     */
    Collection<String> getIdOfReadyTasks();

    static ReadyTasksSupplier getReadyTasksSupplier(
            TasksGraph tasksGraph,
            Collection<String> doneTasks,
            DirectPredecessorsProvider predecessorsProvider) {

        return () -> tasksGraph.vertexSet().stream()
                .map(vertex -> {
                    //Get predecessors (parents); copy into own collection
                    Collection<String> predecessors = new LinkedHashSet<>(predecessorsProvider.getDirectPredecessorsOfTask(vertex));
                    //Remove all executed tasks from predecessors
                    predecessors.removeAll(doneTasks);
                    //{task, non_executed_predecessors}
                    return Pair.create(vertex, (Set<String>) predecessors);
                })
                //Accept tasks without non-executed predecessors
                .filter(pair -> pair.second.isEmpty())
                //Get task from pair
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }
}
