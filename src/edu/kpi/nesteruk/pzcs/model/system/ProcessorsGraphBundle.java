package edu.kpi.nesteruk.pzcs.model.system;

import edu.kpi.nesteruk.pzcs.model.common.GraphModelBundle;
import edu.kpi.nesteruk.pzcs.model.primitives.CongenericLink;

import java.util.Map;

/**
 * Created by Yurii on 2016-05-15.
 */
public class ProcessorsGraphBundle extends GraphModelBundle<Processor, CongenericLink<Processor>> {
    public ProcessorsGraphBundle(Map<String, Processor> nodesMap, Map<String, CongenericLink<Processor>> linksMap) {
        super(nodesMap, linksMap);
    }
}
