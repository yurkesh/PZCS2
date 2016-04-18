package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Locale;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-03-13.
 */
public class Processor implements Node {

    private static final long serialVersionUID = 2750559400200735041L;

    public static final String DEFAULT_TO_STRING_FORMAT = "P[%s]/%s";
    public static String TO_STRING_FORMAT = DEFAULT_TO_STRING_FORMAT;
    public static Function<Processor, String> STRING_FORMATTER = processor -> String.format(Locale.US, TO_STRING_FORMAT, processor.id, processor.productivity);

    public final String id;
    public final int productivity;

    public Processor(String id, int productivity) {
        this.id = id;
        this.productivity = productivity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getWeight() {
        return productivity;
    }

    @Override
    public String toString() {
        return STRING_FORMATTER.apply(this);
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
