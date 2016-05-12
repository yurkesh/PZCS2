package edu.kpi.nesteruk.pzcs.planning.transfering;

import edu.kpi.nesteruk.pzcs.model.primitives.HasId;

/**
 * Created by Anatolii Bed on 2016-04-20.
 */
public interface Parcel extends HasId {
    String getFrom();
    String geTo();
}
