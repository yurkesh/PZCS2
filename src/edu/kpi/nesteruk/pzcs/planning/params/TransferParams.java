package edu.kpi.nesteruk.pzcs.planning.params;

/**
 * Created by Yurii on 2016-04-20.
 */
public class TransferParams {

    public final Approach approach;
    /**
     * Length of packet for {@link Approach#PacketsConveyor} or -1
     */
    public final int param;

    private TransferParams(Approach approach, int param) {
        this.approach = approach;
        this.param = param;
    }

    public static TransferParams messages() {
        return new TransferParams(Approach.Messages, -1);
    }

    public static TransferParams packetsConveyor(int packetLength) {
        return new TransferParams(Approach.PacketsConveyor, packetLength);
    }

    public enum Approach {
        Messages,
        PacketsConveyor
    }

}
