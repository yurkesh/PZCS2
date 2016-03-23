package edu.kpi.nesteruk.pzcs.presenter.common;

/**
 * Created by Yurii on 2016-03-23.
 */
@FunctionalInterface
public interface CaptionsSupplier {

    /**
     * @param many - false if caption for single item
     * @return caption (title) of concrete item
     */
    String getCaption(Boolean many);
}
