package edu.kpi.nesteruk.pzcs.graph.validation;

import org.jgrapht.Graph;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CompositeGraphValidator implements GraphValidator {

    private final GraphValidator[] graphValidators;

    public CompositeGraphValidator(GraphValidator... graphValidators) {
        this.graphValidators = graphValidators;
    }

    @Override
    public boolean isValid(Graph graph) {
        for (GraphValidator graphValidator : graphValidators) {
            if(!graphValidator.isValid(graph)) {
                return false;
            }
        }
        return true;
    }
}
