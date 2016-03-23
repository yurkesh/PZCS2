package edu.kpi.nesteruk.pzcs.presenter.common;

import edu.kpi.nesteruk.misc.Tuple;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Yurii on 2016-03-23.
 */
public class GraphPresenterSerializable implements Serializable {
    private Map<String, Tuple<Integer>> nodeIdToItsCoordinates;

    public GraphPresenterSerializable() {}

    public GraphPresenterSerializable(Map<String, Tuple<Integer>> nodeIdToItsCoordinates) {
        this.nodeIdToItsCoordinates = nodeIdToItsCoordinates;
    }

    @Override
    public String toString() {
        return "GraphPresenterSerializable{" +
                "nodeIdToItsCoordinates=" + nodeIdToItsCoordinates +
                '}';
    }
}
