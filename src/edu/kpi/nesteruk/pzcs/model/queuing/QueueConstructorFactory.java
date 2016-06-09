package edu.kpi.nesteruk.pzcs.model.queuing;

import edu.kpi.nesteruk.pzcs.common.LabWork;
import edu.kpi.nesteruk.pzcs.model.primitives.DirectedLink;
import edu.kpi.nesteruk.pzcs.model.queuing.common.QueueConstructor;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathByNumberOfNodesAndCoherence11;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathByTimeForAllNodes3;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete.CriticalPathOfGraphAndNodesByTime1;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete2.CriticalPathByNumberOfNodesAndCoherence10;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete2.CriticalPathByTimeAndNumberOfNodes2;
import edu.kpi.nesteruk.pzcs.model.queuing.concrete2.CriticalPathByWeightOfNodes14;
import edu.kpi.nesteruk.pzcs.model.tasks.Task;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Yurii on 2016-06-09.
 */
public class QueueConstructorFactory {

    private static Map<LabWork, Integer> latToVariantMap = new TreeMap<>();

    public static void setLabs234Variants(int lab2Variant, int lab3Variant, int lab4Variant) {
        latToVariantMap.put(LabWork.LAB_2, lab2Variant);
        latToVariantMap.put(LabWork.LAB_3, lab3Variant);
        latToVariantMap.put(LabWork.LAB_4, lab4Variant);
    }

    public static QueueConstructor<Task, DirectedLink<Task>> getQueueConstructor(LabWork labWork) {
        int variant = getQueueConstructorVariant(labWork);
        return getByVariant(variant);
    }

    public static int getQueueConstructorVariant(LabWork labWork) {
        Integer variant = latToVariantMap.get(labWork);
        if(variant == null) {
            throw new NullPointerException("Variant not specified for this lab-work = " + labWork);
        }
        return variant;
    }

    public static QueueConstructor<Task, DirectedLink<Task>> getByVariant(int variant) {
        switch (variant) {
            case 1:
                return new CriticalPathOfGraphAndNodesByTime1<>();
            case 2:
                return new CriticalPathByTimeAndNumberOfNodes2<>();
            case 3:
                return new CriticalPathByTimeForAllNodes3<>();
            case 10:
                return new CriticalPathByNumberOfNodesAndCoherence10<>();
            case 11:
                return new CriticalPathByNumberOfNodesAndCoherence11<>();
            case 14:
                return new CriticalPathByWeightOfNodes14<>();
            default:
                throw new UnsupportedOperationException("Variant '" + variant + "' not implemented");
        }
    }

}
