package edu.kpi.nesteruk.util;

import java.util.Collection;

/**
 * Created by Yurii on 2016-02-29.
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

}
