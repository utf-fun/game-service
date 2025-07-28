package org.readutf.matchmaker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.readutf.matchmaker.data.QueueJoinRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class QueueSocket extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(QueueSocket.class);

    private final @NotNull ObjectMapper objectMapper = new ObjectMapper();
    private final @NotNull Map<UUID, CompletableFuture<JsonNode>> pendingRequests = new ConcurrentHashMap<>();

    public QueueSocket(URI serverUri, @NotNull ObjectMapper objectMapper) {
        super(serverUri);
        connect();
    }

    public CompletableFuture<JsonNode> join(List<UUID> team) {

        UUID entryId = UUID.randomUUID();

        try {
            String request = objectMapper.writeValueAsString(new QueueJoinRequest(entryId, team));
            send(request);
            CompletableFuture<JsonNode> future = new CompletableFuture<>();
            pendingRequests.put(entryId, future);

            return future;
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public void disconnect() {
        try {
            closeBlocking();
        } catch (InterruptedException e) {
            log.error("Disconnect interrupted", e);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connected to the queue server at {}", getURI());
    }

    @Override
    public void onMessage(String s) {

        try {
            JsonNode jsonNode = objectMapper.readTree(s);
            if (jsonNode.has("context")) {
                String context = jsonNode.get("context").asText();
                UUID entryId = UUID.fromString(context);
                pendingRequests.get(entryId).complete(jsonNode);
                pendingRequests.remove(entryId);
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to parse message: {}", s, e);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {}

    @Override
    public void onError(Exception e) {}
}
