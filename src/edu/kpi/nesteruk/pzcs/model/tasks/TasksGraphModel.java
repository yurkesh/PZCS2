package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.pzcs.model.common.*;
import edu.kpi.nesteruk.pzcs.model.common.primitive.DirectedLink;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by Yurii on 3/10/2016.
 */
public class TasksGraphModel implements GraphModel {

    private IdPool idPool = new IdPool();

    private Map<String, Task> tasksMap = new LinkedHashMap<>();

    private Set<DirectedLink<Task>> edges = new LinkedHashSet<>();

    @Override
    public NodeBuilder getNodeBuilder() {
        return new CommonNodeBuilder(
                true,
                idPool::obtainID,
                id -> {
                    boolean unique = !tasksMap.containsKey(id);
                    if(unique) {
                        idPool.obtainId(id);
                    } else {
                        idPool.releaseId(id);
                    }
                    return unique;
                },
                this::createTask
        );
    }

    public String createTask(String id, int weight) {
        if(weight <= 0) {
            return null;
        }
        Task task = new Task(id, weight);
        tasksMap.put(id, task);
        return task.toString();
    }

    @Override
    public void deleteNode(String id) {
        tasksMap.computeIfPresent(id, (s, task) -> {
            idPool.releaseId(task.id);
            return null;
        });
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return new CommonLinkBuilder(true, this::canConnect, this::connect);
    }

    public boolean canConnect(String srcId, String destId) {
        return true;
    }

    public String connect(String srcId, String destId, int weight) {
        DirectedLink<Task> link = new DirectedLink<>(tasksMap.get(srcId), tasksMap.get(destId), weight);
        boolean added = edges.add(link);
        if(added) {
            return link.toString();
        } else {
            return null;
        }
    }

    @Override
    public boolean validate() {
        return true;
    }
}
