package edu.kpi.nesteruk.pzcs.planning.processors;

/**
 * Created by Yurii on 2016-05-26.
 */
public class ChannelTransfer {

    public final int startTact;
    public final int weight;
    public final int srcChannel;
    public final String receiver;
    public int destChannel;
    public String transfer;

    public ChannelTransfer(int startTact, int weight, int srcChannel, String receiver) {
        this.startTact = startTact;
        this.weight = weight;
        this.srcChannel = srcChannel;
        this.receiver = receiver;
    }

    public int getStartTact() {
        return startTact;
    }

    public int getSrcChannel() {
        return srcChannel;
    }

    public int getDestChannel() {
        if(destChannel == -1) {
            throw new IllegalStateException("Dest channel is not set");
        }
        return destChannel;
    }

    public ChannelTransfer setDestChannel(int destChannel) {
        this.destChannel = destChannel;
        return this;
    }

    public ChannelTransfer setTransfer(String transfer) {
        this.transfer = transfer;
        return this;
    }
}
