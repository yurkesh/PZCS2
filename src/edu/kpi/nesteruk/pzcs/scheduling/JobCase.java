package edu.kpi.nesteruk.pzcs.scheduling;

import java.util.Comparator;

/**
 * Created by Yurii on 2016-06-09.
 */
public class JobCase implements Comparable<JobCase> {

    public static final Comparator<JobCase> COMPARATOR = Comparator
            .comparing(JobCase::getNumberOfTasks)
            .thenComparing(JobCase::getTasksGraphCoherence);

    public final int numberOfTasks;
    public final double tasksGraphCoherence;

    public JobCase(int numberOfTasks, double tasksGraphCoherence) {
        this.numberOfTasks = numberOfTasks;
        this.tasksGraphCoherence = tasksGraphCoherence;
    }

    public Integer getNumberOfTasks() {
        return numberOfTasks;
    }

    public double getTasksGraphCoherence() {
        return tasksGraphCoherence;
    }

    @Override
    public int compareTo(JobCase o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobCase jobCase = (JobCase) o;

        if (numberOfTasks != jobCase.numberOfTasks) return false;
        return Double.compare(jobCase.tasksGraphCoherence, tasksGraphCoherence) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = numberOfTasks;
        temp = Double.doubleToLongBits(tasksGraphCoherence);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "tasks=" + numberOfTasks +
                ", coherence=" + tasksGraphCoherence +
                "}"
                ;
    }

}
