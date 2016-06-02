package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
@FunctionalInterface
public interface TasksGraphSimplifier {

    TasksGraph convertToGraph(TasksGraphBundle tasksGraphBundle);

}
