package edu.kpi.nesteruk.pzcs.model.common;

import java.util.Optional;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface LinkBuilder {
    boolean beginConnect(String srcId, String destId);
    boolean needWeight();
    void setWeight(Integer weightIn);
    Optional<String> finishConnect();
}
