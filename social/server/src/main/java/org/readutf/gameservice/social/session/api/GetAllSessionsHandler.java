package org.readutf.gameservice.social.session.api;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.session.PlayerSession;
import org.readutf.gameservice.social.session.SessionManager;

import java.util.List;

public class GetAllSessionsHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        List<PlayerSession> sessions = SessionManager.getAllSessions();

        ctx.status(200);
        ctx.json(sessions);
    }
}
