package edu.kpi.nesteruk.misc;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Yurii on 3/10/2016.
 */
public class IntIdPool implements IdPool<Integer> {

    private Queue<Integer> ids = new PriorityQueue<>();

    @Override
    public Integer obtainID() {
        if (ids.isEmpty()) {
            ids.offer(0);
        }
        int result = ids.poll();
        if (ids.isEmpty()) {
            ids.add(result + 1);
        }
        return result;
    }

    @Override
    public boolean obtainId(Integer id) {
        boolean removed = ids.remove(id);
        if(ids.isEmpty()) {
            ids.add(id + 1);
        }
        return removed;
    }

    @Override
    public void releaseId(Integer id) {
        if(id != null) {
            ids.offer(id);
        }
    }
}
