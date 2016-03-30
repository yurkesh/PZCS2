package edu.kpi.nesteruk.misc;

/**
 * Created by Anatolii on 2016-03-14.
 */
public interface Wrapper<Raw, Backed> {
    Backed wrap(Raw raw);
    Raw unwrap(Backed backed);
}
