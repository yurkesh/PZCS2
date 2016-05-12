package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-04-21.
 */
public class TaskDependencies {

    private final Task task;
    private final Collection<DirectedLink<Task>> nearestDependencies;
    private final Map<Task, TaskDependencies> nearestDependenciesMap;

    public TaskDependencies(Task task, Collection<DirectedLink<Task>> nearestDependencies, Map<Task, TaskDependencies> nearestDependenciesMap) {
        this.task = task;
        this.nearestDependencies = nearestDependencies;
        this.nearestDependenciesMap = nearestDependenciesMap;
    }

    public static TaskDependencies noDependencies(Task task) {
        return new TaskDependencies(task, new ArrayList<>(), new HashMap<>());
    }

//    public static TaskDependencies forTask(Task task, Function<Task, >)

    /*
    public TaskDependencies(Task task, Collection<Task> dependenciesOnTasks, Collection<DirectedLink<Task>> dependenciesOnTransfers) {
        this.task = Objects.requireNonNull(task);
        this.dependencies = new LinkedList<>(dependenciesOnTasks);
        this.dependenciesMap = dependenciesOnTasks.stream()
                .collect(CollectionUtils.CustomCollectors.toMap(
                        Function.<Task>identity(),
                        taskDependentOn ->
                                dependenciesOnTransfers.stream()
                                        //Get link which source is current dependency
                                        .filter(link -> link.getFirst().equals(taskDependentOn))
                                        .findFirst().get(),
                        LinkedHashMap::new
                ));
    }

    public static class Builder {
        private final Task task;
        private final List<DirectedLink<Task>> dependencies = new ArrayList<>();

        public Builder(Task task) {
            this.task = task;
        }

        public boolean addDependency(DirectedLink<Task> link) {
            dependencies.stream().
        }

        public TaskDependencies build() {
            return new TaskDependencies(task, dependencies);
        }
    }

    public Optional<DirectedLink<Task>> getNextDependency() {
        return Optional.ofNullable(dependencies.peek());
    }

    public boolean removeDependency(DirectedLink<Task> dependency) {

    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskDependencies that = (TaskDependencies) o;

        return task.equals(that.task);

    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }
}
