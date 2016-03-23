package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.pzcs.model.primitives.Node;

/**
 * Created by Yurii on 3/10/2016.
 */
public class Task implements Node {

    private static final long serialVersionUID = 4635855386417329784L;

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

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "T#" + id + "=" + weight;
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
