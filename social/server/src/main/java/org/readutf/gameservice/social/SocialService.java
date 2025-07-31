package org.readutf.gameservice.social;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.session.SessionSocket;

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
        }).ws("/api/v1/session", new SessionSocket().toConfigConsumer());
    }

    public static void main(String[] args) {
        new SocialService();
    }

}
