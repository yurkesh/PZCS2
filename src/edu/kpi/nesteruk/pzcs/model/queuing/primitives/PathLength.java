package edu.kpi.nesteruk.pzcs.model.queuing.primitives;

/**
 * Created by Yurii on 2016-04-07.
 */
public class PathLength {
    public final int inWeight;
    public final int inNumberOfNodes;

    public PathLength(int inWeight, int inNumberOfNodes) {
        this.inWeight = inWeight;
        this.inNumberOfNodes = inNumberOfNodes;
    }

    @Override
    public String toString() {
        return "PathLength{" +
                "inWeight=" + inWeight +
                ", inNumberOfNodes=" + inNumberOfNodes +
                '}';
    }

    public int getInWeight() {
        return inWeight;
    }

    public int getInNumberOfNodes() {
        return inNumberOfNodes;
    }
}
