package edu.kpi.nesteruk.pzcs.planning.processors;

/**
 * Created by Yurii on 2016-06-02.
 */
public interface ScheduledJobsHolder {

    String getExecutingTask(int tact);
    String getTransfer(int channel, int tact);
    int getNumberOfChannels();

}
