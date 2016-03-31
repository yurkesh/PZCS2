package edu.kpi.nesteruk.misc;

import java.util.function.Predicate;

/**
 * Created by Yurii on 2016-03-14.
 */
public class IdPoolWrapper<RawId, BackedId> implements IdPool<BackedId> {

    private final IdPool<RawId> wrappedIdPool;
    private final Wrapper<RawId, BackedId> wrapper;

    public IdPoolWrapper(IdPool<RawId> wrappedIdPool, Wrapper<RawId, BackedId> wrapper) {
        this.wrappedIdPool = wrappedIdPool;
        this.wrapper = wrapper;
    }

    @Override
    public BackedId obtainId() {
        return wrapper.wrap(wrappedIdPool.obtainId());
    }

    @Override
    public BackedId obtainId(Predicate<BackedId> backedIdPredicate) {
        return wrapper.wrap(wrappedIdPool.obtainId(rawId -> backedIdPredicate.test(wrapper.wrap(rawId))));
    }

    @Override
    public boolean obtainId(BackedId backedId) {
        return wrappedIdPool.obtainId(wrapper.unwrap(backedId));
    }

    @Override
    public void releaseId(BackedId backedId) {
        wrappedIdPool.releaseId(wrapper.unwrap(backedId));
    }
}
