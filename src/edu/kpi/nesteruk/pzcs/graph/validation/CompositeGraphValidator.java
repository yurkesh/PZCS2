package edu.kpi.nesteruk.pzcs.graph.validation;

import edu.kpi.nesteruk.pzcs.graph.GraphData;

/**
 * Created by Yurii on 2016-03-13.
 */
public class CompositeGraphValidator implements GraphValidator {

    private final GraphValidator[] graphValidators;

    public CompositeGraphValidator(GraphValidator... graphValidators) {
        this.graphValidators = graphValidators;
    }

    @Override
    public boolean isValid(GraphData graph) {
        for (GraphValidator graphValidator : graphValidators) {
            if(!graphValidator.isValid(graph)) {
                return false;
            }
        }
        return true;
    }
}
