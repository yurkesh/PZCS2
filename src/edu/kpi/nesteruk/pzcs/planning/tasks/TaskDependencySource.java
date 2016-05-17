package edu.kpi.nesteruk.pzcs.planning.tasks;

/**
 * Created by Yurii on 2016-05-18.
 */
public interface TaskDependencySource {
    String getSourceTaskId();

    String getTargetTaskId();

    String getProcessorId();

    int getTransferWeight();
}
