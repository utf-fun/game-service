package org.readutf.matchmaker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.readutf.matchmaker.data.ListQueuesResponse;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Test {

    public static void main(String[] args) throws IOException {

        QueueSocket queueSocket = new QueueSocket(URI.create("ws://localhost:8080/api/v1/queue/tnttag/join"), new ObjectMapper());

        CompletableFuture<JsonNode> response1 = queueSocket.join(List.of(UUID.randomUUID()));
        CompletableFuture<JsonNode> response2 = queueSocket.join(List.of(UUID.randomUUID()));

        JsonNode node1 = response1.join();
        JsonNode node2 = response2.join();

        System.out.println(node1);
        System.out.println(node2);

        queueSocket.disconnect();
    }

}
