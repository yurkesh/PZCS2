package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.params.ProcessorsParams;

/**
 * Created by Anatolii Bed on 2016-05-15.
 */
public interface Planner {

    SchedulingResult getPlannedWork(
            ProcessorsGraphBundle processorsGraphBundle,
            TasksGraphBundle tasksGraphBundle,
            ProcessorsParams params
    );
}
