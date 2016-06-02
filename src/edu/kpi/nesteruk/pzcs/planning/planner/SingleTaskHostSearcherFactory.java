package edu.kpi.nesteruk.pzcs.planning.planner;

import edu.kpi.nesteruk.pzcs.common.LabWork;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Anatolii Bed on 2016-06-02.
 */
public class SingleTaskHostSearcherFactory {

    private static Map<LabWork, Integer> latToVariantMap = new TreeMap<>();

    public static void setLabs67Variants(int lab6Variant, int lab7Variant) {
        latToVariantMap.put(LabWork.LAB_6, lab6Variant);
        latToVariantMap.put(LabWork.LAB_7, lab7Variant);
    }

    public static SingleTaskHostSearcher getSearcher(LabWork labWork) {
        Integer variant = latToVariantMap.get(labWork);
        if(variant == null) {
            throw new NullPointerException("Variant not specified for this lab-work = " + labWork);
        }
        return getVariant(variant);
    }

    private static SingleTaskHostSearcher getVariant(int variant) {
        switch (variant) {
            case 2:
                return new Variant2TheMostIdleProcessor();
            case 3:
                return new Variant3ByProcessorsPriorityWithoutPrediction();
            case 4:
                return new Variant4EarliestStartWithoutPrediction();
            case 5:
                return new Variant5EarliestStartWithPrediction();
            default:
                throw new UnsupportedOperationException("Variant '" + variant + "' not implemented");
        }
    }
}
