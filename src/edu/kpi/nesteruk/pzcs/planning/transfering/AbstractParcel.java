package edu.kpi.nesteruk.pzcs.planning.transfering;

import edu.kpi.nesteruk.pzcs.model.primitives.HasId;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Anatolii Bed on 2016-04-20.
 */
public abstract class AbstractParcel implements Parcel {

    public final String from;
    public final String to;

    /**
     * Task id
     */
    public final String id;

    private final Set<String> hops = new LinkedHashSet<>();

    public AbstractParcel(String from, String to, String taskId) {
        this.from = from;
        this.to = to;
        this.id = taskId;
    }

    public AbstractParcel(HasId from, HasId to, HasId task) {
        this(from.getId(), to.getId(), task.getId());
    }

    public void addHop(HasId hop) {
        boolean unique = hops.add(hop.getId());
        if(!unique) {
            throw new IllegalArgumentException("Hop already present in hops");
        }
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String geTo() {
        return to;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
