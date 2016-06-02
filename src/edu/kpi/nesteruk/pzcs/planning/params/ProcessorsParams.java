package edu.kpi.nesteruk.pzcs.planning.params;

/**
 * Created by Anatolii Bed on 2016-04-20.
 */
public class ProcessorsParams {

    public static final int DEFAULT_NUMBER_OF_CHANNELS = 6;

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

    public ProcessorsParams(int numberOfChannels) {
        this(numberOfChannels, false, null, null);

    }

    public static int numberOfChannelsNullSafe(ProcessorsParams params) {
        return params == null ? ProcessorsParams.DEFAULT_NUMBER_OF_CHANNELS : params.numberOfChannels;
    }
}
