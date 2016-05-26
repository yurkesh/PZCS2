package edu.kpi.nesteruk.util;

import java.util.*;
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

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
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

    public static <T> String join(T[] entities, AppenderTextProvider<T> appenderTextProvider, String delimiter) {
        return join(Arrays.asList(entities), appenderTextProvider, delimiter);
    }

    public static <T> String join(Collection<T> entities, AppenderTextProvider<T> appenderTextProvider, String delimiter) {
        return join(new StringBuilder(), entities, appenderTextProvider, delimiter).toString();
    }

    public static <T> StringBuilder join(StringBuilder sb, T[] entities, AppenderTextProvider<T> appenderTextProvider, String delimiter) {
        return join(sb, Arrays.asList(entities), appenderTextProvider, delimiter);
    }

    public static <T> StringBuilder join(StringBuilder sb, Collection<T> entities, AppenderTextProvider<T> appenderTextProvider, String delimiter) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        boolean firstTime = true;
        for (T entity : entities) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            appenderTextProvider.appendText(sb, entity);
        }
        return sb;
    }

    public interface AppenderTextProvider<T> {
        void appendText(StringBuilder sb, T entity);
    }

    public static abstract class SimpleAppenderTextProvider<T> implements AppenderTextProvider<T> {

        protected abstract String getTextRepresentation(T entity);

        @Override
        public void appendText(StringBuilder sb, T entity) {
            sb.append(getTextRepresentation(entity));
        }
    }

}
