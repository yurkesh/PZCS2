package edu.kpi.nesteruk.pzcs.presenter.system;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.event.ActionEvent;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Yurii on 2016-03-23.
 */
public class SystemGraphPresenter extends CommonGraphPresenter implements SystemPresenter {

    public SystemGraphPresenter(GraphView graphView, Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor, CaptionsSupplier captionsSupplier, Supplier<GraphModel> graphModelFactory) {
        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory);
    }

    @Override
    public void onNewSystem(ActionEvent event) {

    }

    @Override
    public void onOpenSystem(ActionEvent event) {
        onOpen(event);
    }

    @Override
    public void onSaveSystem(ActionEvent event) {
        onSave(event);
    }

    @Override
    public void onProcessorsParams(ActionEvent event) {

    }
}
