package edu.kpi.nesteruk.pzcs.planning.tasks;

import java.util.List;

/**
 * Created by Yurii on 2016-05-18.
 */
public class TaskWithHostedDependencies {

    /**
     * Id of concrete task
     */
    public final String task;

    /**
     * Dependencies of this task that contain info about processor hosting them
     */
    public final List<TaskHostedDependency> dependencySources;

    public TaskWithHostedDependencies(String task, List<? extends TaskHostedDependency> dependencySources) {
        this.task = task;
        this.dependencySources = (List<TaskHostedDependency>) dependencySources;
    }

    @Override
    public String toString() {
        return "TaskWithHostedDependencies{" +
                "task='" + task + '\'' +
                ", dependencySources=" + dependencySources +
                '}';
    }

    public String getTaskId() {
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
}
