package org.readutf.gameservice.client.heartbeat;

import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.capacity.CapacitySupplier;
import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.hermes.netty.NettyClientPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class HeartbeatTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatTask.class);
    private final @NotNull Supplier<NettyClientPlatform> clientSupplier;
    private final @NotNull CapacitySupplier capacitySupplier;

    public HeartbeatTask(@NotNull Supplier<NettyClientPlatform> clientSupplier, @NotNull CapacitySupplier capacitySupplier) {
        this.clientSupplier = clientSupplier;
        this.capacitySupplier = capacitySupplier;
    }

    @Override
    public void run() {
        NettyClientPlatform client = clientSupplier.get();
        if (client == null || !client.isConnected()) {
            return;
        }

        try {
            client.sendPacket(new HeartbeatPacket(capacitySupplier.getCapacity()));
        } catch (Exception e) {
            log.error("Failed to send heartbeat", e);
        }

    }
}
