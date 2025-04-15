package org.master.diploma.git.support;

import java.util.*;

public class TwoOrderedMap<K, V> implements Map<K, V> {

    private Map<K, V> map = new HashMap<>();
    private Map<V, K> obr = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        map.put(key, value);
        obr.put(value, key);
        return value;
    }

    @Override
    public V remove(Object key) {
        V value = map.remove(key);
        if (Objects.nonNull(value) && obr.containsKey(value)) {
            obr.remove(value);
        }
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (var mp : m.entrySet()) {
            this.put(mp.getKey(), mp.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
        obr.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public K getKey(V value) {
        return obr.get(value);
    }
}
