package org.readutf.gameservice.social;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.party.PartyManager;
import org.readutf.gameservice.social.party.api.PartyActionHandler;
import org.readutf.gameservice.social.party.api.PartyFunction;
import org.readutf.gameservice.social.session.api.GetAllSessionsHandler;
import org.readutf.gameservice.social.session.api.GetSessionHandler;
import org.readutf.gameservice.social.session.api.SessionSocketHandler;

public class SocialService {

    private final @NotNull Javalin javalin;

    public SocialService() {
        PartyManager partyManager = new PartyManager();
        this.javalin = setupJavalin(partyManager);

        javalin.start(8080);
    }

    private static Javalin setupJavalin(PartyManager partyManager) {
        return Javalin.create(config -> {
                    config.showJavalinBanner = false;
                    config.useVirtualThreads = true;
                })
                .get("/api/v1/session/", new GetAllSessionsHandler())
                .get("/api/v1/session/{player}", new GetSessionHandler())
                .get("/api/v1/party/{owner}", PartyFunction.getParty.toHandler(partyManager))
                .put("/api/v1/party/{owner}", PartyFunction.createParty.toHandler(partyManager))
                .put("/api/v1/party/{owner}/invite/{player}", PartyFunction.invite.toHandler(partyManager))
                .put("/api/v1/party/{player}/join/{owner}", PartyFunction.joinParty.toHandler(partyManager))
                .put("/api/v1/party/{player}/open", PartyFunction.open.toHandler(partyManager))
                .ws("/api/v1/session", new SessionSocketHandler().toConfigConsumer());
    }

    public static void main(String[] args) {
        new SocialService();
    }

}
