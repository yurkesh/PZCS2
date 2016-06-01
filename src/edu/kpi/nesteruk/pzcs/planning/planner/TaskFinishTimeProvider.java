package edu.kpi.nesteruk.pzcs.planning.planner;

/**
 * Created by Yurii on 2016-05-24.
 */
@FunctionalInterface
interface TaskFinishTimeProvider {

    int getFinishTimeOfTask(String taskId);

}
