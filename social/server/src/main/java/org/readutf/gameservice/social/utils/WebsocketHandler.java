package org.readutf.gameservice.social.utils;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface WebsocketHandler {

    default void onConnect(@NotNull WsConnectContext wsConnectHandler) {}

    default void onMessage(@NotNull WsMessageContext wsMessageHandler) {}

    default void onBinaryMessage(@NotNull WsBinaryMessageContext wsBinaryMessageHandler) {}

    default void onClose(@NotNull WsCloseContext wsCloseHandler) {}

    default void onError(@NotNull WsErrorContext wsErrorHandler) {}

    default Consumer<WsConfig> toConfigConsumer() {
        return wsConfig -> {
            wsConfig.onConnect(this::onConnect);
            wsConfig.onMessage(this::onMessage);
            wsConfig.onBinaryMessage(this::onBinaryMessage);
            wsConfig.onClose(this::onClose);
            wsConfig.onError(this::onError);
        };
    }
}
