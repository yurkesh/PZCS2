package edu.kpi.nesteruk.pzcs.model.primitives;

/**
 * Created by Yurii on 2016-04-20.
 */
public interface HasId extends Comparable<HasId> {
    String getId();

    @Override
    default int compareTo(HasId o) {
        return getId().compareToIgnoreCase(o.getId());
    }
}
