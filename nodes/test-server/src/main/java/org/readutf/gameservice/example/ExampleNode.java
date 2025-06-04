package org.readutf.gameservice.example;

import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.platform.DockerPlatform;

public class ExampleNode {

    public static void main(String[] args) throws InterruptedException {

        GameServiceClient.reconnecting("[::1]:50052", new DockerPlatform());

        Thread.sleep(5000);

    }

}
