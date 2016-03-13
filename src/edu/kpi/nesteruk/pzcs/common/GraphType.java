package edu.kpi.nesteruk.pzcs.common;

import edu.kpi.nesteruk.pzcs.model.system.SystemGraphModel;
import edu.kpi.nesteruk.pzcs.model.tasks.TasksGraphModel;
import edu.kpi.nesteruk.pzcs.view.common.CommonGraphView;
import edu.kpi.nesteruk.pzcs.view.common.CommonPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphPresenter;
import edu.kpi.nesteruk.pzcs.view.common.GraphView;

import java.awt.*;

/**
 * Created by Yurii on 2016-03-13.
 */
public enum GraphType {
    TASKS("Tasks") {

        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            return new CommonPresenter(
                    graphView,
                    many -> many ? "tasks" : "task",
                    TasksGraphModel::new
            );
        }
    },
    SYSTEM("System") {
        @Override
        GraphPresenter getPresenter(GraphView graphView) {
            return new CommonPresenter(
                    graphView,
                    many -> many ? "CPUs" : "CPU",
                    SystemGraphModel::new
            );
        }
    };

    private final String caption;

    GraphType(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    abstract GraphPresenter getPresenter(GraphView graphView);

    public CommonGraphView getGraphView() {
        CommonGraphView view = new CommonGraphView();
        view.setLayout(new BorderLayout());
        view.setPresenter(getPresenter(view));
        return view;
    }
}
