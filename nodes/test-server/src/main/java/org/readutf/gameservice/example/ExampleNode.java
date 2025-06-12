package org.readutf.gameservice.example;

import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.ReconnectingGameService;
import org.readutf.gameservice.client.platform.DockerPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleNode {

    private static final Logger log = LoggerFactory.getLogger(ExampleNode.class);

    public static void main(String[] args) throws InterruptedException {

        ReconnectingGameService reconnecting = GameServiceClient.reconnecting("orchestrator:50052", new DockerPlatform(), () -> 0.5f);
    }

}
