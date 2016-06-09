package edu.kpi.nesteruk.pzcs.view.common.input;

import edu.kpi.nesteruk.misc.GenericBuilder;

/**
 * Created by Yurii on 2016-06-09.
 */
public interface ParamsInput<Params, B extends GenericBuilder<Params>> {

    String getCaption();

    Number getValue(Params params);

    void setValue(String input, B paramsBuilder);
}
