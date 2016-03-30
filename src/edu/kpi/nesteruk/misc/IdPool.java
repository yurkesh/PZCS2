package edu.kpi.nesteruk.misc;

/**
 * Created by Anatolii on 2016-03-14.
 */
public interface IdPool<Id> {
    Id obtainID();

    boolean obtainId(Id id);

    void releaseId(Id id);
}
