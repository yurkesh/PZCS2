package edu.kpi.nesteruk.pzcs.planning.params;

/**
 * Created by Yurii on 2016-04-20.
 */
public class ProcessorsParams {

    public static final int DEFAULT_NUMBER_OF_CHANNELS = 1;

    public final int numberOfChannels;
    public final boolean hasIoProcessor;
    public final DuplexMode duplexMode;
    public final TransferParams transferParams;

    public ProcessorsParams(int numberOfChannels, boolean hasIoProcessor, DuplexMode duplexMode, TransferParams transferParams) {
        this.numberOfChannels = numberOfChannels;
        this.hasIoProcessor = hasIoProcessor;
        this.duplexMode = duplexMode;
        this.transferParams = transferParams;
    }

    public static int numberOfChannelsNullSafe(ProcessorsParams params) {
        return params == null ? ProcessorsParams.DEFAULT_NUMBER_OF_CHANNELS : params.numberOfChannels;
    }
}
