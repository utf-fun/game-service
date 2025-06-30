package org.readutf.gameservice.api.routes;

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
        String filterTag = ctx.queryParam("tag");
        if(filterTag != null) {
            // Filter servers by tag
            ctx.json(serverManager.getActiveServers().stream()
                .filter(server -> server.getTags().stream().anyMatch(s -> s.equalsIgnoreCase(filterTag)))
                .toList());

            ctx.status(200);
            return;
        }

        ctx.contentType("application/json");
        ctx.json(serverManager.getActiveServers());
        ctx.status(200);
    }
}
