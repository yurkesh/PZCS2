package edu.kpi.nesteruk.pzcs.view.processors;

import edu.kpi.nesteruk.pzcs.planning.PlanningParams;
import edu.kpi.nesteruk.pzcs.view.common.input.ParamsInput;

/**
 * Created by Yurii on 2016-06-02.
 */
public enum PlanningParamInput implements ParamsInput<PlanningParams, PlanningParams.Builder> {
    LabWork("Лабораторна") {
        @Override
        public Number getValue(PlanningParams params) {
            return params.labWork.getNumber();
        }

        @Override
        public void setValue(String input, PlanningParams.Builder builder) {
            builder.setLabWork(Integer.parseInt(input));
        }
    },
    NumberOfChannels("Кількість каналів") {
        @Override
        public Number getValue(PlanningParams params) {
            return params.numberOfChannels;
        }

        @Override
        public void setValue(String input, PlanningParams.Builder builder) {
            builder.setNumberOfChannels(Integer.parseInt(input));
        }
    }

    ;

    private final String caption;

    PlanningParamInput(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
