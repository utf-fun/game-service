package org.readutf.gameservice.client;

import org.jetbrains.annotations.Blocking;
import org.readutf.gameservice.common.SharedKryo;
import org.readutf.hermes.kryo.KryoPacketCodec;
import org.readutf.hermes.netty.NettyClientPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GameServiceClient.class);
    private NettyClientPlatform nettyClient;
    private AtomicBoolean running = new AtomicBoolean(true);

    public GameServiceClient() {
    }

    @Blocking
    public void startBlocking(InetSocketAddress address) throws Exception {
        while (running.get()) {
            try {
                this.nettyClient = new NettyClientPlatform(new KryoPacketCodec(SharedKryo::createKryo));

                nettyClient.connect(address, 5000);


                log.info("Connected, waiting for shutdown...");
                nettyClient.awaitShutdown();
                log.info("Shut down");


                this.nettyClient = null;
            } catch (Exception e) {
                log.error("Failed to connect to Game Service, restarting in 5 seconds");
                Thread.sleep(5000); // Retry after 5 seconds
            }
        }

        log.info("Client shut down");
    }

    public void stop() {
        running.set(false);
        if (nettyClient != null) {
            nettyClient.shutdown();
        }
    }

}
