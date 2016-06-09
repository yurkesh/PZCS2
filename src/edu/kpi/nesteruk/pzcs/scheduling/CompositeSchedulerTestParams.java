package edu.kpi.nesteruk.pzcs.scheduling;

import edu.kpi.nesteruk.misc.GenericBuilder;

/**
 * Created by Yurii on 2016-06-09.
 */
public class CompositeSchedulerTestParams {

    public static final CompositeSchedulerTestParams DEFAULT = new CompositeSchedulerTestParams(
            6,
            12,
            3,
            .1,
            .9,
            .4,
            1,
            12,
            1,
            6,
            10
    );

    public final int minNumberOfTasks;
    public final int maxNumberOfTasks;
    public final int deltaNumberOfTasks;

    public final double minTasksGraphCoherence;
    public final double maxTasksGraphCoherence;
    public final double deltaTasksGraphCoherence;

    public final int minTaskWeight;
    public final int maxTaskWeight;

    public final int minTasksLinkWeight;
    public final int maxTasksLinkWeight;

    public final int numberOfTaskGraphsToGenerate;

    public CompositeSchedulerTestParams(
            int minNumberOfTasks,
            int maxNumberOfTasks,
            int deltaNumberOfTasks,
            double minTasksGraphCoherence,
            double maxTasksGraphCoherence,
            double deltaTasksGraphCoherence,
            int minTaskWeight,
            int maxTaskWeight,
            int minTasksLinkWeight,
            int maxTasksLinkWeight,
            int numberOfTaskGraphsToGenerate) {

        this.minNumberOfTasks = minNumberOfTasks;
        this.maxNumberOfTasks = maxNumberOfTasks;
        this.deltaNumberOfTasks = deltaNumberOfTasks;
        this.minTasksGraphCoherence = minTasksGraphCoherence;
        this.maxTasksGraphCoherence = maxTasksGraphCoherence;
        this.deltaTasksGraphCoherence = deltaTasksGraphCoherence;
        this.minTaskWeight = minTaskWeight;
        this.maxTaskWeight = maxTaskWeight;
        this.minTasksLinkWeight = minTasksLinkWeight;
        this.maxTasksLinkWeight = maxTasksLinkWeight;
        this.numberOfTaskGraphsToGenerate = numberOfTaskGraphsToGenerate;
    }

    public int getMinNumberOfTasks() {
        return minNumberOfTasks;
    }

    public int getMaxNumberOfTasks() {
        return maxNumberOfTasks;
    }

    public int getDeltaNumberOfTasks() {
        return deltaNumberOfTasks;
    }

    public double getMinTasksGraphCoherence() {
        return minTasksGraphCoherence;
    }

    public double getMaxTasksGraphCoherence() {
        return maxTasksGraphCoherence;
    }

    public double getDeltaTasksGraphCoherence() {
        return deltaTasksGraphCoherence;
    }

    public int getMinTaskWeight() {
        return minTaskWeight;
    }

    public int getMaxTaskWeight() {
        return maxTaskWeight;
    }

    public int getMinTasksLinkWeight() {
        return minTasksLinkWeight;
    }

    public int getMaxTasksLinkWeight() {
        return maxTasksLinkWeight;
    }

    public int getNumberOfTaskGraphsToGenerate() {
        return numberOfTaskGraphsToGenerate;
    }

    @Override
    public String toString() {
        return "CompositeSchedulerTestParams{" +
                "minNumberOfTasks=" + minNumberOfTasks +
                ", maxNumberOfTasks=" + maxNumberOfTasks +
                ", deltaNumberOfTasks=" + deltaNumberOfTasks +
                ", minTasksGraphCoherence=" + minTasksGraphCoherence +
                ", maxTasksGraphCoherence=" + maxTasksGraphCoherence +
                ", deltaTasksGraphCoherence=" + deltaTasksGraphCoherence +
                ", minTaskWeight=" + minTaskWeight +
                ", maxTaskWeight=" + maxTaskWeight +
                ", minTasksLinkWeight=" + minTasksLinkWeight +
                ", maxTasksLinkWeight=" + maxTasksLinkWeight +
                ", numberOfTaskGraphsToGenerate=" + numberOfTaskGraphsToGenerate +
                '}';
    }


    public static class Builder implements GenericBuilder<CompositeSchedulerTestParams> {

        private int minNumberOfTasks;
        private int maxNumberOfTasks;
        private int deltaNumberOfTasks;

        private double minTasksGraphCoherence;
        private double maxTasksGraphCoherence;
        private double deltaTasksGraphCoherence;

        private int minTaskWeight;
        private int maxTaskWeight;

        private int minTasksLinkWeight;
        private int maxTasksLinkWeight;

        private int numberOfTaskGraphsToGenerate;

        public Builder() {
        }

        public Builder(Builder builder) {
            minNumberOfTasks = builder.minNumberOfTasks;
            maxNumberOfTasks = builder.maxNumberOfTasks;
            deltaNumberOfTasks = builder.deltaNumberOfTasks;

            minTasksGraphCoherence = builder.minTasksGraphCoherence;
            maxTasksGraphCoherence = builder.maxTasksGraphCoherence;
            deltaTasksGraphCoherence = builder.deltaTasksGraphCoherence;

            minTaskWeight = builder.minTaskWeight;
            maxTaskWeight = builder.maxTaskWeight;

            minTasksLinkWeight = builder.minTasksLinkWeight;
            maxTasksLinkWeight = builder.maxTasksLinkWeight;

            numberOfTaskGraphsToGenerate = builder.numberOfTaskGraphsToGenerate;
        }

        public Builder setMinNumberOfTasks(int minNumberOfTasks) {
            this.minNumberOfTasks = minNumberOfTasks;
            return this;
        }

        public Builder setMaxNumberOfTasks(int maxNumberOfTasks) {
            this.maxNumberOfTasks = maxNumberOfTasks;
            return this;
        }

        public Builder setDeltaNumberOfTasks(int deltaNumberOfTasks) {
            this.deltaNumberOfTasks = deltaNumberOfTasks;
            return this;
        }

        public Builder setMinTasksGraphCoherence(double minTasksGraphCoherence) {
            this.minTasksGraphCoherence = minTasksGraphCoherence;
            return this;
        }

        public Builder setMaxTasksGraphCoherence(double maxTasksGraphCoherence) {
            this.maxTasksGraphCoherence = maxTasksGraphCoherence;
            return this;
        }

        public Builder setDeltaTasksGraphCoherence(double deltaTasksGraphCoherence) {
            this.deltaTasksGraphCoherence = deltaTasksGraphCoherence;
            return this;
        }

        public Builder setMinTaskWeight(int minTaskWeight) {
            this.minTaskWeight = minTaskWeight;
            return this;
        }

        public Builder setMaxTaskWeight(int maxTaskWeight) {
            this.maxTaskWeight = maxTaskWeight;
            return this;
        }

        public Builder setMinTasksLinkWeight(int minTasksLinkWeight) {
            this.minTasksLinkWeight = minTasksLinkWeight;
            return this;
        }

        public Builder setMaxTasksLinkWeight(int maxTasksLinkWeight) {
            this.maxTasksLinkWeight = maxTasksLinkWeight;
            return this;
        }

        public Builder setNumberOfTaskGraphsToGenerate(int numberOfTaskGraphsToGenerate) {
            this.numberOfTaskGraphsToGenerate = numberOfTaskGraphsToGenerate;
            return this;
        }

        @Override
        public CompositeSchedulerTestParams build() {
            return new CompositeSchedulerTestParams(
                    minNumberOfTasks,
                    maxNumberOfTasks,
                    deltaNumberOfTasks,
                    minTasksGraphCoherence,
                    maxTasksGraphCoherence,
                    deltaTasksGraphCoherence,
                    minTaskWeight,
                    maxTaskWeight,
                    minTasksLinkWeight,
                    maxTasksLinkWeight,
                    numberOfTaskGraphsToGenerate
            );
        }
    }
}
