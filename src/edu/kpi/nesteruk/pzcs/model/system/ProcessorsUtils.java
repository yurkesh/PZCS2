package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yurii on 2016-04-21.
 */
public class ProcessorsUtils {

    public static List<ProcessorInfo> getInfos(Collection<Processor> allProcessors, Collection<CongenericLink<Processor>> allLinks) {
        return allProcessors.stream()
                .map(processor -> new ProcessorInfo(
                        processor,
                        //Get all links incident with current processor
                        allLinks.stream()
                                .filter(link -> link.isIncident(processor))
                                .collect(Collectors.toList())
                ))
                //Sort as defined in ProcessorInfo
                .sorted()
                .collect(Collectors.toList());
    }

}
