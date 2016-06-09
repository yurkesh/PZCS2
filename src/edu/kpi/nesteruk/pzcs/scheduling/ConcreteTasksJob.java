package edu.kpi.nesteruk.pzcs.scheduling;

import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;

/**
 * Created by Yurii on 2016-06-09.
 */
public class ConcreteTasksJob {

    public final JobCase jobCase;
    public final TasksGraphBundle tasksGraphBundle;

    public ConcreteTasksJob(JobCase jobCase, TasksGraphBundle tasksGraphBundle) {
        this.jobCase = jobCase;
        this.tasksGraphBundle = tasksGraphBundle;
    }
}
