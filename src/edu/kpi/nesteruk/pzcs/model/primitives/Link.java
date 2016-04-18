package edu.kpi.nesteruk.pzcs.model.primitives;

import java.io.Serializable;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface Link<N extends Node> extends HasWeight, Serializable {
    N getFirst();
    N getSecond();
    boolean isIncident(N node);
}
