package org.readutf.gameservice.social.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.utils.WebsocketHandler;
import org.readutf.social.status.SessionUpdatePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionSocket implements WebsocketHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SessionSocket.class);
    private final Map<String, UUID> sessionToServer = new ConcurrentHashMap<>();

    @Override
    public void onConnect(@NotNull WsConnectContext wsConnectHandler) {
        log.info("Session connected: {}", wsConnectHandler.sessionId());
    }

    @Override
    public void onMessage(@NotNull WsMessageContext wsMessageHandler) {
        String message = wsMessageHandler.message();

        SessionUpdatePayload sessionUpdatePayload;
        try {
            sessionUpdatePayload = mapper.readValue(message, SessionUpdatePayload.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing StatusMessage", e);
            return;
        }

        sessionToServer.put(wsMessageHandler.sessionId(), sessionUpdatePayload.serverId());
        for (UUID uuid : sessionUpdatePayload.joined()) {
            SessionManager.onJoin(uuid, sessionUpdatePayload.serverId());
        }
        for (UUID uuid : sessionUpdatePayload.disconnected()) {
            SessionManager.onLeave(uuid, sessionUpdatePayload.serverId());
        }
    }

    @Override
    public void onClose(@NotNull WsCloseContext wsCloseHandler) {
        UUID serverId = sessionToServer.remove(wsCloseHandler.sessionId());
        SessionManager.serverShutdown(serverId);
    }
}
