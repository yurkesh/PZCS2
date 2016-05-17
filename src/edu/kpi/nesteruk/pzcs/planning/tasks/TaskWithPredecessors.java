package edu.kpi.nesteruk.pzcs.planning.tasks;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import org.jgrapht.Graphs;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-16.
 */
public class TaskWithPredecessors {

    public final String task;
    private final Function<String, Collection<String>> predecessorsProvider;
    private final Function<String, String> transferFromPredecessorProvider;

    public TaskWithPredecessors(String task, Function<String, Collection<String>> predecessorsProvider, Function<String, String> transferFromPredecessorProvider) {
        this.task = task;
        this.predecessorsProvider = predecessorsProvider;
        this.transferFromPredecessorProvider = transferFromPredecessorProvider;
    }

    public Collection<String> getPredecessors() {
        return predecessorsProvider.apply(task);
    }

    /**
     * @return {predecessor_id -> link_id}
     */
    public Map<String, String> getTransfersFromPredecessors() {
        return getPredecessors().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        transferFromPredecessorProvider
                ));
    }

    @Override
    public String toString() {
        return "TaskWithPredecessors{" +
                "task='" + task + '\'' +
                '}';
    }

    public static Function<String, Collection<String>> getDirectPredecessorsProvider(TasksGraph tasksGraph) {
        Function<String, Collection<String>> dependenciesProvider = new FunctionWithCache<>(
                task -> Graphs.predecessorListOf(tasksGraph, task)
        );
        return task -> new LinkedHashSet<>(dependenciesProvider.apply(task));
    }
}
