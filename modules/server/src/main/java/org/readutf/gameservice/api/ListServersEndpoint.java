package org.readutf.gameservice.api;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.server.ServerManager;

public class ListServersEndpoint implements Handler {

    private @NotNull final ServerManager serverManager;

    public ListServersEndpoint(@NotNull ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        ctx.contentType("application/json");
        ctx.json(serverManager.getActiveServers());
        ctx.status(200);
    }
}
