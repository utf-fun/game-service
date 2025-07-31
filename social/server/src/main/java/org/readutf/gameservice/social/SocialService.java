package org.readutf.gameservice.social;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.session.api.GetAllSessionsHandler;
import org.readutf.gameservice.social.session.api.GetSessionHandler;
import org.readutf.gameservice.social.session.api.SessionSocketHandler;

public class SocialService {

    private final @NotNull Javalin javalin;

    public SocialService() {
        this.javalin = setupJavalin();

        javalin.start(8080);
    }

    private static Javalin setupJavalin() {
        return Javalin.create(config -> {
                    config.showJavalinBanner = false;
                    config.useVirtualThreads = true;
                })
                .get("/api/v1/session/", new GetAllSessionsHandler())
                .get("/api/v1/session/{player}", new GetSessionHandler())
                .ws("/api/v1/session", new SessionSocketHandler().toConfigConsumer());
    }

    public static void main(String[] args) {
        new SocialService();
    }

}
