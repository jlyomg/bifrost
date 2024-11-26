package com.dataour.bifrost.common.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 LinkedHashMap 实现的一个简单的 LRU 缓存
 *
 * @param <K>
 * @param <V>
 */
public class EvictingMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public EvictingMap(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

//    public static void main(String[] args) {
//        EvictingMap<Integer, String> evictingMap = new EvictingMap<>(10);
//
//        for (int i = 0; i < 100; i++) {
//            evictingMap.put(i, "Value " + i);
//        }
//        System.out.println(evictingMap.size()); // 超过阈值的最老的键已被淘汰
//    }
}