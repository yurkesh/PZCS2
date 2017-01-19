package edu.kpi.nesteruk.pzcs.graph.mutating;

import org.jgrapht.Graph;

/**
 * Created by Yurii on 2017-01-09.
 */
public interface GraphMutator<G extends Graph<?, ?>> {
    G mutate(G graph);
}
