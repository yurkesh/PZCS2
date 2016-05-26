package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.planning.processors.ChannelTransfer;

/**
 * Created by Yurii on 2016-05-26.
 */
public class ProcessorTransfer {

    public final String srcProcessor;
    public final String destProcessor;

    public final ChannelTransfer channelTransfer;

    public ProcessorTransfer(String srcProcessor, String destProcessor, ChannelTransfer channelTransfer) {
        this.srcProcessor = srcProcessor;
        this.destProcessor = destProcessor;
        this.channelTransfer = channelTransfer;
    }

}
