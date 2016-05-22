package edu.kpi.nesteruk.pzcs.planning.state;

/**
 * Created by Yurii on 2016-05-23.
 */
public class ProcessorWithStartTime {

    public final StatefulProcessor statefulProcessor;
    public final int startTime;

    public ProcessorWithStartTime(StatefulProcessor statefulProcessor, int startTime) {
        this.statefulProcessor = statefulProcessor;
        this.startTime = startTime;
    }

    public String getProcessorId() {
        return statefulProcessor.getProcessorId();
    }

    public int getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "ProcessorWithStartTime{" +
                "statefulProcessor=" + statefulProcessor +
                ", startTime=" + startTime +
                '}';
    }
}
