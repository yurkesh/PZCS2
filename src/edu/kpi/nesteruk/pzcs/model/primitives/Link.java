package edu.kpi.nesteruk.pzcs.model.primitives;

import java.io.Serializable;

/**
 * Created by Anatolii on 2016-03-13.
 */
public interface Link<N> extends HasWeight, Serializable {
    String getFirst();
    String getSecond();
    boolean isIncident(HasId node);
}
