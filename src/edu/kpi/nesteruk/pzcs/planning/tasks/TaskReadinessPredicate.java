package edu.kpi.nesteruk.pzcs.planning.tasks;

import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Yurii on 2016-05-14.
 */
public interface TaskReadinessPredicate  {
    Map<String, Boolean> getReadyTasks(TasksGraphBundle tasksGraphBundle, Collection<Task> completedTasks);
}
