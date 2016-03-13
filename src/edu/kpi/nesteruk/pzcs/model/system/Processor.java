package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.pzcs.model.primitives.Node;

/**
 * Created by Yurii on 2016-03-13.
 */
public class Processor implements Node {

    public final String id;

    public Processor(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "P[" + id + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Processor processor = (Processor) o;

        return id != null ? id.equals(processor.id) : processor.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
