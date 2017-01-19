package edu.kpi.nesteruk.pzcs.model.tasks;

import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yurii on 2016-05-15.
 */
public class TasksGraphBundle extends GraphModelBundle<Task, DirectedLink<Task>> {
    public TasksGraphBundle(Map<String, Task> nodesMap, Map<String, DirectedLink<Task>> linksMap) {
        super(nodesMap, linksMap);
    }
}
