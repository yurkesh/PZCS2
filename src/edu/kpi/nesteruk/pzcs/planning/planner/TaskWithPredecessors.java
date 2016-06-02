package edu.kpi.nesteruk.pzcs.planning.planner;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Anatolii Bed on 2016-05-16.
 */
interface TaskWithPredecessors {

    /**
     * @return id of holding task
     */
    String getTaskId();

    /**
     * @return {predecessor_id -> link_id}
     */
    Map<String, String> getTransfersFromPredecessors();

    static TaskWithPredecessors getTaskWithPredecessorsSupplier(
            DirectPredecessorsProvider predecessorsProvider,
            String taskId,
            TransfersFromPredecessorProvider transfersFromPredecessorProvider) {

        return new TaskWithPredecessors() {
            @Override
            public String getTaskId() {
                return taskId;
            }

            @Override
            public Map<String, String> getTransfersFromPredecessors() {
                return predecessorsProvider.getDirectPredecessorsOfTask(taskId).stream()
                        .collect(Collectors.toMap(
                                Function.<String>identity(),
                                transfersFromPredecessorProvider::getTransferId
                        ));
            }

            @Override
            public String toString() {
                return taskId;
            }
        };
    }
}
