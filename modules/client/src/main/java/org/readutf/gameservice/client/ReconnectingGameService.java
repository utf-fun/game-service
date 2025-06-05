package org.readutf.gameservice.client;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.exception.GameServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectingGameService implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReconnectingGameService.class);

    @NotNull
    private final Supplier<GameServiceClient> clientCreator;

    private GameServiceClient client;
    private boolean active = true;

    public ReconnectingGameService(Supplier<GameServiceClient> clientCreator) {
        this.clientCreator = clientCreator;
    }

    @Override
    public void run() {
        while (active) {
            try {
                client = clientCreator.get();
                client.startBlocking();
                client = null;
            } catch (GameServiceException e) {
                log.error("A connection error occurred", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        active = false;
        if (client != null) {
            client.disconnect();
            log.info("Shut down GameServiceClient");
        }
    }

}