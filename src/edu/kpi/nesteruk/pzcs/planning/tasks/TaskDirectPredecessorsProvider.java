package edu.kpi.nesteruk.pzcs.planning.tasks;

import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraph;
import org.jgrapht.Graphs;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-05-16.
 */
@FunctionalInterface
public interface TaskDirectPredecessorsProvider extends Function<String, Collection<String>> {

}
