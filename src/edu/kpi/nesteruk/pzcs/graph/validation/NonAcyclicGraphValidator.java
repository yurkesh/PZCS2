package edu.kpi.nesteruk.pzcs.graph.validation;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;

/**
 * Created by Anatolii on 2016-03-20.
 */
public class NonAcyclicGraphValidator<V, E> implements GraphValidator<V, E> {

    @Override
    public boolean isValid(Graph<V, E> graph) {
        return !new CycleDetector<>((DirectedGraph<V, E>) graph).detectCycles();
    }
}
