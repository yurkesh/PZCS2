package edu.kpi.nesteruk.pzcs.graph.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatolii Bed on 2016-05-22.
 */
public class WeightedGraphSupport {

    private final Map<String, Double> edgeWeightMap = new HashMap<>();

    public void setEdgeWeight(String s, double weight) {
        edgeWeightMap.put(s, weight);
    }

    public double getEdgeWeight(String s) {
        Double weight = edgeWeightMap.get(s);
        if(weight == null) {
            throw new IllegalArgumentException("Cannot get weight of edge = '" + s + "'");
        }
        return weight;
    }

}
