package edu.kpi.nesteruk.pzcs.model;

import java.io.Serializable;

/**
 * Created by Yurii on 3/10/2016.
 */
public class Task implements Serializable {
    public final String id;

    public Task(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + "'" +
                '}';
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
