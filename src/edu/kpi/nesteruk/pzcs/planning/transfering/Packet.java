package edu.kpi.nesteruk.pzcs.planning.transfering;

import edu.kpi.nesteruk.pzcs.model.primitives.HasWeight;

/**
 * Created by Anatolii Bed on 2016-04-20.
 */
public class Packet extends AbstractParcel implements HasWeight {

    public final int weight;

    public Packet(String from, String to, String id, int weight) {
        super(from, to, id);
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Packet{" + super.toString() + ", weight = " + weight + "}";
    }
}
