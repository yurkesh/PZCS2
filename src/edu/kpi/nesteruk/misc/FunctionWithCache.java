package edu.kpi.nesteruk.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Yurii on 2016-05-16.
 */
public class FunctionWithCache<T, R> implements Function<T, R> {

    private final Map<T, R> cache = new HashMap<>();
    private final Function<T, R> function;

    public FunctionWithCache(Function<T, R> function) {
        this.function = function;
    }

    @Override
    public R apply(T input) {
        return cache.computeIfAbsent(input, function);
    }
}
