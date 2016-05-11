package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yurii on 2016-04-21.
 */
public class ProcessorInfo implements Comparable<ProcessorInfo> {

    public static final Comparator<ProcessorInfo> COMPARATOR =
            Comparator.comparing(ProcessorInfo::getCoherence).thenComparing(ProcessorInfo::getProcessor);

    public final Processor processor;
    public final int coherence;
    public final List<CongenericLink<Processor>> links;

    public ProcessorInfo(Processor processor, List<CongenericLink<Processor>> links) {
        this.processor = processor;
        this.coherence = links.size();
        this.links = Collections.unmodifiableList(links);
    }

    public Processor getProcessor() {
        return processor;
    }

    public int getCoherence() {
        return coherence;
    }

    public List<CongenericLink<Processor>> getLinks() {
        return links;
    }

    @Override
    public int compareTo(ProcessorInfo o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "ProcessorInfo{" +
                "processor=" + processor +
                ", coherence=" + coherence +
                ", links=" + links +
                '}';
    }
}
