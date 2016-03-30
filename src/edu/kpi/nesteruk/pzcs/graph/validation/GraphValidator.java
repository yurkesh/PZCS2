package edu.kpi.nesteruk.pzcs.graph.validation;

import org.jgrapht.Graph;

/**
 * Created by Anatolii on 2016-03-13.
 */
public interface GraphValidator<V, E> {

    boolean isValid(Graph<V, E> graph);

}
