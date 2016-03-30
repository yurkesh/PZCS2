package edu.kpi.nesteruk.pzcs.graph.validation;

import org.jgrapht.Graph;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CompositeGraphValidator<V, E> implements GraphValidator<V, E> {

    private final GraphValidator<V, E>[] graphValidators;

    @SafeVarargs
    public CompositeGraphValidator(GraphValidator<V, E>... graphValidators) {
        this.graphValidators = graphValidators;
    }

    @Override
    public boolean isValid(Graph<V, E> graph) {
        for (GraphValidator<V, E> graphValidator : graphValidators) {
            if(!graphValidator.isValid(graph)) {
                return false;
            }
        }
        return true;
    }
}
