package edu.kpi.nesteruk.pzcs.model.common;

import java.util.Optional;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface NodeBuilder {
    /**
     *
     * @return proposed ID
     */
    String beginBuild();
    boolean setId(String id);
    boolean needWeight();
    void setWeight(int weight);
    Optional<String> finishBuild();
}
