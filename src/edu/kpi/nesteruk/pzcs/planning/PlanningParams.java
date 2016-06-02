package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.common.LabWork;

/**
 * Created by Yurii on 2016-06-02.
 */
public class PlanningParams {

    public static final PlanningParams DEFAULT = new PlanningParams(LabWork.LAB_6, 1);

    public final LabWork labWork;
    public final int numberOfChannels;

    public PlanningParams(LabWork labWork, int numberOfChannels) {
        this.labWork = labWork;
        this.numberOfChannels = numberOfChannels;
    }

    @Override
    public String toString() {
        return "PlanningParams{" +
                "labWork=" + labWork +
                ", numberOfChannels=" + numberOfChannels +
                '}';
    }

    public static class Builder {

        private int labWork;
        private int numberOfChannels;

        public Builder setLabWork(int labWork) {
            this.labWork = labWork;
            return this;
        }

        public Builder setNumberOfChannels(int numberOfChannels) {
            this.numberOfChannels = numberOfChannels;
            return this;
        }

        public PlanningParams build() {
            LabWork labWork = getLabWork(this.labWork);
            checkNumberOfChannels(numberOfChannels);
            return new PlanningParams(labWork, numberOfChannels);
        }

        private static void checkNumberOfChannels(int numberOfChannels) {
            if(numberOfChannels < 1) {
                throw new IllegalStateException("Number of channels must be positive. " + numberOfChannels + " is unacceptable");
            }
        }

        private static LabWork getLabWork(int labWork) {
            switch (labWork) {
                case 6:
                    return LabWork.LAB_6;
                case 7:
                    return LabWork.LAB_7;
                default:
                    throw new IllegalArgumentException("Unacceptable lab work = " + labWork);
            }
        }

    }

}
