package edu.kpi.nesteruk.pzcs.presenter.tasks;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.TasksPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.event.ActionEvent;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-03-23.
 */
public class TasksGraphPresenter extends CommonGraphPresenter implements TasksPresenter {


    public TasksGraphPresenter(GraphView graphView, Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor, CaptionsSupplier captionsSupplier, Supplier<GraphModel> graphModelFactory) {
        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory);
    }

    @Override
    public void onNewGraphEditor(ActionEvent event) {

    }

    @Override
    public void onNewGraphGenerator(ActionEvent event) {

    }

    @Override
    public void onOpenGraph(ActionEvent event) {

    }

    @Override
    public void onSaveGraph(ActionEvent event) {
        onSave(event);
    }
}
