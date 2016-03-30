package edu.kpi.nesteruk.pzcs.graph.validation;

import edu.kpi.nesteruk.util.CollectionUtils;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;

import java.util.Set;

/**
 * Created by Anatolii on 2016-03-24.
 */
public class NonSplitGraphValidator implements GraphValidator {

    @Override
    public boolean isValid(Graph graph) {
        Set vertexSet = graph.vertexSet();
        if(CollectionUtils.isEmpty(vertexSet)) {
            return true;
        }

        Object src = vertexSet.iterator().next();
        FloydWarshallShortestPaths paths = new FloydWarshallShortestPaths(graph);
        for (Object dest : vertexSet) {
            if(src != dest) {
                GraphPath path = paths.getShortestPath(src, dest);
                if(path == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
