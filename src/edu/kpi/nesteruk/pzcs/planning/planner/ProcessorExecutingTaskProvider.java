package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.StatefulProcessor;

import java.util.Collection;

/**
 * Created by Yurii on 2016-05-22.
 */
@FunctionalInterface
interface ProcessorExecutingTaskProvider {

    /**
     * @param taskId id of task
     * @return id of processor that holds specified task
     */
    String getIdOfProcessorExecutingTask(String taskId);

    static ProcessorExecutingTaskProvider getTaskToExecutingProcessorMapper(Collection<StatefulProcessor> statefulProcessors) {
        return task -> statefulProcessors.stream()
                .filter(statefulProcessor -> statefulProcessor.hasTask(task))
                .findFirst()
                .get()
                .getId();
    }
}
