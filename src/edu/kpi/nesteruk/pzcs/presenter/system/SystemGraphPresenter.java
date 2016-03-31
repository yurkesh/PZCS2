package edu.kpi.nesteruk.pzcs.presenter.system;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphVertexSizeSupplier;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.event.ActionEvent;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Anatolii on 2016-03-23.
 */
public class SystemGraphPresenter extends CommonGraphPresenter implements SystemPresenter {

    public SystemGraphPresenter(GraphView graphView,
                                Function<mxStylesheet, mxStylesheet> graphStylesheetInterceptor,
                                CaptionsSupplier captionsSupplier,
                                Supplier<GraphModel> graphModelFactory,
                                GraphVertexSizeSupplier vertexSizeSupplier) {

        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory, vertexSizeSupplier, GraphType.SYSTEM);
    }

    @Override
    public void onProcessorsParams(ActionEvent event) {

    }
}
