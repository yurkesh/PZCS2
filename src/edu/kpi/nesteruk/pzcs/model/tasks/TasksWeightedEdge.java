package edu.kpi.nesteruk.pzcs.model.tasks;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Objects;

/**
 * Created by Anatolii Bed on 2016-05-22.
 */
public class TasksWeightedEdge extends DefaultWeightedEdge {

    public final String id;

    public TasksWeightedEdge(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TasksWeightedEdge that = (TasksWeightedEdge) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
