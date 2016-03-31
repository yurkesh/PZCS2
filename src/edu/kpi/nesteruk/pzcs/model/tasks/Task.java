package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.pzcs.model.primitives.Node;

import java.util.Locale;
import java.util.function.Function;

/**
 * Created by Yurii on 3/10/2016.
 */
public class Task implements Node {

    private static final long serialVersionUID = 4635855386417329784L;

    public static final String DEFAULT_TO_STRING_FORMAT = "T#%s/%s";
    public static String TO_STRING_FORMAT = DEFAULT_TO_STRING_FORMAT;
    public static Function<Task, String> STRING_FORMATTER = task -> String.format(Locale.US, TO_STRING_FORMAT, task.id, task.weight);

    public final String id;
    public final int weight;

    public Task(String id, int weight) {
        this.id = id;
        this.weight = weight;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return STRING_FORMATTER.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id != null ? id.equals(task.id) : task.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
