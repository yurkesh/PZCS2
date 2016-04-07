package edu.kpi.nesteruk.pzcs.presenter.tasks;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.primitives.CriticalNode;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphVertexSizeSupplier;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-03-23.
 */
public class TasksGraphPresenter extends CommonGraphPresenter implements TasksPresenter {

    private final Collection<QueueConstructor<Task, DirectedLink<Task>>> queueConstructors;

    public TasksGraphPresenter(
            GraphView graphView,
            Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor,
            CaptionsSupplier captionsSupplier,
            Supplier<GraphModel> graphModelFactory,
            GraphVertexSizeSupplier vertexSizeSupplier,
            Collection<QueueConstructor<Task, DirectedLink<Task>>> queueConstructors) {

        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory, vertexSizeSupplier);
        this.queueConstructors = queueConstructors;
    }

    @Override
    public void onNewGraphGenerator(ActionEvent event) {

    }

    @Override
    public void onMakeQueues(ActionEvent event) {
        Map<String, List<CriticalNode<Task>>> queuesWithTitles = queueConstructors.stream()
                .map(queueConstructor -> queueConstructor.constructQueues((GraphModelBundle<Task, DirectedLink<Task>>) getModel().getSerializable())).collect(Collectors.toMap(
                titleWithCriticalNodesPair -> titleWithCriticalNodesPair.first,
                Pair::getSecond
        ));

        queuesWithTitles.forEach((title, queues) -> System.out.println(title + ":\n" + queuesToString(queues) + "\n"));
    }

    private static String queuesToString(Collection<CriticalNode<Task>> criticalNodes) {
        StringBuilder sb = new StringBuilder();
        criticalNodes.forEach(node ->
                sb.append(node.node).append(" (").append(node.value).append(")").append("\n")
        );
        return sb.toString();
    }


}
