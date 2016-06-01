package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.pzcs.graph.misc.WeightedGraphSupport;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Created by Yurii on 2016-05-16.
 */
public class ProcessorsGraph extends SimpleWeightedGraph<String, String> {

    private final WeightedGraphSupport weightedGraphSupport = new WeightedGraphSupport();

    public ProcessorsGraph(EdgeFactory<String, String> ef) {
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
