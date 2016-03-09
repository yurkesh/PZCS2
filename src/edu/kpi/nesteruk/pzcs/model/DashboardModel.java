package edu.kpi.nesteruk.pzcs.model;

import edu.kpi.nesteruk.misc.Tuple;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yurii on 3/10/2016.
 */
public class DashboardModel {

    private IdPool idPool = new IdPool();

    private Map<String, Task> tasksMap = new LinkedHashMap<>();

    private Set<Tuple<String>> edges = new LinkedHashSet<>();

    public Task newTask() {
        return new Task(idPool.obtainID());
    }

    public void deleteTask(String id) {
        tasksMap.computeIfPresent(id, (s, task) -> {
            idPool.releaseId(task.id);
            return null;
        });
    }

    public boolean canConnect(String first, String second) {
        return true;
    }

    public boolean connect(String first, String second) {
        return edges.add(new Tuple<>(first, second));
    }

}
