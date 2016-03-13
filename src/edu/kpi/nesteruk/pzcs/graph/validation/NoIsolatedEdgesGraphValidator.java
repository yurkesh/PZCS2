package edu.kpi.nesteruk.pzcs.graph.validation;

import edu.kpi.nesteruk.pzcs.graph.GraphData;

import java.util.Set;

/**
 * Created by Yurii on 2016-03-13.
 */
public class NoIsolatedEdgesGraphValidator implements GraphValidator {

    @Override
    public boolean isValid(GraphData graph) {
        Set vertices = graph.getVertices();
        if(vertices.size() == 1) {
            return true;
        }
        boolean hasConnection;
        for (Object src : vertices) {
            hasConnection = false;
            for (Object dest : vertices) {
                if(src != dest) {
                    if(graph.containsEdge(src, dest) || graph.containsEdge(dest, src)) {
                        hasConnection = true;
                        break;
                    }
                }
            }
            if(!hasConnection) {
                return false;
            }
        }
        return true;
    }
}
