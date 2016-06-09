package edu.kpi.nesteruk.pzcs.scheduling;

/**
 * Created by Yurii on 2016-06-09.
 */
public class SchedulerCase {

    public final int queueConstructorVariant;
    public final int plannerVariant;

    public SchedulerCase(int queueConstructorVariant, int plannerVariant) {
        this.queueConstructorVariant = queueConstructorVariant;
        this.plannerVariant = plannerVariant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchedulerCase that = (SchedulerCase) o;

        if (queueConstructorVariant != that.queueConstructorVariant) return false;
        return plannerVariant == that.plannerVariant;

    }

    @Override
    public int hashCode() {
        int result = queueConstructorVariant;
        result = 31 * result + plannerVariant;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "queue=" + queueConstructorVariant +
                ", planner=" + plannerVariant +
                "}";
    }
}
