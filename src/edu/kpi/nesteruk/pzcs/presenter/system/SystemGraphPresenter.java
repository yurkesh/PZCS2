package edu.kpi.nesteruk.pzcs.presenter.system;

import com.mxgraph.view.mxStylesheet;
import edu.kpi.nesteruk.misc.Pair;
import edu.kpi.nesteruk.pzcs.model.common.GraphModel;
import edu.kpi.nesteruk.pzcs.model.system.ProcessorsGraphBundle;
import edu.kpi.nesteruk.pzcs.presenter.common.CaptionsSupplier;
import edu.kpi.nesteruk.pzcs.presenter.common.CommonGraphPresenter;
import edu.kpi.nesteruk.pzcs.presenter.common.GraphVertexSizeSupplier;
import edu.kpi.nesteruk.pzcs.scheduling.*;
import edu.kpi.nesteruk.pzcs.view.common.GenericInputDialog;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;
import edu.kpi.nesteruk.pzcs.view.print.Table;
import edu.kpi.nesteruk.pzcs.view.print.TableRepresentationBuilder;
import edu.kpi.nesteruk.pzcs.view.processors.CompositeSchedulerTestParamsInput;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

        super(graphView, graphStylesheetInterceptor, captionsSupplier, graphModelFactory, vertexSizeSupplier);
    }

    @Override
    public void onStatistics(ActionEvent event) {
        GenericInputDialog.showDialog(
                "Enter scheduling testing params",
                CompositeSchedulerTestParams.DEFAULT,
                Arrays.asList(CompositeSchedulerTestParamsInput.values()),
                CompositeSchedulerTestParams.Builder::new,
                params -> {
                    List<Pair<ConcreteTasksJob, Map<SchedulerCase, ResultIndicators>>> fullResult =
                            CompositePlannerTesting.performFullTesting(
                                    getProcessorsGraphBundle(), params
                            );
                    Table table =
                            CompositePlannerTestingResultsInterpreter.makeTableResult(fullResult);
                    System.out.println("Results: " + new TableRepresentationBuilder(table).getRepresentation());
                }
        );
    }

    @Override
    public ProcessorsGraphBundle getProcessorsGraphBundle() {
        return (ProcessorsGraphBundle) getModel().getBundle();
    }

    @Override
    public void setGraph(Object graph) {
        if(graph instanceof ProcessorsGraphBundle) {
            setGraph(getModel().restore((ProcessorsGraphBundle) graph));
        } else {
            throw new IllegalArgumentException("Cannot set graph = " + graph);
        }
    }
}
