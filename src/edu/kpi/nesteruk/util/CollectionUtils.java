package edu.kpi.nesteruk.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public static <T> T getFirstOrNull(Collection<T> collection) {
        return collection.stream().findFirst().get();
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

    public static <T, C extends Collection<T>> C add(Collection<T> collection, T element, Supplier<C> destinationSupplier) {
        C destination = destinationSupplier.get();
        destination.addAll(collection);
        destination.add(element);
        return destination;
    }

    public static class CustomCollectors {

        public static <T> BinaryOperator<T> throwingMerger() {
            return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
        }

        public static <T, K, U, M extends Map<K, U>>
        Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
                                        Function<? super T, ? extends U> valueMapper,
                                        Supplier<M> mapSupplier) {
            return Collectors.toMap(
                    keyMapper,
                    valueMapper,
                    throwingMerger(),
                    mapSupplier
            );
        }
    }

}
