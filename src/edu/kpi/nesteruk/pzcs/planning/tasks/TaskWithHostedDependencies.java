package edu.kpi.nesteruk.pzcs.planning.tasks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-05-18.
 */
public class TaskWithHostedDependencies {

    /**
     * Id of concrete task
     */
    public final String task;

    public final int weight;

    /**
     * Dependencies of this task that contain info about processor hosting them
     */
    public final List<TaskHostedDependency> dependencySources;
    /**
     * The same as {@link #dependencySources} but in map {TaskHostedDependency#sourceTaskId -> TaskHostedDependency}
     */
    public final Map<String, TaskHostedDependency> dependencySourcesMap;

    public TaskWithHostedDependencies(String task, int weight, List<TaskHostedDependency> dependencySources) {
        this.task = task;
        this.weight = weight;
        this.dependencySources = dependencySources;
        this.dependencySourcesMap = Collections.unmodifiableMap(dependencySources.stream().collect(Collectors.toMap(
                TaskHostedDependency::getSourceTaskId,
                Function.identity()
        )));
    }

    @Override
    public String toString() {
        return "TaskWithHostedDependencies{" +
                "task='" + task + '\'' +
                "weight=" + task +
                ", dependencySources=" + dependencySources +
                '}';
    }

    public String getId() {
        return task;
    }

    public boolean hasDependencies() {
        return !dependencySources.isEmpty();
    }

    /**
     * @param processor to check
     * @return true if all dependencies (predecessors) are hosted on the specified processor
     */
    public boolean allAreOnProcessor(String processor) {
        return !dependencySources.stream()
                .map(TaskHostedDependency::getProcessorId)
                .filter(processorHost -> !processor.equals(processorHost))
                .findAny()
                .isPresent();
    }

    /**
     * @param processorIdToExclude id of processor to exclude
     * @return all dependencies that are hosted on processors different than specified
     */
    public List<TaskHostedDependency> getAllDependenciesExcluding(String processorIdToExclude) {
        return dependencySources.stream()
                .filter(dependencySource -> !processorIdToExclude.equals(dependencySource.getProcessorId()))
                .collect(Collectors.toList());
    }
}
