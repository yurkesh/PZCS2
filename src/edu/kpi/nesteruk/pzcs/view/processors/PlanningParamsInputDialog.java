package edu.kpi.nesteruk.pzcs.view.processors;

import edu.kpi.nesteruk.pzcs.planning.PlanningParams;
import edu.kpi.nesteruk.pzcs.view.common.GenericInputDialog;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Yurii on 2016-04-13.
 */
public class PlanningParamsInputDialog {

    public static void showDialog(PlanningParams defaultParams, Consumer<PlanningParams> paramsConsumer) {
        GenericInputDialog.showDialog(
                "Enter scheduling params",
                defaultParams,
                Arrays.asList(PlanningParamInput.values()),
                PlanningParams.Builder::new,
                paramsConsumer
        );
    }
}
