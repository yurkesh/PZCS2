package edu.kpi.nesteruk.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Anatolii on 2016-02-29.
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static <T> T getLastOrNull(Collection<T> collection) {
        if(isEmpty(collection)) {
            return null;
        }
        int size = collection.size();
        if(collection instanceof List) {
            return ((List<T>) collection).get(size - 1);
        }
        return collection.stream().skip(size - 1).collect(Collectors.toList()).get(0);
    }



}
