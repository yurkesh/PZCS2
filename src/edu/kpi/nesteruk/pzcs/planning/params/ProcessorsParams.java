package edu.kpi.nesteruk.pzcs.planning.params;

/**
 * Created by Yurii on 2016-04-20.
 */
public class ProcessorsParams {

    public static final int DEFAULT_NUMBER_OF_CHANNELS = 6;

    public static final int NUMBER_OF_CHANNELS_BY_MAX_COHERENCE = -1;

    public final int numberOfChannels;

    public ProcessorsParams(int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    public static int numberOfChannelsNullSafe(ProcessorsParams params) {
        return params == null ? ProcessorsParams.DEFAULT_NUMBER_OF_CHANNELS : params.numberOfChannels;
    }
}
