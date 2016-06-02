package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;

import java.util.List;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
public interface TasksToScheduleSorter {

    List<String> sort(TasksGraphBundle tasksGraphBundle);

}
