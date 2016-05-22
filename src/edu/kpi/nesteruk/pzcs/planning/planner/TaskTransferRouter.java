package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.planning.state.StatefulProcessor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by Yurii on 2016-05-22.
 */
@FunctionalInterface
interface TaskTransferRouter {

    /**
     *
     * @param src source processor
     * @param dest destination processor
     * @return hops
     */
    List<CongenericLink<Processor>> getTransfersBetweenProcessors(Processor src, Processor dest);

    /**
     * @return function that accepts source and destination processors and returns list of links between processors that
     * provide minimal total weight
     */
    static TaskTransferRouter getRouter(
            ProcessorsGraph processorsGraph,
            ProcessorsGraphBundle processorsGraphBundle,
            Map<String, StatefulProcessor> statefulProcessorMap) {
        return null;
    }
}
