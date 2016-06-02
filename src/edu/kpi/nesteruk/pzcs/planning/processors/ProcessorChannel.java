package edu.kpi.nesteruk.pzcs.planning.processors;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * Created by Anatolii Bed on 2016-05-26.
 */
class ProcessorChannel {

    public final int id;

    private final Map<Integer, Parcel> transfers;

    ProcessorChannel(int id) {
        this(id,  new TreeMap<>());
    }

    private ProcessorChannel(int id, Map<Integer, Parcel> transfers) {
        this.id = id;
        this.transfers = transfers;
    }

    public ProcessorChannel copy() {
        Map<Integer, Parcel> transfersCopy = new TreeMap<>();
        transfersCopy.putAll(transfers);
        return new ProcessorChannel(id, transfersCopy);
    }

    /**
     * Processor cannot perform multiple transfers on single channel in moment of time
     * @param startTime time from which begin to check
     * @param windowLength minimum number of tacts available to use for transfer to receiver
     * @return
     */
    public int getMinAvailableStartTime(int startTime, int windowLength) {
        for (;; startTime++) {
            if(areAllNextTactsFree(startTime, windowLength)) {
                return startTime;
            }
        }
    }

    /**
     * @return true if this channel is free at tacts [startTime, windowLength)
     */
    private boolean areAllNextTactsFree(int startTime, int windowLength) {
        return !IntStream.range(startTime, startTime + windowLength)
                .filter(transfers::containsKey)
                .findAny()
                .isPresent();
    }

    /**
     * @return true if transfer to receiver is present at any tact [tact, tact + window)
     */
    public boolean isTransferringToReceiver(int tact, int window, String receiver) {
        return IntStream.range(tact, tact + window)
                .filter(futureTact -> {
                    Parcel parcel = transfers.get(tact);
                    return parcel != null && parcel.receiver.equals(receiver);
                })
                .findAny()
                .isPresent();
    }

    public void addTransfer(int startTact, int length, String receiver, String transfer) {
        Parcel parcel = new Parcel(receiver, transfer, length);
        IntStream.range(startTact, startTact + length)
                .forEach(futureTact -> {
                    Parcel removed = transfers.put(futureTact, parcel);
                    if(removed != null) {
                        throw new IllegalStateException("Parcel removed = " + removed + ". New = " + parcel + ". Tact = " + futureTact);
                    }
                });
    }

    public int getId() {
        return id;
    }

    public String getTransfer(int tact) {
        Parcel parcel = transfers.get(tact);
        return parcel == null ? null : parcel.transfer + "(" + parcel.receiver + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessorChannel that = (ProcessorChannel) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    private static class Parcel {
        public final String receiver;
        public final String transfer;
        public final int totalWeight;

        private Parcel(String receiver, String transfer, int totalWeight) {
            this.receiver = receiver;
            this.transfer = transfer;
            this.totalWeight = totalWeight;
        }

        @Override
        public String toString() {
            return "Parcel{" +
                    "" + transfer + " (" + receiver + ") [" + totalWeight + "]" +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", transfers=" + transfers +
                '}';
    }
}
