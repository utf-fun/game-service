package org.readutf.gameservice.social.session.api;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.session.PlayerSession;
import org.readutf.gameservice.social.session.SessionManager;

import java.util.UUID;

public class GetSessionHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) {
        String playerId = ctx.pathParam("player");

        UUID uuid = UUID.fromString(playerId);

        PlayerSession session = SessionManager.getSession(uuid);
        if(session == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            return;
        }
        ctx.status(200);
        ctx.json(session);
    }
}
