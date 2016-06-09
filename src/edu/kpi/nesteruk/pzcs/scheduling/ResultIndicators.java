package edu.kpi.nesteruk.pzcs.scheduling;

import java.util.Comparator;

/**
 * Created by Yurii on 2016-06-09.
 */
public class ResultIndicators implements Comparable<ResultIndicators> {

    public static final Comparator<ResultIndicators> COMPARATOR = Comparator
            .comparing(ResultIndicators::getSpeedUp)
            .thenComparing(ResultIndicators::getSystemEfficiency)
            .thenComparing(ResultIndicators::getSchedulerEfficiency);

    public final double speedUp;
    public final double systemEfficiency;
    public final double schedulerEfficiency;

    public ResultIndicators(double speedUp, double systemEfficiency, double schedulerEfficiency) {
        this.speedUp = speedUp;
        this.systemEfficiency = systemEfficiency;
        this.schedulerEfficiency = schedulerEfficiency;
    }

    public double getSpeedUp() {
        return speedUp;
    }

    public double getSystemEfficiency() {
        return systemEfficiency;
    }

    public double getSchedulerEfficiency() {
        return schedulerEfficiency;
    }

    @Override
    public int compareTo(ResultIndicators o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "ResultIndicators{" +
                "speedUp=" + speedUp +
                ", systemEfficiency=" + systemEfficiency +
                ", schedulerEfficiency=" + schedulerEfficiency +
                '}';
    }

    public static ResultIndicators calculate(
            int singleCoreTime,
            int parallelSchedulingTime,
            int numberOfProcessors,
            int theoreticCriticalTime) {

        double seedUp = 1.0 * singleCoreTime / parallelSchedulingTime;
        double systemEf = seedUp / numberOfProcessors;
        double schedulerEf = theoreticCriticalTime / parallelSchedulingTime;

        return new ResultIndicators(seedUp, systemEf, schedulerEf);
    }
}
