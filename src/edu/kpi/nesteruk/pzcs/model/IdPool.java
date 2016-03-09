package edu.kpi.nesteruk.pzcs.model;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Yurii on 3/10/2016.
 */
public class IdPool {

    private Queue<Integer> ids = new PriorityQueue<>();

    public String obtainID() {
        if (ids.isEmpty()) {
            ids.offer(0);
        }
        int result = ids.poll();
        if (ids.isEmpty()) {
            ids.add(result + 1);
        }
        return String.valueOf(result);
    }

    public void releaseId(String id) {
        try {
            Integer idInt = Integer.valueOf(id);
            ids.offer(idInt);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Must be int. value = '" + id + "'");
        }
    }
}
