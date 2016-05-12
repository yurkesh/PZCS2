package edu.kpi.nesteruk.pzcs.planning;

import edu.kpi.nesteruk.pzcs.model.primitives.Node;
import edu.kpi.nesteruk.pzcs.model.system.Processor;
import edu.kpi.nesteruk.pzcs.planning.transfering.Parcel;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by Yurii on 2016-04-20.
 */
public class TransmittableProcessor implements Node {

    public final Processor processor;
    public final ProcessorsParams params;

    private final Set<String> neighbours = new LinkedHashSet<>();

    private final Queue<Parcel> inputBuffer = new LinkedList<>();
    private final Queue<Parcel> outputBuffer = new LinkedList<>();

    public TransmittableProcessor(Processor processor, ProcessorsParams params) {
        this.processor = processor;
        this.params = params;
    }

    @Override
    public String getId() {
        return processor.getId();
    }

    @Override
    public int getWeight() {
        return processor.getWeight();
    }
}
