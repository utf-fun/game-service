package org.readutf.gameservice;

import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.platform.DockerResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTest.class);

    public static void main(String[] args) throws Exception {

        GameServiceClient serviceClient = new GameServiceClient(new DockerResolver(), List.of("test", "integration"));


        serviceClient.startBlocking(new InetSocketAddress("gameservice", 50052));
    }

}
