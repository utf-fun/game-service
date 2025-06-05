package org.readutf.gameservice.api;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteLogger implements Handler {
    private static final Logger log = LoggerFactory.getLogger(RouteLogger.class);

    @Override
    public void handle(@NotNull Context ctx) {

        log.info("[{}] {} - {} - {}",
                ctx.method(),
                ctx.req().getRequestURI(),
                ctx.req().getRemoteAddr(),
                HttpStatus.forStatus(ctx.res().getStatus())
        );
    }
}
