package edu.kpi.nesteruk.pzcs.view.processors;

import edu.kpi.nesteruk.pzcs.planning.PlanningParams;

/**
 * Created by Yurii on 2016-06-02.
 */
public enum PlanningParamInput {
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

    public abstract Number getValue(PlanningParams params);

    public abstract void setValue(String input, PlanningParams.Builder builder);
}
