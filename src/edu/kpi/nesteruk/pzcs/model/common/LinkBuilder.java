package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;

import java.util.Optional;

/**
 * Created by Anatolii on 2016-03-13.
 */
public interface LinkBuilder {
    boolean beginConnect(String srcId, String destId);
    boolean needWeight();
    void setWeight(Integer weightIn);
    Optional<IdAndValue> finishConnect();
}
