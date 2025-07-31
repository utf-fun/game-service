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

public class SocialClient {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SocialClient.class);

    private final String hostname;
    private final UUID serverId;
    private final ReconnectingWebSocketClient webSocketClient;

    private Set<UUID> lastOnline;

    public SocialClient(String hostname) {
        this.hostname = hostname;
        this.serverId = UUID.randomUUID();
        this.lastOnline = new HashSet<>();
        this.webSocketClient = ReconnectingWebSocketClient.builder(URI.create("ws://" + hostname + ":8080/api/v1/session"))
                .maxReconnectAttempts(-1)
                .initialReconnectDelay(5000)
                .onOpen((client, data) -> {
                    List<UUID> newPlayers = List.copyOf(lastOnline);
                    lastOnline = new HashSet<>();
                    syncOnline(newPlayers);
                    log.info("WebSocket connection established to Social Service at {}", hostname);
                })
                .build();

        webSocketClient.connect();
    }

    public void syncOnline(List<UUID> online) {

        Set<UUID> joined = new HashSet<>(online);
        lastOnline.forEach(joined::remove);

        Set<UUID> disconnected = new HashSet<>(lastOnline);
        online.forEach(disconnected::remove);

        System.out.println("previous: " + lastOnline);
        System.out.println("new: " + online);
        System.out.println("joined: " + joined);
        System.out.println("disconnected: " + disconnected);

        lastOnline = new HashSet<>(online);

        try {
            webSocketClient.send(objectMapper.writeValueAsString(new SessionUpdatePayload(serverId, joined, disconnected)));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session update payload", e);
        }
    }

    public void playerConnect(UUID playerId) {
        try {
            lastOnline.add(playerId);
            webSocketClient.send(objectMapper.writeValueAsString(new SessionUpdatePayload(serverId, Collections.singleton(playerId), Collections.emptyList())));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session update payload", e);
        }
    }

    public void playerDisconnect(UUID playerId) {
        try {
            lastOnline.remove(playerId);
            webSocketClient.send(objectMapper.writeValueAsString(new SessionUpdatePayload(serverId, Collections.emptyList(), Collections.singleton(playerId))));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize session update payload", e);
        }
    }
}
