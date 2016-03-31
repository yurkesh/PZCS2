package edu.kpi.nesteruk.pzcs.model.common;

import edu.kpi.nesteruk.misc.IdPool;
import edu.kpi.nesteruk.misc.IdPoolWrapper;
import edu.kpi.nesteruk.misc.IntIdPool;
import edu.kpi.nesteruk.misc.Wrapper;

import java.util.function.Predicate;

/**
 * Created by Anatolii on 2016-03-14.
 */
public class CommonIdPool implements IdPool<String> {

    private IdPool<String> idPool = new IdPoolWrapper<>(
            new IntIdPool(),
            new Wrapper<Integer, String>() {
                @Override
                public String wrap(Integer id) {
//                    return " " + id + " ";
                    return "" + id;
                }

                @Override
                public Integer unwrap(String str) {
                    try {
                        return Integer.valueOf(str.trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
    );

    @Override
    public String obtainId() {
        return idPool.obtainId();
    }

    @Override
    public String obtainId(Predicate<String> stringPredicate) {
        return idPool.obtainId(stringPredicate);
    }

    @Override
    public boolean obtainId(String s) {
        return idPool.obtainId(s);
    }

    @Override
    public void releaseId(String s) {
        idPool.releaseId(s);
    }
}
