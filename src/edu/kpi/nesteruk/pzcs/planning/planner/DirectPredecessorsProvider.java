package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import org.jgrapht.Graphs;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-05-23.
 */
@FunctionalInterface
interface DirectPredecessorsProvider {

    /**
     * @param taskId id of task
     * @return ids of direct predecessors of specified task
     */
    Collection<String> getDirectPredecessorsOfTask(String taskId);

    static DirectPredecessorsProvider getDirectPredecessorsProvider(TasksGraph tasksGraph) {
        Function<String, Collection<String>> dependenciesProvider = new FunctionWithCache<>(
                task -> Graphs.predecessorListOf(tasksGraph, task)
        );
        return task -> new LinkedHashSet<>(dependenciesProvider.apply(task));
    }

}
