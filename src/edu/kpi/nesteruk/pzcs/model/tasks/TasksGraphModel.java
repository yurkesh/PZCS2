package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.pzcs.model.common.*;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.primitives.IdAndValue;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yurii on 3/10/2016.
 */
public class TasksGraphModel implements GraphModel {

    private IdPool<String> idPool = new CommonIdPool();

    private Map<String, Task> tasksMap = new LinkedHashMap<>();

    private Map<String, DirectedLink<Task>> edges = new LinkedHashMap<>();

    @Override
    public NodeBuilder getNodeBuilder() {
        return new CommonNodeBuilder(
                true,
                idPool::obtainID,
                id -> {
                    id = id.toLowerCase();
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

    public IdAndValue createTask(String id, int weight) {
        if(weight <= 0) {
            return null;
        }
        Task task = new Task(id, weight);
        tasksMap.put(id, task);
        return new IdAndValue(id, task.toString());
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

    public IdAndValue connect(String srcId, String destId, int weight) {
        DirectedLink<Task> link = new DirectedLink<>(tasksMap.get(srcId), tasksMap.get(destId), weight);
        String linkId = link.toString();
        return edges.putIfAbsent(linkId, link) == null ? new IdAndValue(linkId, link.toString()) : null;
    }

    @Override
    public void deleteLink(String id) {
        edges.remove(id);
    }

    @Override
    public boolean validate() {
        return true;
    }
}
