package org.readutf.gameservice.social.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.utils.WebsocketHandler;
import org.readutf.social.status.StatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatusSocket implements WebsocketHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(StatusSocket.class);
    private final Map<String, UUID> sessionToServer = new ConcurrentHashMap<>();

    private final @NotNull StatusManager statusManager;

    public StatusSocket(StatusManager statusManager) {
        this.statusManager = statusManager;
        ;
    }

    @Override
    public void onMessage(@NotNull WsMessageContext wsMessageHandler) {
        String message = wsMessageHandler.message();

        StatusMessage statusMessage;
        try {
            statusMessage = mapper.readValue(message, StatusMessage.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing StatusMessage", e);
            return;
        }

        sessionToServer.put(wsMessageHandler.sessionId(), statusMessage.serverId());
        statusManager.handleStatusUpdate(statusMessage);
    }

    @Override
    public void onClose(@NotNull WsCloseContext wsCloseHandler) {
        UUID serverId = sessionToServer.remove(wsCloseHandler.sessionId());
        onlinePlayers.remove(serverId);
    }
}
