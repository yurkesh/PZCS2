package edu.kpi.nesteruk.pzcs.planning;

/**
 * Created by Yurii on 2016-04-20.
 */
public class ProcessorsParams {

    public final int numberOfLinks;
    public final boolean hasIoProcessor;
    public final Duplex duplex;
    public final TransferParams transferParams;

    public ProcessorsParams(int numberOfLinks, boolean hasIoProcessor, Duplex duplex, TransferParams transferParams) {
        this.numberOfLinks = numberOfLinks;
        this.hasIoProcessor = hasIoProcessor;
        this.duplex = duplex;
        this.transferParams = transferParams;
    }
}
