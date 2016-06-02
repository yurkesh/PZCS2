package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.pzcs.graph.misc.WeightedGraphSupport;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Created by Anatolii Bed on 2016-05-15.
 */
public class TasksGraph extends SimpleDirectedWeightedGraph<String, String> {

    private final WeightedGraphSupport weightedGraphSupport = new WeightedGraphSupport();

    public TasksGraph(EdgeFactory<String, String> ef) {
        super(ef);
    }

    @Override
    public void setEdgeWeight(String s, double weight) {
        weightedGraphSupport.setEdgeWeight(s, weight);
    }

    @Override
    public double getEdgeWeight(String s) {
        return weightedGraphSupport.getEdgeWeight(s);
    }
}
