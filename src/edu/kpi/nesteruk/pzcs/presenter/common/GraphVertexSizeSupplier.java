package edu.kpi.nesteruk.pzcs.presenter.common;

import edu.kpi.nesteruk.misc.Tuple;

/**
 * Created by Anatolii on 2016-03-29.
 */
@FunctionalInterface
public interface GraphVertexSizeSupplier {
    /**
     *
     * @return {width, height}
     */
    Tuple<Integer> getVertexSize();
}
