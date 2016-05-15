package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.params.PlanningParams;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;

import java.util.List;
import java.util.Map;

/**
 * Created by Yurii on 2016-05-15.
 */
public interface Planner {

    Map<Processor, List<Pair<Task, Parcel>>> getPlannedWork(
            ProcessorsGraphBundle processorsGraphBundle,
            TasksGraphBundle tasksGraphBundle,
            PlanningParams params
    );
}
