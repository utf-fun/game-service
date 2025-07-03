package org.readutf.gameservice.common.packet;

import org.readutf.hermes.packet.Packet;

public class HeartbeatPacket extends Packet<Void> {

    private final float capacity;

    public HeartbeatPacket(float capacity) {
        super(false);
        this.capacity = capacity;
    }

    public float getCapacity() {
        return capacity;
    }
}
