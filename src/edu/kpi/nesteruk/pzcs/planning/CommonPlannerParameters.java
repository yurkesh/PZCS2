package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.common.LabWork;
import edu.kpi.nesteruk.pzcs.graph.misc.GraphUtils;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathByTimeForAllNodes3;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.system.SystemGraphModel;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.planning.planner.CommonPlanner;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcher;
import edu.kpi.nesteruk.pzcs.planning.planner.SingleTaskHostSearcherFactory;

import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Anatolii Bed on 30.05.2016.
 */
public class CommonPlannerParameters {

    private ProcessorsGraphSimplifier processorsGraphSimplifier;
    private ProcessorsToScheduleOnSorter processorsSorter;
    private TasksGraphSimplifier tasksGraphSimplifier;
    private TasksToScheduleSorter tasksSorter;
    private SingleTaskHostSearcher singleTaskPlanner;
    private Consumer<Object> logger;

    public static final int NUMBER_OF_LINKS = 1;
    public static final LabWork LAB_WORK = LabWork.LAB_7;

    public static Planner makePlanner(LabWork labWork) {
        return new CommonPlanner(
                processorsGraphBundle -> GraphUtils.makeGraphCheckAllEdgesAdded(
                        SystemGraphModel::newGraph,
                        processorsGraphBundle.getNodesMap().values(),
                        processorsGraphBundle.getLinksMap().values()
                ),
                //Sort processors by coherence (number of links)
                GraphUtils::sortVertexesByCoherence,
                tasksGraphBundle -> GraphUtils.makeGraphCheckAllEdgesAdded(
                        TasksGraphModel::makeGraph,
                        tasksGraphBundle.getNodesMap().values(),
                        tasksGraphBundle.getLinksMap().values()
                ),
                tasksGraphBundle -> new CriticalPathByTimeForAllNodes3<Task, DirectedLink<Task>>()
                        .constructQueues(tasksGraphBundle).second.stream()
                        .map(CriticalNode::getNode)
                        .map(Task::getId)
                        .collect(Collectors.toList())
                ,
                SingleTaskHostSearcherFactory.getSearcher(labWork),
                System.out::println
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonPlannerParameters that = (CommonPlannerParameters) o;

        if (processorsGraphSimplifier != null ? !processorsGraphSimplifier.equals(that.processorsGraphSimplifier) : that.processorsGraphSimplifier != null)
            return false;
        if (processorsSorter != null ? !processorsSorter.equals(that.processorsSorter) : that.processorsSorter != null)
            return false;
        if (tasksGraphSimplifier != null ? !tasksGraphSimplifier.equals(that.tasksGraphSimplifier) : that.tasksGraphSimplifier != null)
            return false;
        if (tasksSorter != null ? !tasksSorter.equals(that.tasksSorter) : that.tasksSorter != null) return false;
        if (singleTaskPlanner != null ? !singleTaskPlanner.equals(that.singleTaskPlanner) : that.singleTaskPlanner != null)
            return false;
        return logger != null ? logger.equals(that.logger) : that.logger == null;

    }

    @Override
    public int hashCode() {
        int result = processorsGraphSimplifier != null ? processorsGraphSimplifier.hashCode() : 0;
        result = 31 * result + (processorsSorter != null ? processorsSorter.hashCode() : 0);
        result = 31 * result + (tasksGraphSimplifier != null ? tasksGraphSimplifier.hashCode() : 0);
        result = 31 * result + (tasksSorter != null ? tasksSorter.hashCode() : 0);
        result = 31 * result + (singleTaskPlanner != null ? singleTaskPlanner.hashCode() : 0);
        result = 31 * result + (logger != null ? logger.hashCode() : 0);
        return result;
    }

    public ProcessorsGraphSimplifier getProcessorsGraphSimplifier() {
        return processorsGraphSimplifier;
    }

    public void setProcessorsGraphSimplifier(ProcessorsGraphSimplifier processorsGraphSimplifier) {
        this.processorsGraphSimplifier = processorsGraphSimplifier;
    }

    public ProcessorsToScheduleOnSorter getProcessorsSorter() {
        return processorsSorter;
    }

    public void setProcessorsSorter(ProcessorsToScheduleOnSorter processorsSorter) {
        this.processorsSorter = processorsSorter;
    }

    public TasksGraphSimplifier getTasksGraphSimplifier() {
        return tasksGraphSimplifier;
    }

    public void setTasksGraphSimplifier(TasksGraphSimplifier tasksGraphSimplifier) {
        this.tasksGraphSimplifier = tasksGraphSimplifier;
    }

    public TasksToScheduleSorter getTasksSorter() {
        return tasksSorter;
    }

    public void setTasksSorter(TasksToScheduleSorter tasksSorter) {
        this.tasksSorter = tasksSorter;
    }

    public SingleTaskHostSearcher getSingleTaskPlanner() {
        return singleTaskPlanner;
    }

    public void setSingleTaskPlanner(SingleTaskHostSearcher singleTaskPlanner) {
        this.singleTaskPlanner = singleTaskPlanner;
    }

    public Consumer<Object> getLogger() {
        return logger;
    }

    public void setLogger(Consumer<Object> logger) {
        this.logger = logger;
    }
}
