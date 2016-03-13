package edu.kpi.nesteruk.misc;

/**
 * Created by Yurii on 2016-03-14.
 */
public interface Wrapper<Raw, Backed> {
    Backed wrap(Raw raw);
    Raw unwrap(Backed backed);
}
