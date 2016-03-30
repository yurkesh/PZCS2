package edu.kpi.nesteruk.misc;

/**
 * Created by Anatolii on 2016-03-14.
 */
public class IdPoolWrapper<RawId, BackedId> implements IdPool<BackedId> {

    private final IdPool<RawId> wrappedIdPool;
    private final Wrapper<RawId, BackedId> wrapper;

    public IdPoolWrapper(IdPool<RawId> wrappedIdPool, Wrapper<RawId, BackedId> wrapper) {
        this.wrappedIdPool = wrappedIdPool;
        this.wrapper = wrapper;
    }

    @Override
    public BackedId obtainID() {
        return wrapper.wrap(wrappedIdPool.obtainID());
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
