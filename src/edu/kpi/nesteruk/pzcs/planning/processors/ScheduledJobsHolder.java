package edu.kpi.nesteruk.pzcs.planning.processors;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
public interface ScheduledJobsHolder {

    boolean USE_EXTENDED_TRANSFER_FORMATTING = false;

    String getExecutingTask(int tact);
    String getTransfer(int channel, int tact);
    int getNumberOfChannels();

}
