package org.readutf.gameservice.social;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

public class SocialServer {

    private final @NotNull Javalin javalin;

    public SocialServer(String hostname) {

        this.javalin = setupJavalin();

    }

    private static Javalin setupJavalin() {
        return Javalin.create();
    }

}
