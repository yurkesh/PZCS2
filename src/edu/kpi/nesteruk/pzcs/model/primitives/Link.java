package edu.kpi.nesteruk.pzcs.model.primitives;

/**
 * Created by Yurii on 2016-03-13.
 */
public interface Link<N extends Node> {
    N getFirst();
    N getSecond();
    int getWeight();
}
