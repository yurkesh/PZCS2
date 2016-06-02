package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;

import java.util.function.Function;

/**
 * Created by Anatolii Bed on 2016-05-22.
 */
@FunctionalInterface
interface TransfersFromPredecessorProvider {
    String getTransferId(String predecessor);

    static TransfersFromPredecessorProvider getTransfersFromPredecessorProvider(TasksGraph tasksGraph, String task) {
        return new FunctionWithCache<>(
                (Function<String, String>) predecessor -> tasksGraph.getEdge(predecessor, task)
        )::apply;
    }
}
