package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.misc.FunctionWithCache;
import edu.kpi.nesteruk.misc.Tuple;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraph;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Anatolii Bed on 2016-05-22.
 */
@FunctionalInterface
interface TaskTransferRouter {

    /**
     *
     * @param srcProcessorId id of source processor
     * @param destProcessorId id of destination processor
     * @return list of available routes, desc by total length
     */
    List<List<CongenericLink<Processor>>> getAllRoutesBetweenProcessors(String srcProcessorId, String destProcessorId);

    /**
     * @return function that accepts source and destination processors and returns list of links between processors that
     * provide minimal total weight
     */
    static TaskTransferRouter getRouter(
            ProcessorsGraph processorsGraph,
            ProcessorsGraphBundle processorsGraphBundle) {

        FunctionWithCache<Tuple<String>, List<List<CongenericLink<Processor>>>> cachedPathFinder = new FunctionWithCache<>(
                //{srcProcessorId, destProcessorId}
                srcAndDestProcessorsIds -> {
                    //Get shortest path
                    GraphPath<String, String> path = new FloydWarshallShortestPaths<>(processorsGraph).getShortestPath(
                            srcAndDestProcessorsIds.first, srcAndDestProcessorsIds.second
                    );
                    //Check it is present
                    if (path == null) {
                        throw new IllegalStateException("Cannot find shortest path between processors: " +
                                "src = " + srcAndDestProcessorsIds.first + ", dest = " + srcAndDestProcessorsIds.second);
                    }
                    Map<String, CongenericLink<Processor>> linksMap = processorsGraphBundle.getLinksMap();
                    //Convert to list of CongenericLinks
                    return Collections.singletonList(
                            path.getEdgeList().stream()
                                    .map(linksMap::get)
                                    .collect(Collectors.toList())
                    );
                }
        );

        return (src, dest) -> cachedPathFinder.apply(Tuple.of(src, dest));
    }
}
