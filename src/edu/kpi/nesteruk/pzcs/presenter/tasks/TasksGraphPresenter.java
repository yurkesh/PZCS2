package edu.kpi.nesteruk.pzcs.presenter.tasks;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.common.NodesQueue;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphVertexSizeSupplier;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-03-23.
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
        Map<String, NodesQueue> queues = queueConstructors.stream()
                .map(queueConstructor -> queueConstructor.constructQueue((
                        (GraphModelBundle<Task, DirectedLink<Task>>) getModel().getSerializable())
                ))
                .collect(Collectors.toMap(
                        (Function<Pair<String, NodesQueue<Task>>, String>) pair -> pair.first,
                        (Function<Pair<String, NodesQueue<Task>>, NodesQueue>) pair -> pair.second
                ));

        queues.forEach((title, queue) -> System.out.println(title + ": value = " + queue.value + ", tasks order:\n" + queue.tasksSequence + "\n"));
    }
}
