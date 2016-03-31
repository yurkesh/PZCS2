package edu.kpi.nesteruk.pzcs.model.primitives;

import java.io.Serializable;

/**
 * Created by Anatolii on 2016-03-13.
 */
public interface Link<N extends Node> extends Serializable {
    N getFirst();
    N getSecond();
    int getWeight();
    boolean isIncident(N node);
}
