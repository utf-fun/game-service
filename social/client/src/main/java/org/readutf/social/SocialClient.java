package org.readutf.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.readutf.social.status.SessionUpdatePayload;
import org.readutf.social.utils.ReconnectingWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class SocialClient {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SocialClient.class);

    private final String hostname;
    private final UUID serverId;
    private final ReconnectingWebSocketClient webSocketClient;

    private List<UUID> lastOnline = new ArrayList<>();

    public SocialClient(String hostname) {
        this.hostname = hostname;
        this.serverId = UUID.randomUUID();
        this.webSocketClient = ReconnectingWebSocketClient.builder(URI.create("ws://" + hostname + ":8080/api/v1/session"))
                .maxReconnectAttempts(-1)
                .initialReconnectDelay(5000)
                .build();

        webSocketClient.connect();
    }

    public void syncOnline(
            Supplier<List<UUID>> onlinePlayersSupplier, long periodMs) {

        List<UUID> online = onlinePlayersSupplier.get();

        Set<UUID> joined = new HashSet<>(online);
        lastOnline.forEach(joined::remove);

        Set<UUID> disconnected = new HashSet<>(online);
        lastOnline.forEach(disconnected::remove);

        lastOnline = online;

        try {
            webSocketClient.send(objectMapper.writeValueAsString(new SessionUpdatePayload(serverId, joined, disconnected)));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session update payload", e);
        }
    }

    public void playerConnect(UUID playerId) {
        try {
            webSocketClient.send(objectMapper.writeValueAsString(new SessionUpdatePayload(serverId, Collections.singleton(playerId), Collections.emptyList())));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session update payload", e);
        }
    }

    public void playerDisconnect(UUID playerId) {
        try {
            webSocketClient.send(objectMapper.writeValueAsString(new SessionUpdatePayload(serverId, Collections.emptyList(), Collections.singleton(playerId))));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session update payload", e);
        }
    }
}
