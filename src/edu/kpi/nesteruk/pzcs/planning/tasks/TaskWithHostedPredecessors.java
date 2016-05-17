package edu.kpi.nesteruk.pzcs.planning.tasks;

import java.util.List;

/**
 * Created by Yurii on 2016-05-18.
 */
public class TaskWithHostedPredecessors {

    public final String task;
    public final List<TaskDependencySource> dependencySources;

    public TaskWithHostedPredecessors(String task, List<? extends TaskDependencySource> dependencySources) {
        this.task = task;
        this.dependencySources = (List<TaskDependencySource>) dependencySources;
    }
}
