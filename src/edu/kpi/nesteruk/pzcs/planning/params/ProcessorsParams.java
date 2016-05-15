package edu.kpi.nesteruk.pzcs.planning.params;

/**
 * Created by Yurii on 2016-04-20.
 */
public class ProcessorsParams {

    public final int numberOfLinks;
    public final boolean hasIoProcessor;
    public final DuplexMode duplexMode;
    public final TransferParams transferParams;

    public ProcessorsParams(int numberOfLinks, boolean hasIoProcessor, DuplexMode duplexMode, TransferParams transferParams) {
        this.numberOfLinks = numberOfLinks;
        this.hasIoProcessor = hasIoProcessor;
        this.duplexMode = duplexMode;
        this.transferParams = transferParams;
    }
}
