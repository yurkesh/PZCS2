package edu.kpi.nesteruk.pzcs.planning.processors;

import edu.kpi.nesteruk.pzcs.planning.planner.ProcessorTransfer;
import edu.kpi.nesteruk.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Yurii on 2016-05-26.
 */
class ProcessorTransfers {

    private final String processorId;

    /**
     * Processor cannot perform more transfers than it has channels in the same time
     */
    private final Map<Integer, ProcessorChannel> channels;

    public ProcessorTransfers(String processorId, int numberOfChannels) {
        this.processorId = Objects.requireNonNull(processorId);
        if(numberOfChannels < 1) {
            throw new IllegalArgumentException();
        }
        this.channels = Collections.unmodifiableMap(
                IntStream.range(0, numberOfChannels)
                        .boxed()
                        .map(ProcessorChannel::new)
                        .collect(CollectionUtils.CustomCollectors.toMap(
                                ProcessorChannel::getId,
                                Function.identity(),
                                LinkedHashMap::new
                        ))
        );
    }

    private ProcessorTransfers(String processorId, Map<Integer, ProcessorChannel> channels) {
        this.processorId = processorId;
        this.channels = channels;
    }

    public ProcessorTransfers copy() {
        Map<Integer, ProcessorChannel> channelsCopy = new LinkedHashMap<>();
        channels.entrySet().forEach(entry -> channelsCopy.put(entry.getKey(), entry.getValue().copy()));
        return new ProcessorTransfers(processorId, channelsCopy);
    }

    public int getNumberOfChannels() {
        return channels.size();
    }

    /**
     * Processor cannot perform multiple transfers to the same receiver on different channels simultaneously.
     * @param startTact tact from which start to check
     * @param weight length of transfer = number of tacts
     * @return ChannelTransfer with all transfer info set
     */
    public ChannelTransfer getTransferEstimate(int startTact, int weight, ProcessorTransfers receiver) {
        if(this.processorId.equals(receiver.processorId)) {
            throw new IllegalArgumentException("Cannot send message to self. ProcessorId = '" + processorId + "'");
        }
        return getTransferEstimateInner(startTact, weight, receiver);
    }

    private ChannelTransfer getTransferEstimateInner(int startTact, int weight, ProcessorTransfers receiver) {
        for (;; startTact++) {
            Optional<ChannelTransfer> sendingEstimateOpt = getBestSendingEstimate(startTact, weight, receiver);
            if(sendingEstimateOpt.isPresent()) {
                ChannelTransfer sendingEstimate = sendingEstimateOpt.get();
                startTact = sendingEstimate.getStartTact();
                OptionalInt channelThatCanReceiveOpt = receiver.getChannelThatCanReceive(
                        startTact, weight, processorId
                );
                if(channelThatCanReceiveOpt.isPresent()) {
                    return new ChannelTransfer(
                            startTact,
                            weight,
                            sendingEstimate.getSrcChannel(),
                            receiver.processorId
                    ).setDestChannel(channelThatCanReceiveOpt.getAsInt());
                }
            }
        }
    }

    /**
     * @return optional id of channel that can receive transfer with start time = startTact and length = weight
     */
    private OptionalInt getChannelThatCanReceive(int startTact, int weight, String fromSender) {
        boolean simultaneousTransferToReceiverOnAnotherChannelIsPresent = channels.values().stream()
                .filter(channel -> channel.isTransferringToReceiver(startTact, weight, fromSender))
                .findAny()
                .isPresent();
        if(!simultaneousTransferToReceiverOnAnotherChannelIsPresent) {
            Optional<ProcessorChannel> canStartNowOpt = channels.values().stream()
                    //Check if channel can start this transfer at specified tact
                    .filter(channel -> channel.getMinAvailableStartTime(startTact, weight) == startTact)
                    .findAny();
            if(canStartNowOpt.isPresent()) {
                return OptionalInt.of(canStartNowOpt.get().getId());
            }
        }
        return OptionalInt.empty();
    }


    private Optional<ChannelTransfer> getBestSendingEstimate(int startTact, int weight, ProcessorTransfers receiver) {
        //Get channel that can provide the lowest start time of transfer
        ChannelTransfer estimateWithLowestStartTime = channels.values().stream()
                //For all channels get estimate (with minimum possible start time)
                .map(channel -> new ChannelTransfer(
                        channel.getMinAvailableStartTime(startTact, weight),
                        weight,
                        channel.getId(),
                        receiver.processorId
                ))
                //Get channel with the lowest time (sort by possible start time)
                .min(Comparator.comparing(ChannelTransfer::getStartTact))
                .get();

        boolean simultaneousTransferToReceiverOnAnotherChannelIsPresent =
                getAllWithout(estimateWithLowestStartTime.srcChannel).stream()
                        .filter(channel -> channel.isTransferringToReceiver(startTact, weight, receiver.processorId))
                        .findAny()
                        .isPresent();

        if(simultaneousTransferToReceiverOnAnotherChannelIsPresent) {
            return Optional.empty();
        } else {
            return Optional.of(estimateWithLowestStartTime);
        }
    }

    private Collection<ProcessorChannel> getAllWithout(int channelId) {
        Set<ProcessorChannel> allOtherChannels = new HashSet<>(channels.values());
        allOtherChannels.remove(channels.get(channelId));
        return allOtherChannels;
    }

    public void addTransfer(int channel, int startTact, int length, String receiver, String transfer) {
        channels.get(channel).addTransfer(startTact, length, receiver, transfer);
    }

    public String getTransfer(int channel, int tact) {
        return channels.get(channel).getTransfer(tact);
    }

    @Override
    public String toString() {
        return channels.values().stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
