package edu.kpi.nesteruk.misc;

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

    public boolean obtainId(String id) {
        try {
            Integer idInt = Integer.valueOf(id);
            return ids.remove(idInt);
        } catch (NumberFormatException e) {
            // IGNORE
            // throw new IllegalArgumentException("Must be int. value = '" + id + "'");
            return false;
        }
    }

    public void releaseId(String id) {
        try {
            Integer idInt = Integer.valueOf(id);
            ids.offer(idInt);
        } catch (NumberFormatException e) {
            // IGNORE
            // throw new IllegalArgumentException("Must be int. value = '" + id + "'");
        }
    }
}
