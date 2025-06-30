package org.readutf.gameservice.api.routes;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.server.ServerManager;

import java.util.UUID;

public class GetServerEndpoint implements Handler {

    private final ServerManager serverManager;

    public GetServerEndpoint(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String serverIdPath = ctx.pathParam("id");
        UUID serverId;
        try {
           serverId = UUID.fromString(serverIdPath);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid server ID");
            return;
        }

        Server server = serverManager.getServerById(serverId);
        if (server == null) {
            ctx.status(404).result("Server not found");
            return;
        }

        ctx.contentType("application/json");
        ctx.json(server);
        ctx.status(200);
    }
}
