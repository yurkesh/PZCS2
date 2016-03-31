package edu.kpi.nesteruk.misc;

import java.util.function.Predicate;

/**
 * Created by Yurii on 2016-03-14.
 */
public interface IdPool<Id> {
    @Deprecated
    Id obtainId();

    Id obtainId(Predicate<Id> idPredicate);

    boolean obtainId(Id id);

    void releaseId(Id id);
}
