package edu.kpi.nesteruk.pzcs.model.tasks;

import org.jgrapht.DirectedGraph;
import org.jgrapht.WeightedGraph;

/**
 * Created by Yurii on 2016-05-15.
 */
public interface TasksGraph extends WeightedGraph<String, String>, DirectedGraph<String, String> {
}
