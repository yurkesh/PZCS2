package edu.kpi.nesteruk.pzcs.presenter.common;

import edu.kpi.nesteruk.pzcs.common.GraphType;
import edu.kpi.nesteruk.pzcs.presenter.system.SystemPresenter;
import edu.kpi.nesteruk.pzcs.presenter.tasks.TasksPresenter;
import edu.kpi.nesteruk.pzcs.view.dashboard.UnitedGraphsView;

import javax.swing.*;

/**
 * Created by Yurii on 2016-03-23.
 */
public interface UnitedGraphPresenter extends GraphPresenter, TasksPresenter, SystemPresenter, DefaultPresenter {

    JComponent getGraphsContainer();

    static UnitedGraphPresenter createDefault(GraphType... types) {
        return new UnitedGraphsView(types);
    }

}
