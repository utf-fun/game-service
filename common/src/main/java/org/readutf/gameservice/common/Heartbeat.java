package org.readutf.gameservice.common;

public class Heartbeat {

    private final long timestamp;
    private final float capacity;

    public Heartbeat(long timestamp, float capacity) {
        this.timestamp = timestamp;
        this.capacity = capacity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getCapacity() {
        return capacity;
    }
}
