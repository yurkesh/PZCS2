package edu.kpi.nesteruk.misc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Anatolii on 2016-03-23.
 */
public class OneToOneMapper<K, V> {

    private final Map<K, V> map = new HashMap<>();
    private final Map<V, K> inverse = new HashMap<>();

    public void add(K key, V value) {
        checkKeyAndValue(key, value);

        if(map.containsKey(key) || inverse.containsKey(value)) {
            throw new IllegalArgumentException(String.format(Locale.US, "Already has mapping. K->V({%s -> %s}), V->K({%s -> %s})", key, map.get(key), value, inverse.get(value)));
        }
        map.put(key, value);
        inverse.put(value, key);
    }

    public void remove(K key, V value) {
        checkKeyAndValue(key, value);

        V removedValue = map.remove(key);
        if(!value.equals(removedValue)) {
            throw new IllegalArgumentException(String.format(Locale.US, "Provided key = %s; provided value = %s; stored value = %s", key, value, removedValue));
        }
        K removedKey = inverse.remove(value);
        if(!key.equals(removedKey)) {
            throw new IllegalArgumentException(String.format(Locale.US, "Provided value = %s; provided key = %s; stored key = %s", value, key, removedKey));
        }
    }

    public V removeByKey(K key) {
        V removedValue = map.remove(Objects.requireNonNull(key));
        inverse.remove(Objects.requireNonNull(removedValue));
        return removedValue;
    }

    public V getByKey(K key) {
        return map.get(Objects.requireNonNull(key));
    }

    public K getByValue(V value) {
        return inverse.get(Objects.requireNonNull(value));
    }

    public void clear() {
        map.clear();
        inverse.clear();
    }

    private void checkKeyAndValue(K key, V value) {
        if(key == null) {
            throw new NullPointerException("Null key for value = " + value);
        }
        if(value == null) {
            throw new NullPointerException("Null value for key = " + key);
        }
    }

    public void replace(K key, V oldValue, V newValue) {
        remove(key, oldValue);
        add(key, newValue);
    }
}
