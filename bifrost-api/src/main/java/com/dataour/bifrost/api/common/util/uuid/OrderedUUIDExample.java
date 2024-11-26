package com.dataour.bifrost.api.common.util.uuid;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrderedUUIDExample {
    private static AtomicLong counter = new AtomicLong(0L);

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            UUID orderedUUID = generateOrderedUUID();
            System.out.println(orderedUUID.toString().replace("-","")+" "+orderedUUID.toString().replace("-","").length());
        }
    }

    public static UUID generateOrderedUUID() {
        long timestamp = System.currentTimeMillis();
        long count = counter.getAndIncrement();
        return new UUID(timestamp, count);
    }
}
